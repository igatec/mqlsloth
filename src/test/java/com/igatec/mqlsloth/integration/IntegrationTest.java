package com.igatec.mqlsloth.integration;

import com.igatec.mqlsloth.cli.SlothAppCLI;
import com.igatec.mqlsloth.context.ApplicationContext;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.framework.MQLCommand;
import com.igatec.mqlsloth.io.fs.FileWriterService;
import com.igatec.mqlsloth.kernel.SlothException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IntegrationTest {

    private final MQLCommand command = mock(MQLCommand.class);
    private final FileWriterService fileWriterService = mock(FileWriterService.class);

    @BeforeEach
    void setUp() {
        ApplicationContext.instance().setMqlCommand(command);
        ApplicationContext.instance().setFileWriterService(fileWriterService);
    }

    @Test
    void test() throws Exception {
        mockCommand("list type Document", "Document");
        mockCommandUsingFile("print type Document", "type_Document.mql");
        mockCommand("list property on program eServiceSchemaVariableMapping.tcl to type Document", "");

        SlothAppCLI.main(generateContext(), args("-e -p type Document -t ."));

        verifyFileExport("./dataModel/type/Document.yml", "dataModel/type/Document.yml");
    }

    private void verifyFileExport(String expectedFilePath, String expectedContentPath) throws IOException {
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(fileWriterService).writeNewFile(fileCaptor.capture(), contentCaptor.capture());

        String actualExportPath = fileCaptor.getValue().getPath();
        assertThat(Paths.get(actualExportPath), equalTo(Paths.get(expectedFilePath)));

        String actualContent = contentCaptor.getValue();
        assertThat(
                readFileFromClasspath("/slothoutput/" + expectedContentPath).trim(),
                equalTo(actualContent.trim())
        );
    }

    private void mockCommand(String cmd, String result) throws SlothException {
        String[] cmdArr = cmd.split(" ");

        when(command.executeOrThrow(eq(generateCommandTemplate(cmdArr)), eq(Arrays.asList(cmdArr))))
                .thenReturn(result);
    }

    private void mockCommandUsingFile(String cmd, String fileWithResult) throws SlothException {
        String[] cmdArr = cmd.split(" ");

        String fileContent = readFileFromClasspath("/mqloutput/" + fileWithResult);

        when(command.executeOrThrow(eq(generateCommandTemplate(cmdArr)), eq(Arrays.asList(cmdArr))))
                .thenReturn(fileContent);
    }

    private static String generateCommandTemplate(String[] cmd) {
        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (String s : cmd) {
            sb.append("$");
            sb.append(i);
            sb.append(" ");
            i++;
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String readFileFromClasspath(String filePath) {
        return new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(filePath)
        ))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private static String[] args(String args) {
        return args.split(" ");
    }

    private static Context generateContext() {
        return new Context() {
            @Override
            public void setUser(String user) {

            }

            @Override
            public void setPassword(String password) {

            }

            @Override
            public void connect() {

            }

            @Override
            public boolean isConnected() {
                return false;
            }

            @Override
            public String createWorkspace() {
                return null;
            }
        };
    }
}
