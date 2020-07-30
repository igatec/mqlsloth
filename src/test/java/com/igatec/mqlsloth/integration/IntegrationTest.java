package com.igatec.mqlsloth.integration;

import com.igatec.mqlsloth.cli.SlothAppCLI;
import com.igatec.mqlsloth.context.ApplicationContext;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.framework.MQLCommand;
import com.igatec.mqlsloth.kernel.SlothException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntegrationTest {

    private final MQLCommand command = mock(MQLCommand.class);

    @BeforeEach
    void setUp() {
        ApplicationContext.instance().setMqlCommand(command);
    }

    @Test
    void test() throws Exception {
        mockCommand("list type Document", "Document");
        mockCommandUsingFile("print type Document", "type_Document.mql");
        mockCommand("list property on program eServiceSchemaVariableMapping.tcl to type Document", "");

        SlothAppCLI.main(generateContext(), args("-e -p type Document -t ."));
    }

    private void mockCommand(String cmd, String result) throws SlothException {
        int i = 1;
        StringBuilder sb = new StringBuilder();
        String[] cmdArr = cmd.split(" ");
        for (String s : cmdArr) {
            sb.append("$");
            sb.append(i);
            sb.append(" ");
            i++;
        }
        sb.deleteCharAt(sb.length() -1);

        when(command.executeOrThrow(any(), eq(sb.toString()), eq(Arrays.asList(cmdArr))))
                .thenReturn(result);
    }

    private void mockCommandUsingFile(String cmd, String fileWithResult) throws SlothException {
        int i = 1;
        StringBuilder sb = new StringBuilder();
        String[] cmdArr = cmd.split(" ");
        for (String s : cmdArr) {
            sb.append("$");
            sb.append(i);
            sb.append(" ");
            i++;
        }
        sb.deleteCharAt(sb.length() -1);

        String fileContent = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/mqloutput/" + fileWithResult)
        ))
                .lines()
                .collect(Collectors.joining("\n"));

        when(command.executeOrThrow(any(), eq(sb.toString()), eq(Arrays.asList(cmdArr))))
                .thenReturn(fileContent);
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
