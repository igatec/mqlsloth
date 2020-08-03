package com.igatec.mqlsloth.io.fs;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.PageCI;
import com.igatec.mqlsloth.ci.ProgramCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.context.ApplicationContext;
import com.igatec.mqlsloth.iface.io.PersistenceFormat;
import com.igatec.mqlsloth.iface.util.Readable;
import com.igatec.mqlsloth.io.AbstractOutputProvider;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.writers.WriterCI;
import com.igatec.mqlsloth.writers.YAMLWriter;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class FileSystemOutputProvider extends AbstractOutputProvider {

    private static final Collection<SlothAdminType> INWRITEABLE_IN_DIFF_MODE = Arrays.asList(
            SlothAdminType.POLICY
    );

    private final String directory;
    private final FileWriterService fileWriterService;

    public FileSystemOutputProvider(String directory) {
        this.directory = directory;
        this.fileWriterService = ApplicationContext.instance().getFileWriterService();
    }

    @Override
    public void saveCIDefinition(AbstractCI ci) throws SlothException {

        if (ci.getDiffMode() == CIDiffMode.DIFF) { // TODO workaround
            if (INWRITEABLE_IN_DIFF_MODE.contains(ci.getSlothAdminType())) {
                return;
            }
        }

        CIFullName fullName = ci.getCIFullName();
        String relPath = FSUtil.REL_PATHS.getValue(fullName.getAdminType());
        File ciFile = new File(FSUtil.buildPath(
                directory,
                relPath,
                FSUtil.buildFileName(fullName, PersistenceFormat.YML)
        ));
        WriterCI writer = new YAMLWriter();
        String ciString;
        try {
            if (ci instanceof ProgramCI) {
                ProgramCI progCI = (ProgramCI) ci;
                String code = progCI.getCode();
                progCI.setCode("");
                ciString = writer.stringify(ci);
                progCI.setCode(code);
                if (progCI.getProgramType() == ProgramCI.Type.JAVA) {
                    String contentFilePath = FSUtil.buildContentFilePath(directory, fullName, ProgramCI.Type.JAVA);
                    fileWriterService.writeNewFile(new File(contentFilePath), code);
                }
            } else if (ci instanceof PageCI) {
                String content = ((PageCI) ci).getContent();
                ((PageCI) ci).setContent(fullName.getName());
                ciString = writer.stringify(ci);
                ((PageCI) ci).setContent(content);
                String contentFilePath = FSUtil.buildPath(
                        directory, FSUtil.REL_PATHS.getValue(SlothAdminType.PAGE),
                        "content", fullName.getName()
                );
                fileWriterService.writeNewFile(new File(contentFilePath), content);
            } else {
                ciString = writer.stringify(ci);
            }
            fileWriterService.writeNewFile(ciFile, ciString);
        } catch (ParserException | IOException e) {
            throw new SlothException(e);
        }
    }

    @Override
    public void saveUpdateScript(Readable readable) throws SlothException {
        BufferedWriter bw;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File scriptFile = new File(directory + File.separator + buildScriptName());
            bw = new BufferedWriter(new FileWriter(scriptFile));
            String line;
            while ((line = readable.read()) != null) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new SlothException(e);
        }
    }

    @Override
    public boolean clearAll() {
        File dir = new File(directory);
        if (dir.exists()) {
            try {
                FileUtils.cleanDirectory(dir);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private String buildScriptName() {
        Date current = new Date();
        return "update-" + current.getTime() + ".mql";
    }
}
