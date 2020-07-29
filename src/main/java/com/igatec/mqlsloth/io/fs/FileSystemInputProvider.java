package com.igatec.mqlsloth.io.fs;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.PageCI;
import com.igatec.mqlsloth.ci.ProgramCI;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.iface.io.PersistenceFormat;
import com.igatec.mqlsloth.io.AbstractInputProviderPatternInterpreter;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.parser.Parser;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.yaml.YAMLParser;
import com.igatec.mqlsloth.util.CIStub;
import com.igatec.mqlsloth.util.ObjectStreamReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileSystemInputProvider extends AbstractInputProviderPatternInterpreter {

    private static final int DEFAULT_QUEUE_SIZE = 10;
    private final String directory;

    public FileSystemInputProvider(String directory, int namesQueueCapacity, int ciStringsQueueCapacity, int cisQueueCapacity) {
        super(namesQueueCapacity, ciStringsQueueCapacity, cisQueueCapacity);
        this.directory = directory;
    }

    public FileSystemInputProvider(String directory) {
        this(directory, DEFAULT_QUEUE_SIZE, DEFAULT_QUEUE_SIZE, DEFAULT_QUEUE_SIZE);
    }

    @Override
    protected AbstractCI getCIByNameDirectly(CIFullName fullName) throws SlothException {
        SlothAdminType aType = fullName.getAdminType();
        try {

            if (aType == SlothAdminType.PROGRAM) {
                String jpoContentFilePath = FSUtil.buildContentFilePath(directory, fullName, ProgramCI.Type.JAVA);
                File contentFile = new File(jpoContentFilePath);
                String code = "";
                if (contentFile.exists()) {
                    code = FileUtils.readFileToString(new File(jpoContentFilePath));
                }
                AbstractCI ci;
                File defFile = getCIFile(fullName);
                if (defFile != null) {
                    Parser<AbstractCI> parser = YAMLParser.fromString(FileUtils.readFileToString(defFile));
                    ci = parser.parse();
                    if (!(ci instanceof ProgramCI)) {
                        throw new SlothException("CI type error");
                    }
                    if (!fullName.equals(ci.getCIFullName())) {
                        throw new SlothException("CI name conflict");
                    }
                } else {
                    if (code == null) {
                        return new CIStub();
                    }
                    ci = new ProgramCI(fullName.getName());
                    ((ProgramCI) ci).setProgramType(ProgramCI.Type.JAVA);
                }
                ((ProgramCI) ci).setCode(code);
                return ci;

            } else if (aType == SlothAdminType.PAGE) {
                AbstractCI ci;
                File defFile = getCIFile(fullName);
                String contentFileName = fullName.getName();
                if (defFile != null) {
                    Parser<AbstractCI> parser = YAMLParser.fromString(FileUtils.readFileToString(defFile));
                    ci = parser.parse();
                    if (!(ci instanceof PageCI)) {
                        throw new SlothException("CI type error");
                    }
                    if (!fullName.equals(ci.getCIFullName())) {
                        throw new SlothException("CI name conflict");
                    }
                    String content = ((PageCI) ci).getContent();
                    if (content != null && !"".equals(content)) {
                        contentFileName = content;
                    }
                } else {
                    ci = new PageCI(fullName.getName());
                }
                String contentFilePath = FSUtil.buildPath(
                        directory, FSUtil.REL_PATHS.getValue(SlothAdminType.PAGE),
                        "content", contentFileName
                );
                File contentFile = new File(contentFilePath);
                if (!contentFile.exists()) {
                    if (defFile.exists()) {
                        throw new SlothException("Page content file was not found");
                    } else {
                        return new CIStub();
                    }
                } else {
                    String content = FileUtils.readFileToString(contentFile);
                    ((PageCI) ci).setContent(content);
                }
                return ci;

            }
        } catch (Exception ex) {
            throw new SlothException(ex);
        }
        return null;
    }

    @Override
    protected String getCIStringByName(CIFullName fullName) throws SlothException {
        File ciFile = getCIFile(fullName);
        if (ciFile == null) {
            return null;
        }
        try {
            return FileUtils.readFileToString(ciFile);
        } catch (IOException e) {
            throw new SlothException("Could not read file " + ciFile.getName());
        }
    }

    private File getCIFile(CIFullName fullName) throws SlothException {
        String relPath = FSUtil.REL_PATHS.getValue(fullName.getAdminType());
        File ciFile = null;
        for (PersistenceFormat format : FSUtil.FILE_EXTENSIONS.keySet()) {
            File temp = new File(FSUtil.buildPath(directory, relPath, FSUtil.buildFileName(fullName, format)));
            if (temp.exists()) {
                if (ciFile != null) {
                    throw new SlothException(String.format("File conflict: %s, %s", ciFile.getName(), temp.getName()));
                } else {
                    ciFile = temp;
                }
            }
        }
        return ciFile;
    }

    @Override
    protected AbstractCI deserialize(String ciString) throws ParserException {
        Parser<AbstractCI> parser = YAMLParser.fromString(ciString);
        return parser.parse();
    }

    @Override
    public ObjectStreamReader<CIFullName> getCINamesByPattern(CIFullName fullNamePattern) {

        return new ObjectStreamReader<CIFullName>() {

            SortedSet<CIFullName> names = new TreeSet<>();
            Iterator<CIFullName> namesIter;
            SlothAdminType aType = fullNamePattern.getAdminType();
            String pattern = fullNamePattern.getName();
            File currentDir = new File(FSUtil.buildPath(directory, FSUtil.REL_PATHS.getValue(aType)));

            {
                if (currentDir.exists()) {
                    Iterator<File> fileIter = FileUtils.iterateFiles(currentDir,
                            FSUtil.FILE_EXTENSIONS.valueSet().toArray(new String[FSUtil.FILE_EXTENSIONS.size()]),
                            false);
                    while (fileIter.hasNext()) {
                        File ciFile = fileIter.next();
                        CIFullName fullName = FSUtil.buildNameByFileNameIfMatch(aType, ciFile.getName());
                        if (fullName != null && FSUtil.matchesMqlPattern(fullName, pattern)) {
                            names.add(fullName);
                        }
                    }
                }
                String contentFileDirName = FSUtil.buildContentFileDir(directory, aType, ProgramCI.Type.JAVA);
                if (contentFileDirName != null) {
                    File contentFileDir = new File(contentFileDirName);
                    if (contentFileDir.exists() && contentFileDir.isDirectory()) {
                        Iterator<File> fileIter = FileUtils.iterateFiles(contentFileDir, null, false);
                        if (aType == SlothAdminType.PROGRAM) {
                            int nameEndLength = FSUtil.JPO_FILE_NAME_END.length();
                            while (fileIter.hasNext()) {
                                String name = fileIter.next().getName();
                                if (name.endsWith(FSUtil.JPO_FILE_NAME_END)) {
                                    String ciName = name.substring(0, name.length() - nameEndLength);
                                    CIFullName fullName = new CIFullName(SlothAdminType.PROGRAM, new StringCIName(ciName));
                                    names.add(fullName);
                                }
                            }
                        } else if (aType == SlothAdminType.PAGE) {
                            while (fileIter.hasNext()) {
                                String name = fileIter.next().getName();
                                CIFullName fullName = new CIFullName(SlothAdminType.PAGE, new StringCIName(name));
                                names.add(fullName);
                            }
                        }
                    }
                }
                namesIter = names.iterator();
            }

            @Override
            public CIFullName next() throws SlothException {
                return namesIter.next();
            }

            @Override
            public boolean hasNext() {
                return namesIter.hasNext();
            }
        };

    }

    @Override
    public boolean containsCIDefinition(CIFullName fullName) {
        try {
            File ciFile = getCIFile(fullName);
            return ciFile != null;
        } catch (SlothException e) {
            return false;
        }
    }

    @Override
    public ObjectStreamReader<CIFullName> getAllCINames() {
        // TODO search for JPOs and Pages that don't have def file (only content) does not work in this method
        ObjectStreamReader<CIFullName> reader = new ObjectStreamReader<CIFullName>() {
            private final Iterator<SlothAdminType> persistedTypesIter = SlothAdminType.sort(FSUtil.REL_PATHS.keySet()).iterator();
            private Iterator<File> fileIter = null;
            private SlothAdminType currentType = null;
            private CIFullName nextName = null;

            {
                initFileIter();
                nextName();
            }

            private boolean initFileIter() {
                if (!persistedTypesIter.hasNext()) {
                    return false;
                }
                currentType = persistedTypesIter.next();
                File currentDir = new File(FSUtil.buildPath(directory, FSUtil.REL_PATHS.getValue(currentType)));
                if (currentDir.exists()) {
                    fileIter = FileUtils.iterateFiles(
                            currentDir,
                            FSUtil.FILE_EXTENSIONS.valueSet().toArray(new String[FSUtil.FILE_EXTENSIONS.size()]),
                            false);
                } else {
                    fileIter = new LinkedList<File>().iterator();
                }
                return true;
            }

            private void nextName() {
                nextName = null;
                while (true) {
                    while (fileIter.hasNext()) {
                        File ciFile = fileIter.next();
                        CIFullName fullName = FSUtil.buildNameByFileNameIfMatch(currentType, ciFile.getName());
                        if (fullName != null) {
                            nextName = fullName;
                            return;
                        }
                    }
                    if (!initFileIter()) {
                        return;
                    }
                }
            }

            @Override
            public CIFullName next() throws SlothException {
                if (nextName == null) {
                    throw new NoSuchElementException();
                }
                CIFullName result = nextName;
                nextName();
                return result;
            }

            @Override
            public boolean hasNext() {
                return nextName != null;
            }
        };

        return reader;
    }

}
