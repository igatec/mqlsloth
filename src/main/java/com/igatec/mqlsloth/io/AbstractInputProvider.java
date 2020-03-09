package com.igatec.mqlsloth.io;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.AbstractCIName;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.iface.io.InputProvider;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.util.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractInputProvider implements InputProvider {

    private final int namesQueueCapacity;
    private final int ciStringsQueueCapacity;
    private final int cisQueueCapacity;

    protected AbstractInputProvider(int namesQueueCapacity, int ciStringsQueueCapacity, int cisQueueCapacity) {
        this.namesQueueCapacity = namesQueueCapacity;
        this.ciStringsQueueCapacity = ciStringsQueueCapacity;
        this.cisQueueCapacity = cisQueueCapacity;
    }

    private interface Session {
        void run();

        ObjectStreamReader<AbstractCI> getReader();
    }

    /*
    This method may be overridden by a child class if it cannot divide CI providing into to separate methods:
    getCIStringByName(CIFullName name) and deserialize(String ciString).
    If CI providing can be divided, method should return null
    IF CI does not exist, it should return CIStub instance.
     */
    protected AbstractCI getCIByNameDirectly(CIFullName fullName) throws SlothException {
        return null;
    }

    private AbstractCI getCIByName(CIFullName fullName) throws SlothException {
        AbstractCI ci = getCIByNameDirectly(fullName);
        if (ci != null) {
            if (ci instanceof CIStub)
                return null;
            return ci;
        }
        String ciString = getCIStringByName(fullName);
        try {
            return (ciString != null) ? deserialize(ciString) : null;
        } catch (ParserException e) {
            throw new SlothException(String.format("Could not parse %s", fullName), e);
        }
    }

    private class SyncSession implements Session {

        private final ObjectStreamReader<CIFullName> namesReader;

        SyncSession(ObjectStreamReader<CIFullName> namesReader) {
            this.namesReader = namesReader;
        }

        @Override
        public void run() {
        }

        @Override
        public ObjectStreamReader<AbstractCI> getReader() {
            return new ObjectStreamReader<AbstractCI>() {
                @Override
                public AbstractCI next() throws SlothException {
                    CIFullName name = namesReader.next();
                    AbstractCI ci = getCIByName(name);
                    if (ci == null)
                        return null;
                    if (!ci.getCIFullName().equals(name))
                        throw new SlothException(String.format("ERROR: CI name conflict. CI file '%s' does not " +
                                "contain definition for this CI", name));
                    return ci;
                }

                @Override
                public boolean hasNext() {
                    return namesReader.hasNext();
                }
            };
        }
    }

    private class MultithreadSession implements Session {

        private final BlockingQueue<ObjectContainer<CIFullName>> names = new LinkedBlockingQueue<>(namesQueueCapacity);
        private final BlockingQueue<ObjectContainer<String>> ciStrings = new LinkedBlockingQueue<>(ciStringsQueueCapacity);
        private final BlockingQueue<ObjectContainer<AbstractCI>> cis = new LinkedBlockingQueue<>(cisQueueCapacity);
        private final ObjectStreamReader<CIFullName> namesReader;
        private volatile int counter = 0;
        private final Object lock = new Object();

        MultithreadSession(ObjectStreamReader<CIFullName> fullNamesReader) {
            namesReader = fullNamesReader;
        }

        @Override
        public void run() {

            ExecutorService service1 = Executors.newSingleThreadExecutor();
            service1.submit(() -> {
                try {
                    while (namesReader.hasNext()) {
                        try {
                            CIFullName next;
                            synchronized (lock) {
                                next = namesReader.next();
                                counter++;
                            }
                            names.put(new ObjectContainer<>(next));
                        } catch (SlothException ex) {
                            names.put(new ObjectContainer<>(ex));
                            break;
                        }
                        if (!namesReader.hasNext()) {
                            names.put(new EndOfCollectionStub<>());
                        }
                    }
                } catch (InterruptedException e) {
                    try {
                        names.put(new ObjectContainer<>(e));
                    } catch (InterruptedException e1) {
                    }
                }
            });
            service1.shutdown();

            ExecutorService service2 = Executors.newSingleThreadExecutor();
            service2.submit(() -> {
                ObjectContainer<CIFullName> nameContainer;
                try {
                    while (!(nameContainer = names.take()).isStub()) {
                        try {
                            String ciString = getCIStringByName(nameContainer.value());
                            ciStrings.put(new ObjectContainer<>(ciString));
                        } catch (SlothException ex) {
                            ciStrings.put(new ObjectContainer<>(ex));
                            break;
                        }
                    }
                    ciStrings.put(new EndOfCollectionStub<>());
                } catch (InterruptedException e) {
                    try {
                        ciStrings.put(new ObjectContainer<>(e));
                    } catch (InterruptedException e1) {
                    }
                }
            });
            service2.shutdown();

            ExecutorService service3 = Executors.newSingleThreadExecutor();
            service3.submit(() -> {
                ObjectContainer<String> ciStringContainer;
                try {
                    while (!(ciStringContainer = ciStrings.take()).isStub()) {
                        try {
                            String ciString = ciStringContainer.value();
                            AbstractCI ci = ciString == null ? null : deserialize(ciStringContainer.value());

                            // TODO add check that this ci corresponds requested objectName

                            cis.put(new ObjectContainer<>(ci));
                        } catch (Exception ex) {
                            cis.put(new ObjectContainer<>(ex));
                            break;
                        }
                    }
                    cis.put(new EndOfCollectionStub<>());
                } catch (InterruptedException e) {
                    try {
                        cis.put(new ObjectContainer<>(e));
                    } catch (InterruptedException e1) {
                    }
                }
            });
            service3.shutdown();
        }

        @Override
        public ObjectStreamReader<AbstractCI> getReader() {
            return new ObjectStreamReader<AbstractCI>() {

                @Override
                public boolean hasNext() {
                    synchronized (lock) {
                        return !(!namesReader.hasNext() && counter == 0);
                    }
                }

                @Override
                public AbstractCI next() throws SlothException {
                    ObjectContainer<AbstractCI> nextContainer;
                    try {
                        nextContainer = cis.take();
                        synchronized (lock) {
                            counter--;
                        }
                    } catch (InterruptedException e) {
                        throw new SlothException(e);
                    }
                    if (nextContainer.isStub()) {
                        throw new NoSuchElementException();
                    }
                    Exception ex = nextContainer.getException();
                    if (ex != null)
                        throw new SlothException(ex);
                    return nextContainer.value();
                }
            };
        }
    }

    /*
        Template method
     */
    protected abstract String getCIStringByName(CIFullName fullName) throws SlothException;

    /*
        Template method
     */
    protected abstract AbstractCI deserialize(String ciString) throws ParserException;

    @Override
    public ObjectStreamReader<AbstractCI> getCIDefinitions(ObjectStreamReader<CIFullName> fullNamesReader) {
        Session session = new SyncSession(fullNamesReader);
        session.run();
        return session.getReader();
    }

    @Override
    public AbstractCI getCIDefinition(CIFullName fullName) throws SlothException {
        return getCIDefinitions(new ObjectStreamReaderImpl<>(fullName)).next();
    }

    @Override
    public ObjectStreamReader<AbstractCI> getAllCIDefinitions() {
        ObjectStreamReader<CIFullName> allNames = getAllCINames();
        return getCIDefinitions(allNames);
    }

    @Override
    public ObjectStreamReader<AbstractCI> getCIDefinitions(String name) {
        Collection<CIFullName> fullNames = getExistingFullNamesByName(name);
        return getCIDefinitions(new ObjectStreamReaderImpl<>(fullNames));
    }

    @Override
    public ObjectStreamReader<AbstractCI> getCIDefinitionsByPattern(String namePattern) throws SlothException {
        return getCIDefinitions(getCINamesByPattern(namePattern));
    }

    @Override
    public ObjectStreamReader<AbstractCI> getCIDefinitionsByPattern(CIFullName fullNamePattern) throws SlothException {
        Collection<CIFullName> patterns = new LinkedList<>();
        patterns.add(fullNamePattern);
        return getCIDefinitionsByPatterns(patterns);
    }

    @Override
    public ObjectStreamReader<AbstractCI> getCIDefinitionsByPatterns(Collection<CIFullName> fullNamesPatterns)
            throws SlothException {
        return getCIDefinitions(getCINamesByPatterns(fullNamesPatterns));
    }

    @Override
    public ObjectStreamReader<CIFullName> getCINamesByPattern(String namePattern) throws SlothException {
        Collection<CIFullName> fullNamePatterns = getFullNamePatternsByNamePattern(namePattern);
        return getCINamesByPatterns(fullNamePatterns);
    }

    @Override
    public abstract ObjectStreamReader<CIFullName> getCINamesByPattern(CIFullName fullNamePattern) throws SlothException;

    @Override
    public ObjectStreamReader<CIFullName> getCINamesByPatterns(Collection<CIFullName> fullNamesPatterns)
            throws SlothException {
        List<ObjectStreamReader<CIFullName>> list = new LinkedList<>();

        List<SlothAdminType> types = SlothAdminType.getSorted();
        List<SlothAdminType> revTypes = Arrays.asList(SlothAdminType.TRIGGER, SlothAdminType.NUMBER_GENERATOR,
                SlothAdminType.OBJECT_GENERATOR);
        SlothAdminType adminType;
        AbstractCIName abstractCIName;
        String revision;
        CIFullName ciFullName;

        for (CIFullName fullName : fullNamesPatterns) {
            adminType = fullName.getAdminType();
            abstractCIName = fullName.getCIName();
            if (adminType.getKey().equalsIgnoreCase("All")) {
                if (fullName.getCIName() instanceof BusCIName) {
                    revision = ((BusCIName) fullName.getCIName()).getRevision();
                    for (SlothAdminType revType : revTypes) {
                        ciFullName = new CIFullName(revType, new BusCIName(revType.getKey(),
                                ((BusCIName) fullName.getCIName()).getName(), revision));
                        list.add(getCINamesByPattern(ciFullName));
                    }
                } else {
                    for (SlothAdminType type : types) {
                        if (type.getKey().startsWith("eService")) {
                            ciFullName = new CIFullName(type, new BusCIName(type.getKey(),
                                     fullName.getCIName().toString(), "*"));
                        } else {
                            ciFullName = new CIFullName(type, abstractCIName);
                        }
                        list.add(getCINamesByPattern(ciFullName));
                    }
                }
            } else {
                list.add(getCINamesByPattern(fullName));
            }
        }
        return new ComposedObjectStreamReader<>(list);
    }

    protected abstract Collection<CIFullName> getExistingFullNamesByName(String name);

    protected abstract Collection<CIFullName> getFullNamePatternsByNamePattern(String namePattern);

}


