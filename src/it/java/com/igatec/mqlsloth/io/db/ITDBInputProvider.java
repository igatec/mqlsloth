package com.igatec.mqlsloth.io.db;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.io.AbstractInputProvider;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.SessionWithContextBuilder;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.TestUtils;
import com.igatec.mqlsloth.script.MqlKeywords;
import com.igatec.mqlsloth.util.ObjectStreamReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.igatec.mqlsloth.parser.TestUtils.getNullExceprionMessage;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class ITDBInputProvider {
    static AbstractInputProvider inputProvider;
    private static String host = "http://192.168.3.18:8070/internal";
    private static String user = "creator";
    private static String password = "Hls17d53F";

    @BeforeAll
    public static void init() throws Exception {
        SessionWithContextBuilder sb = new SessionWithContextBuilder();
        sb.setRemoteContext(host, user, password);
        Session session = sb.build();

        initDBInputProvider(session);
    }

    private static void initDBInputProvider(Session session) throws Exception {
        inputProvider = new DBInputProvider(session);
    }

    @Test
    public void testGetCINamesByPattern() throws SlothException {
        String regexMql = "Document*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.TYPE, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = "Document.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
        }

        return;
    }

    @Test
    public void testParseCITypeDefinitionsByPattern() throws SlothException, IOException, ParserException {
        String regexMql = "IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.TYPE, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = "IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.TYPE.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Type", "Type.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCIRelationshipDefinitionsByPattern() throws SlothException, IOException, ParserException {
        String regexMql = "IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.RELATIONSHIP, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = "IGA.*";
        Pattern pattern = Pattern.compile(regexJava);

        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.RELATIONSHIP.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Relationship", "Relationship.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCIPageDefinitionsByPattern() throws SlothException, IOException, ParserException {
        String regexMql = "IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.PAGE, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = "IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.PAGE.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Page", "Page.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCINumberGeneratorDefinitionsByPattern() throws SlothException, IOException, ParserException {
        String regexMql = "*IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.NUMBER_GENERATOR, new BusCIName(MqlKeywords.M_NUMBER_GENERATOR, regexMql, "*"));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = ".*IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.NUMBER_GENERATOR.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "NumberGenerator", "NumberGenerator.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCIObjectGeneratorDefinitionsByPattern() throws SlothException, IOException, ParserException {
        String regexMql = "*IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.OBJECT_GENERATOR, new BusCIName(MqlKeywords.M_OBJECT_GENERATOR, regexMql, "*"));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = ".*IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.OBJECT_GENERATOR.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "ObjectGenerator", "ObjectGenerator.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCITriggerDefinitionsByPattern() throws SlothException, IOException, ParserException {
        String regexMql = "*IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.TRIGGER, new BusCIName(MqlKeywords.M_TRIGGER, regexMql, "*"));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = ".*IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.TRIGGER.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Trigger", "Trigger.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCIProgramDefinitionsByPattern() throws SlothException, ParserException, IOException {
        String regexMql = "*IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.JPO, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = ".*IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.JPO.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Program", "Program.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCIRoleDefinitionsByPattern() throws SlothException, ParserException, IOException {
        String regexMql = "*IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.ROLE, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = ".*IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.ROLE.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Role", "Role.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }

    @Test
    public void testParseCIGroupDefinitionsByPattern() throws SlothException, ParserException, IOException {
        String regexMql = "*IGA*";
        CIFullName typeFullName = new CIFullName(SlothAdminType.GROUP, new StringCIName(regexMql));
        ObjectStreamReader<CIFullName> streamReader = inputProvider.getCINamesByPattern(typeFullName);

        String regexJava = ".*IGA.*";
        Pattern pattern = Pattern.compile(regexJava);
        while (streamReader.hasNext()) {
            System.out.println();
            CIFullName ciFullName = streamReader.next();
            Matcher matcher = pattern.matcher(ciFullName.getName());
            if (!matcher.matches()) {
                fail(ciFullName.getAdminType().toString() + " " + ciFullName.getName() + " doesn't match pattern");
            }
            System.out.println(ciFullName.getAdminType().toString() + " " +ciFullName.getName());
            AbstractCI abstractCI = inputProvider.getCIDefinition(ciFullName);

            assertNotNull(abstractCI, getNullExceprionMessage(SlothAdminType.GROUP.toString(), ciFullName.toString()));

            Path resourceDirectory = Paths.get("src","test","resources");
            Path filePath = resourceDirectory.resolve(Paths.get("com", "igatec", "mqlsloth", "parser", "Group", "Group.yaml"));
            TestUtils.writeToYamlFileIfNotExist(filePath, abstractCI);
        }

        return;
    }
}
