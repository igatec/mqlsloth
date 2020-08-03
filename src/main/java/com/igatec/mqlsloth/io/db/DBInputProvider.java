package com.igatec.mqlsloth.io.db;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.AdminObjectCI;
import com.igatec.mqlsloth.ci.PageCI;
import com.igatec.mqlsloth.ci.PolicyCI;
import com.igatec.mqlsloth.ci.ProgramCI;
import com.igatec.mqlsloth.ci.RelationshipCI;
import com.igatec.mqlsloth.ci.constants.MqlAdminType;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.AbstractCIName;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.context.ApplicationContext;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.framework.Page;
import com.igatec.mqlsloth.iface.kernel.IMqlCommand;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.io.AbstractInputProviderPatternInterpreter;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.parser.Parser;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.script.MqlKeywords;
import com.igatec.mqlsloth.script.MqlUtil;
import com.igatec.mqlsloth.util.CIStub;
import com.igatec.mqlsloth.util.ObjectStreamReader;
import com.igatec.mqlsloth.util.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.igatec.mqlsloth.script.MqlKeywords.M_LIST;
import static com.igatec.mqlsloth.script.MqlKeywords.M_ON;
import static com.igatec.mqlsloth.script.MqlKeywords.M_PROGRAM;
import static com.igatec.mqlsloth.script.MqlKeywords.M_PROPERTY;
import static com.igatec.mqlsloth.script.MqlKeywords.M_TABLE;
import static com.igatec.mqlsloth.script.MqlKeywords.M_TO;

public class DBInputProvider extends AbstractInputProviderPatternInterpreter {
    public static final int DEFAULT_NAMES_QUEUE_CAPACITY = 10;
    public static final int DEFAULT_CI_STRINGS_QUEUE_CAPACITY = 10;
    public static final int DEFAULT_CIS_QUEUE_CAPACITY = 10;
    public static final int REQUIRED_PART_OF_MQL_RESULT_NUMBER = 3;
    private static final List<SlothAdminType> ADMIN_TYPES = new ArrayList<>();

    static {
        ADMIN_TYPES.addAll(SlothAdminType.getSorted());
    }

    private final IMqlCommand mqlCommand;
    private final Session session;

    public DBInputProvider(Session session) throws Exception {
        super(DEFAULT_NAMES_QUEUE_CAPACITY, DEFAULT_CI_STRINGS_QUEUE_CAPACITY, DEFAULT_CIS_QUEUE_CAPACITY);
        this.session = session;
        this.mqlCommand = session.getCommand();
    }

    @Override
    protected String getCIStringByName(CIFullName ciFullName) throws SlothException {
        return loadCIStringByName(ciFullName);
    }

    private String loadCIStringByName(CIFullName ciFullName) {
        String mqlAdminType = ciFullName.getAdminType().getMqlKey();
        AbstractCIName ciName = ciFullName.getCIName();
        String result;
        try {
            mqlCommand.execute(MqlKeywords.M_QOUTE, MqlKeywords.M_ON);
            if (SlothAdminType.isBus(ciFullName.getAdminType())) {
                BusCIName busName = (BusCIName) ciName;
                result = mqlCommand.execute(true,
                        MqlKeywords.M_TEMP, MqlKeywords.M_QUERY, mqlAdminType,
                        busName.getType(), busName.getName(), busName.getRevision(), MqlKeywords.M_SELECT,
                        MqlKeywords.M_TYPE,
                        MqlKeywords.M_NAME,
                        MqlKeywords.M_REVISION,
                        MqlKeywords.M_DESCRIPTION,
                        MqlKeywords.M_ATTRIBUTE + ".*",
                        "from.to.type",
                        "from.to.name",
                        "from.to.revision"
                );
                if (result.isEmpty()) {
                    return null;
                }
            } else {
                if (mqlAdminType.equals("table")) {
                    result = mqlCommand.execute(MqlKeywords.M_PRINT, mqlAdminType, ((StringCIName) ciName).getName(), "system");
                } else {
                    result = mqlCommand.execute(MqlKeywords.M_PRINT, mqlAdminType, ((StringCIName) ciName).getName());
                }
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    @Override
    protected AbstractCI deserialize(String ciString) throws ParserException {
        try {
            Parser<AbstractCI> parser = MqlParser.fromString(ciString);
            AbstractCI abstractCI = parser.parse();
            return abstractCI;
        } catch (ParserException e) {
            throw e;
        }
    }

    @Override
    public boolean containsCIDefinition(CIFullName fullName) {
        try {
            List<CIFullName> ciNames = loadCINamesByPattern(fullName);
            return ciNames.size() > 0;
        } catch (SlothException e) {
            return false;
        }
    }

    @Override
    public ObjectStreamReader<CIFullName> getAllCINames() {
        Iterator<SlothAdminType> adminTypesIterator = SlothAdminType.sort(ADMIN_TYPES).iterator();
        DBObjectStreamReader reader = new DBObjectStreamReader(adminTypesIterator);

        return reader;
    }

    @Override
    public ObjectStreamReader<CIFullName> getCINamesByPattern(CIFullName fullNamePattern) {
        List<SlothAdminType> adminTypes = new ArrayList<>(1);
        adminTypes.add(fullNamePattern.getAdminType());
        Iterator<SlothAdminType> adminTypesIterator = adminTypes.iterator();

        AbstractCIName namePattern = fullNamePattern.getCIName();
        DBObjectStreamReader reader = new DBObjectStreamReader(adminTypesIterator, namePattern);

        return reader;
    }

    protected class DBObjectStreamReader implements ObjectStreamReader<CIFullName> {
        private final Iterator<SlothAdminType> adminTypesIter;
        private Iterator<CIFullName> adminTypeInstancesIter = null;
        private SlothAdminType currAdminType = null;
        private AbstractCIName adminTypeInstancesNamePattern = new StringCIName("");

        public DBObjectStreamReader(Iterator<SlothAdminType> adminTypesIter) {
            this.adminTypesIter = adminTypesIter;
        }

        public DBObjectStreamReader(Iterator<SlothAdminType> adminTypesIter, AbstractCIName adminTypeInstancesNamePattern) {
            this.adminTypesIter = adminTypesIter;
            this.adminTypeInstancesNamePattern = adminTypeInstancesNamePattern;
        }

        @Override
        public CIFullName next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            CIFullName result = adminTypeInstancesIter.next();
            return result;
        }

        @Override
        public boolean hasNext() {
            try {
                if (adminTypeInstancesIter != null && adminTypeInstancesIter.hasNext()) {
                    return true;
                }

                if (!adminTypesIter.hasNext()) {
                    return false;
                }

                moveAdminTypesIterator();
                return hasNext();
            } catch (SlothException e) {
                return false;
            }
        }

        private void moveAdminTypesIterator() throws SlothException {
            currAdminType = adminTypesIter.next();
            //todo добавить обработку бизнесс объектов
            initAdminTypeInstancesIterator();
        }

        private void initAdminTypeInstancesIterator() throws SlothException {
            CIFullName ciFullName = new CIFullName(currAdminType, adminTypeInstancesNamePattern);
            List<CIFullName> ciNames = loadCINamesByPattern(ciFullName);

            adminTypeInstancesIter = ciNames.iterator();
        }
    }

    private List<CIFullName> loadCINamesByPattern(CIFullName ciFullName) throws SlothException {
        String mqlAdminType = ciFullName.getAdminType().getMqlKey();
        AbstractCIName ciName = ciFullName.getCIName();
        String result;
        mqlCommand.execute(MqlKeywords.M_QOUTE, MqlKeywords.M_ON);
        if (SlothAdminType.isBus(ciFullName.getAdminType())) {
            try {
                BusCIName busName = (BusCIName) ciName;
                result = mqlCommand.execute(
                        MqlKeywords.M_TEMP,
                        MqlKeywords.M_QUERY,
                        mqlAdminType,
                        busName.getType(),
                        busName.getName(),
                        busName.getRevision()
                );
            } catch (ClassCastException e) {
                result = "";
            }
        } else if (M_TABLE.equals(ciFullName.getAdminType().toString())) {
            result = mqlCommand.execute(MqlKeywords.M_LIST, mqlAdminType, "system", ((StringCIName) ciName).getName());
        } else {
            result = mqlCommand.execute(MqlKeywords.M_LIST, mqlAdminType, ((StringCIName) ciName).getName());
        }
        if (result.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        String[] lines = result.split("\n");
        List<CIFullName> results = Arrays.stream(lines)
                .map(String::trim)
                .map(name -> {
                    List<String> parts = MqlParser.splitHeaderLine(name);
                    AbstractCIName abstractCIName = null;
                    if (ciFullName.getAdminType().getMqlAdminType().equals(MqlAdminType.BUSINESS_OBJECT)) {
                        if (parts.size() != REQUIRED_PART_OF_MQL_RESULT_NUMBER) {
                            throw new IndexOutOfBoundsException(
                                    "Size of parsed header is " + parts.size() + ", but for bus it must be 3"
                            );
                        }
                        abstractCIName = new BusCIName(parts.get(0), parts.get(1), parts.get(2));
                    } else {
                        if (parts.size() != 1) {
                            throw new IndexOutOfBoundsException(
                                    "Size of parsed header is " + parts.size() + ", but for admin object it must be 1"
                            );
                        }
                        abstractCIName = new StringCIName(parts.get(0));
                    }
                    return new CIFullName(ciFullName.getAdminType(), abstractCIName);
                })
                .sorted()
                .collect(Collectors.toList());
        return results;
    }

    @Override
    protected AbstractCI getCIByNameDirectly(CIFullName fullName) throws SlothException {
        SlothAdminType aType = fullName.getAdminType();
        String mqlAdminType = aType.getMqlAdminType().toString();
        String adminName = null;
        if (!SlothAdminType.isBus(aType)) {
            adminName = fullName.getCIName().toString();
        }
        try {
            String definition = loadCIStringByName(fullName);
            if (definition == null) {
                return new CIStub();
            }
            MqlParser mqlParser = MqlParser.fromString(definition);
            AbstractCI ci = (AbstractCI) mqlParser.parse();
            CIFullName foundName = ci.getCIFullName();
            if (!fullName.equals(foundName)) {
                throw new SlothException(String.format("CI name conflict. Required %s, found %s", fullName, foundName));
            }
            // Find symbolic name
            if (ci instanceof AdminObjectCI) {
                String queryResult = "";
                if (ci.getSlothAdminType().getMqlKey().equals("table")) {
                    queryResult = mqlCommand.execute(
                            M_LIST, M_PROPERTY, M_ON, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM,
                            M_TO, ci.getSlothAdminType().getMqlKey(), adminName, "system");
                } else {
                    queryResult = mqlCommand.execute(
                            M_LIST, M_PROPERTY, M_ON, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM,
                            M_TO, ci.getSlothAdminType().getMqlKey(), adminName);
                }
                String[] resultLines = queryResult.split("\n");
                if (resultLines.length > 0) {
                    String line = resultLines[0];
                    String symbolicName = line.split(" on program ")[0];
                    AdminObjectCI adminCI = (AdminObjectCI) ci;
                    adminCI.setSymbolicName(symbolicName);
                }
            }
            if (aType == SlothAdminType.PROGRAM) {
                ProgramCI ciCast = (ProgramCI) ci;
                String code = loadProgramCode(fullName);
                ciCast.setCode(code);

            } else if (aType == SlothAdminType.POLICY) {
                PolicyCI ciCast = (PolicyCI) ci;
                String result = mqlCommand.execute(
                        MqlKeywords.M_PRINT,
                        mqlAdminType,
                        adminName,
                        MqlKeywords.M_SELECT,
                        MqlKeywords.M_TYPE
                );
                List<Pair<String, String>> pairs = MqlUtil.parseSelectPairs(result);
                List<String> types = pairs.stream().map(Pair::getRight).collect(Collectors.toList());
                types.forEach(ciCast::addType);

            } else if (aType == SlothAdminType.RELATIONSHIP) {
                RelationshipCI ciCast = (RelationshipCI) ci;
                String result = mqlCommand.execute(
                        MqlKeywords.M_PRINT, mqlAdminType, adminName, MqlKeywords.M_SELECT,
                        MqlKeywords.M_FROM_TYPE, MqlKeywords.M_TO_TYPE, MqlKeywords.M_FROM_REL, MqlKeywords.M_TO_REL
                );
                List<Pair<String, String>> pairs = MqlUtil.parseSelectPairs(result);
                for (Pair<String, String> pair : pairs) {
                    String key = pair.getLeft();
                    String value = pair.getRight();
                    switch (key) {
                        case MqlKeywords.M_FROM_TYPE:
                            ciCast.getEnd(RelationshipCI.End.FROM).addType(value);
                            break;
                        case MqlKeywords.M_TO_TYPE:
                            ciCast.getEnd(RelationshipCI.End.TO).addType(value);
                            break;
                        case MqlKeywords.M_FROM_REL:
                            ciCast.getEnd(RelationshipCI.End.FROM).addRelationship(value);
                            break;
                        case MqlKeywords.M_TO_REL:
                            ciCast.getEnd(RelationshipCI.End.TO).addRelationship(value);
                            break;
                        default:
                            break;
                    }
                }
            } else if (aType == SlothAdminType.PAGE) {
                PageCI ciCast = (PageCI) ci;
                Page apiPage = new Page(fullName.getCIName().toString());
                Context ctx = ApplicationContext.instance().getFrameworkContext();
                apiPage.open(ctx);
                String content = apiPage.getContents(ctx);
                apiPage.close(ctx);
                ciCast.setContent(content);
            }
            return ci;
        } catch (Exception ex) {
            throw new SlothException(ex);
        }
    }

    protected String loadProgramCode(CIFullName programName) throws Exception {
        Workspace ws = Workspace.create(session);
        File wsDir = ws.getDir();
        String wsDirName = wsDir.getAbsolutePath();
        String progName = ((StringCIName) programName.getCIName()).getName();
        mqlCommand.execute(
                MqlKeywords.M_EXTRACT, MqlKeywords.M_PROGRAM, progName, MqlKeywords.M_SOURCE, wsDirName
        );
        Path file = Paths.get(wsDirName).resolve(MqlUtil.getJPOFileName(programName.getName()));
        String code = file.toFile().exists() ? loadFileContent(file) : "";
        return code;
    }

    private String loadFileContent(Path file) throws IOException {
        return FileUtils.readFileToString(file.toFile());
    }
}
