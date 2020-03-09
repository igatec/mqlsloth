package com.igatec.mqlsloth.parser;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.AttributeCI;
import com.igatec.mqlsloth.ci.InterfaceCI;
import com.igatec.mqlsloth.ci.NumberGeneratorCI;
import com.igatec.mqlsloth.ci.PageCI;
import com.igatec.mqlsloth.ci.RelationshipCI;
import com.igatec.mqlsloth.ci.RelationshipCI.Cardinality;
import com.igatec.mqlsloth.ci.RelationshipCI.CloneBehaviour;
import com.igatec.mqlsloth.ci.RelationshipCI.End;
import com.igatec.mqlsloth.ci.RelationshipCI.RelCIEnd;
import com.igatec.mqlsloth.ci.TypeCI;
import com.igatec.mqlsloth.ci.constants.AttributeType;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.yaml.YAMLParser;
import com.igatec.mqlsloth.util.SlothString;
import com.igatec.mqlsloth.writers.WriterCI;
import com.igatec.mqlsloth.writers.YAMLWriter;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestParserYAML {

    //region Type
    @Test
    public void testYAMLTypeCiParser() throws Exception {
        String ciTestData = getTestYAMLType();
        TypeCI controlType = getControlYAMLTypeDIFF();

        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();

        TestUtils.throwIfExpectedNotEqualsActualType(controlType, (TypeCI) abstractCI);
    }

    private String getTestYAMLType() throws IOException {
        File file = new File(getClass().getResource("TypeExample/Type.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private TypeCI getControlYAMLTypeDIFF() {
        TypeCI controlType = new TypeCI("ARCHDocument", CIDiffMode.DIFF);
        //AdminObjectCI
        controlType.setDescription("Scanned sheet with inventory number");
        controlType.setHidden(false);
        controlType.setProperty("Specializable", "Yes");
        controlType.setProperty("UUID", "56284838-d843-11e8-9f8b-f2801f1b9fd1");

        controlType.deleteProperty("remtest_Specializable");
        controlType.deleteProperty("remtest_UUID");
        //TypeLikeCI
        controlType.setAbstract(true);
        controlType.setParentType(new SlothString("Document"));
        controlType.deleteParentType();
        controlType.addAttribute("ARCHStageOfProject");
        controlType.addAttribute("ARCHSheetsNumber");
        controlType.addAttribute("ARCHSheetNumber");
        controlType.addAttribute("ARCHSheetFormat");
        controlType.addAttribute("ARCHNotes");
        controlType.addAttribute("ARCHChangeStatus");
        controlType.addAttribute("ARCHChangeNumber");
        controlType.addAttribute("ARCHApprovalNumber");
        controlType.addAttribute("ARCHReferenceObject");
        controlType.addAttribute("ARCHDocumentType");

        controlType.reverseAttribute("remtest_ARCHStageOfProject");
        controlType.reverseAttribute("remtest_ARCHSheetsNumber");
        controlType.reverseAttribute("remtest_ARCHSheetNumber");
        controlType.reverseAttribute("remtest_ARCHSheetFormat");
        controlType.reverseAttribute("remtest_ARCHNotes");
        controlType.reverseAttribute("remtest_ARCHChangeStatus");
        controlType.reverseAttribute("remtest_ARCHChangeNumber");
        controlType.reverseAttribute("remtest_ARCHApprovalNumber");
        controlType.reverseAttribute("remtest_ARCHReferenceObject");
        controlType.reverseAttribute("remtest_ARCHDocumentType");
        //TypeCI
        controlType.setComposed(true);
        return controlType;
    }

    @Test
    public void testYAMLTypeWithUnknownKeywordsCiParser() throws IOException {
        String ciTestData = getTestYAMLTypeExtendedByUnknownKeywords();
        Parser parser = YAMLParser.fromString(ciTestData);
        assertThrows(ParserException.class, parser::parse);

        ciTestData = getTestYAMLTypeCompactedByUnknownKeywords();
        parser = YAMLParser.fromString(ciTestData);
        assertThrows(ParserException.class, parser::parse);
    }

    private String getTestYAMLTypeExtendedByUnknownKeywords() throws IOException {
        File file = new File(getClass().getResource("Type/TypeExtendedUnknownFields.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private String getTestYAMLTypeCompactedByUnknownKeywords() throws IOException {
        File file = new File(getClass().getResource("Type/TypeCompactedUnknownFields.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    @Test
    public void testYAMLTypeCiStringifyThenParse() throws ParserException {
        TypeCI controlType = getControlYAMLType();

        WriterCI writerCI = new YAMLWriter();
        String type = writerCI.stringify(controlType);
        System.out.println(type);
        Parser<AbstractCI> parser = YAMLParser.fromString(type);
        AbstractCI abstractCI = parser.parse();
        assertEquals(controlType, abstractCI);
        return;
    }

    private TypeCI getControlYAMLType() {
        TypeCI controlType = new TypeCI("ARCHDocument", CIDiffMode.TARGET);
        //AdminObjectCI
        controlType.setDescription("Scanned sheet with inventory number");
        controlType.setHidden(false);
        controlType.setProperty("Specializable", "Yes");
        controlType.setProperty("UUID", "56284838-d843-11e8-9f8b-f2801f1b9fd1");

//        controlType.deleteProperty("remtest_Specializable");
//        controlType.deleteProperty("remtest_UUID");
        //TypeLikeCI
        controlType.setAbstract(true);
        controlType.setParentType(new SlothString("Document"));
        controlType.deleteParentType();
        controlType.addAttribute("ARCHStageOfProject");
        controlType.addAttribute("ARCHSheetsNumber");
        controlType.addAttribute("ARCHSheetNumber");
        controlType.addAttribute("ARCHSheetFormat");
        controlType.addAttribute("ARCHNotes");
        controlType.addAttribute("ARCHChangeStatus");
        controlType.addAttribute("ARCHChangeNumber");
        controlType.addAttribute("ARCHApprovalNumber");
        controlType.addAttribute("ARCHReferenceObject");
        controlType.addAttribute("ARCHDocumentType");

//        controlType.reverseAttribute("remtest_ARCHStageOfProject");
//        controlType.reverseAttribute("remtest_ARCHSheetsNumber");
//        controlType.reverseAttribute("remtest_ARCHSheetNumber");
//        controlType.reverseAttribute("remtest_ARCHSheetFormat");
//        controlType.reverseAttribute("remtest_ARCHNotes");
//        controlType.reverseAttribute("remtest_ARCHChangeStatus");
//        controlType.reverseAttribute("remtest_ARCHChangeNumber");
//        controlType.reverseAttribute("remtest_ARCHApprovalNumber");
//        controlType.reverseAttribute("remtest_ARCHReferenceObject");
//        controlType.reverseAttribute("remtest_ARCHDocumentType");
        //TypeCI
        controlType.setComposed(true);
        return controlType;
    }

    //endregion

    //region Attribute
    @Test
    public void testYAMLAttributeCIParsing() throws Exception {
        String ciTestData = getTestYAMLAttribute();
        AttributeCI controlAttribute = getControlYAMLAttributeDIFF();

        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();

        TestUtils.throwIfExpectedNotEqualsActualAttribute(controlAttribute, (AttributeCI) abstractCI);
    }

    private String getTestYAMLAttribute() throws IOException {
        File file = new File(getClass().getResource("Attribute.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private AttributeCI getControlYAMLAttributeDIFF() {
        AttributeCI controlAttribute = new AttributeCI("IGAKBNState", AttributeType.STRING, CIDiffMode.DIFF);
        //AdminObjectCI
        controlAttribute.setDescription("Stores OOTB Task states");
        controlAttribute.setHidden(false);

        controlAttribute.setProperty("MxUpdate Sub Path", "");
        controlAttribute.setProperty("file date", "05-22-2019 14:40:28.000");
        controlAttribute.setProperty("installed date", "03-07-2019");
        controlAttribute.setProperty("installer", "The MxUpdate Team");
        controlAttribute.setProperty("remtest_MxUpdate Sub Path", "");
        controlAttribute.setProperty("remtest_file date", "05-22-2019 14:40:28.000");
        controlAttribute.setProperty("remtest_installed date", "03-07-2019");
        controlAttribute.setProperty("remtest_installer", "The MxUpdate Team");
        controlAttribute.deleteProperty("remtest_MxUpdate Sub Path");
        controlAttribute.deleteProperty("remtest_file date");
        controlAttribute.deleteProperty("remtest_installed date");
        controlAttribute.deleteProperty("remtest_installer");
        //AttributeCI
        controlAttribute.setMultivalue(true);
        controlAttribute.setMultiline(false);
        controlAttribute.setDefaultValue(new SlothString("Create"));
        controlAttribute.deleteDefaultValue();

        controlAttribute.addRange("Active");
        controlAttribute.addRange("Assign");
        controlAttribute.addRange("Complete");
        controlAttribute.addRange("Create");
        controlAttribute.addRange("Review");
        controlAttribute.addRange("remtest_Active");
        controlAttribute.addRange("remtest_Assign");
        controlAttribute.addRange("remtest_Complete");
        controlAttribute.addRange("remtest_Create");
        controlAttribute.addRange("remtest_Review");
        controlAttribute.reverseRange("remtest_Active");
        controlAttribute.reverseRange("remtest_Assign");
        controlAttribute.reverseRange("remtest_Complete");
        controlAttribute.reverseRange("remtest_Create");
        controlAttribute.reverseRange("remtest_Review");

        controlAttribute.setMaxLength(0);
        controlAttribute.setResetOnClone(false);
        controlAttribute.setResetOnRevision(false);
        return controlAttribute;
    }

    @Test
    public void testYAMLAttributeCiStringifyThenParse() throws ParserException {
        AttributeCI controlAttribute = getControlYAMLAttribute();

        WriterCI writerCI = new YAMLWriter();
        String attribute = writerCI.stringify(controlAttribute);
        System.out.println(attribute);
        Parser<AbstractCI> parser = YAMLParser.fromString(attribute);
        AbstractCI abstractCI = parser.parse();
        assertEquals(controlAttribute, abstractCI);
        return;
    }

    private AttributeCI getControlYAMLAttribute() {
        AttributeCI controlAttribute = new AttributeCI("IGAKBNState", AttributeType.STRING, CIDiffMode.TARGET);
        //AdminObjectCI
        controlAttribute.setDescription("Stores OOTB Task states");
        controlAttribute.setHidden(false);

        controlAttribute.setProperty("MxUpdate Sub Path", "");
        controlAttribute.setProperty("file date", "05-22-2019 14:40:28.000");
        controlAttribute.setProperty("installed date", "03-07-2019");
        controlAttribute.setProperty("installer", "The MxUpdate Team");
        controlAttribute.setProperty("remtest_MxUpdate Sub Path", "");
        controlAttribute.setProperty("remtest_file date", "05-22-2019 14:40:28.000");
        controlAttribute.setProperty("remtest_installed date", "03-07-2019");
        controlAttribute.setProperty("remtest_installer", "The MxUpdate Team");
//        controlAttribute.deleteProperty("remtest_MxUpdate Sub Path");
//        controlAttribute.deleteProperty("remtest_file date");
//        controlAttribute.deleteProperty("remtest_installed date");
//        controlAttribute.deleteProperty("remtest_installer");
        //AttributeCI
        controlAttribute.setMultivalue(true);
        controlAttribute.setMultiline(false);
        controlAttribute.setDefaultValue(new SlothString("Create"));
//        controlAttribute.deleteDefaultValue();

        controlAttribute.addRange("Active");
        controlAttribute.addRange("Assign");
        controlAttribute.addRange("Complete");
        controlAttribute.addRange("Create");
        controlAttribute.addRange("Review");
        controlAttribute.addRange("remtest_Active");
        controlAttribute.addRange("remtest_Assign");
        controlAttribute.addRange("remtest_Complete");
        controlAttribute.addRange("remtest_Create");
        controlAttribute.addRange("remtest_Review");
//        controlAttribute.reverseRange("remtest_Active");
//        controlAttribute.reverseRange("remtest_Assign");
//        controlAttribute.reverseRange("remtest_Complete");
//        controlAttribute.reverseRange("remtest_Create");
//        controlAttribute.reverseRange("remtest_Review");

        controlAttribute.setMaxLength(0);
        controlAttribute.setResetOnClone(false);
        controlAttribute.setResetOnRevision(false);
        return controlAttribute;
    }

    //endregion

    //region Interface

    @Test
    public void testYAMLInterfaceCIParsing() throws Exception {
        String ciTestData = getTestYAMLInterface();
        InterfaceCI controlInterface = getControlYAMLInterfaceDIFF();

        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();

        TestUtils.throwIfExpectedNotEqualsActualInterface(controlInterface, (InterfaceCI) abstractCI);
    }

    private String getTestYAMLInterface() throws IOException {
        File file = new File(getClass().getResource("Interface.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private InterfaceCI getControlYAMLInterfaceDIFF() {
        InterfaceCI controlInterface = new InterfaceCI("IGAKBNExtension", CIDiffMode.DIFF);
        //AdminObjectCI
        controlInterface.setDescription("");
        controlInterface.setHidden(false);

        controlInterface.setProperty("MxUpdate Sub Path", "");
        controlInterface.setProperty("file date", "05-22-2019 15:35:54.000");
        controlInterface.setProperty("installed date", "05-22-2019");
        controlInterface.setProperty("installer", "The MxUpdate Team");
        controlInterface.setProperty("remtest_MxUpdate Sub Path", "");
        controlInterface.setProperty("remtest_file date", "05-22-2019 15:35:54.000");
        controlInterface.setProperty("remtest_installed date", "05-22-2019");
        controlInterface.setProperty("remtest_installer", "The MxUpdate Team");
        controlInterface.deleteProperty("remtest_MxUpdate Sub Path");
        controlInterface.deleteProperty("remtest_file date");
        controlInterface.deleteProperty("remtest_installed date");
        controlInterface.deleteProperty("remtest_installer");
        //TypeLikeCI
        controlInterface.setParentType(new SlothString("Document"));
        controlInterface.deleteParentType();

        controlInterface.setAbstract(true);

        controlInterface.addAttribute("IGAKBNUID");
        controlInterface.addAttribute("IGAKBNPriority");
        controlInterface.addAttribute("remtest_IGAKBNUID");
        controlInterface.addAttribute("remtest_IGAKBNPriority");
        controlInterface.reverseAttribute("remtest_IGAKBNUID");
        controlInterface.reverseAttribute("remtest_IGAKBNPriority");
        //InterfaceCI
        controlInterface.addType("Task");
        controlInterface.addType("Inbox Task");
        controlInterface.addType("remtest_Task");
        controlInterface.addType("remtest_Inbox Task");
        controlInterface.reverseType("remtest_Task");
        controlInterface.reverseType("remtest_Inbox Task");

        controlInterface.addRelationship("Route Node");
        controlInterface.addRelationship("Test Relationship");
        controlInterface.addRelationship("remtest_Route Node");
        controlInterface.addRelationship("remtest_Test Relationship");
        controlInterface.reverseRelationship("remtest_Route Node");
        controlInterface.reverseRelationship("remtest_Test Relationship");
        return controlInterface;
    }

    @Test
    public void testYAMLInterfaceCiStringifyThenParse() throws ParserException {
        InterfaceCI controlInterface = getControlYAMLInterface();

        WriterCI writerCI = new YAMLWriter();
        String interfaceCi = writerCI.stringify(controlInterface);
        System.out.println(interfaceCi);
        Parser<AbstractCI> parser = YAMLParser.fromString(interfaceCi);
        AbstractCI abstractCI = parser.parse();
        assertEquals(controlInterface, abstractCI);
        return;
    }

    private InterfaceCI getControlYAMLInterface() {
        InterfaceCI controlInterface = new InterfaceCI("IGAKBNExtension", CIDiffMode.TARGET);
        //AdminObjectCI
        controlInterface.setDescription("");
        controlInterface.setHidden(false);

        controlInterface.setProperty("MxUpdate Sub Path", "");
        controlInterface.setProperty("file date", "05-22-2019 15:35:54.000");
        controlInterface.setProperty("installed date", "05-22-2019");
        controlInterface.setProperty("installer", "The MxUpdate Team");
        controlInterface.setProperty("remtest_MxUpdate Sub Path", "");
        controlInterface.setProperty("remtest_file date", "05-22-2019 15:35:54.000");
        controlInterface.setProperty("remtest_installed date", "05-22-2019");
        controlInterface.setProperty("remtest_installer", "The MxUpdate Team");
//        controlInterface.deleteProperty("remtest_MxUpdate Sub Path");
//        controlInterface.deleteProperty("remtest_file date");
//        controlInterface.deleteProperty("remtest_installed date");
//        controlInterface.deleteProperty("remtest_installer");
        //TypeLikeCI
        controlInterface.setParentType(new SlothString("Document"));
//        controlInterface.deleteParentType();

        controlInterface.setAbstract(true);

        controlInterface.addAttribute("IGAKBNUID");
        controlInterface.addAttribute("IGAKBNPriority");
        controlInterface.addAttribute("remtest_IGAKBNUID");
        controlInterface.addAttribute("remtest_IGAKBNPriority");
//        controlInterface.reverseAttribute("remtest_IGAKBNUID");
//        controlInterface.reverseAttribute("remtest_IGAKBNPriority");
        //InterfaceCI
        controlInterface.addType("Task");
        controlInterface.addType("Inbox Task");
        controlInterface.addType("remtest_Task");
        controlInterface.addType("remtest_Inbox Task");
//        controlInterface.reverseType("remtest_Task");
//        controlInterface.reverseType("remtest_Inbox Task");

        controlInterface.addRelationship("Route Node");
        controlInterface.addRelationship("Test Relationship");
        controlInterface.addRelationship("remtest_Route Node");
        controlInterface.addRelationship("remtest_Test Relationship");
//        controlInterface.reverseRelationship("remtest_Route Node");
//        controlInterface.reverseRelationship("remtest_Test Relationship");
        return controlInterface;
    }

    //endregion

    //region Relationship

    @Test
    public void testYAMLRelationshipCIParsing() throws Exception {
        String ciTestData = getTestYAMLRelationship();
        RelationshipCI controlRelationship = getControlYAMLRelationshipDIFF();

        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();
        TestUtils.throwIfExpectedNotEqualsActualRelationship((RelationshipCI) abstractCI, controlRelationship);
    }

    private String getTestYAMLRelationship() throws IOException {
        File file = new File(getClass().getResource("Relationship.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private RelationshipCI getControlYAMLRelationshipDIFF() {
        RelationshipCI controlRelationship = new RelationshipCI("IGAKBNBoardLane", CIDiffMode.DIFF);
        //AdminObjectCI
        controlRelationship.setDescription("Connect Kanban board and lanes");
        controlRelationship.setHidden(false);
        controlRelationship.setProperty("MxUpdate Sub Path", "");
        controlRelationship.setProperty("file date", "05-22-2019 14:40:28.000");
        controlRelationship.setProperty("installed date", "03-07-2019");
        controlRelationship.setProperty("installer", "The MxUpdate Team");
        //TypeLikeCI
        controlRelationship.setAbstract(false);
//        controlRelationship.setParentType(new SlothString("Document"));
        controlRelationship.addAttribute("IGAKBNIsDefault");
        controlRelationship.addAttribute("IGAKBNState");
        controlRelationship.addAttribute("IGAKBNIndex");
        //RelationshipCI
        controlRelationship.setPreventDuplicates(true);

        RelCIEnd endFrom = controlRelationship.getEnd(End.FROM);
        endFrom.addType("IGAKBNBoard");
        endFrom.addType("remtest_TypeFrom");
        endFrom.reverseType("remtest_TypeFrom");

        endFrom.addRelationship("remtest_RelFrom");
        endFrom.reverseRelationship("remtest_RelFrom");

        endFrom.setMeaning("");
        endFrom.setRevisionBehaviour(CloneBehaviour.NONE);
        endFrom.setCloneBehaviour(CloneBehaviour.NONE);
        endFrom.setCardinality(Cardinality.ONE);
        endFrom.setPropagateModify(false);
        endFrom.setPropagateConnection(false);

        RelCIEnd endTo = controlRelationship.getEnd(End.TO);
        endTo.addType("IGAKBNLane");
        endFrom.addType("remtest_TypeTo");
        endFrom.reverseType("remtest_TypeTo");

        endFrom.addRelationship("remtest_RelTo");
        endFrom.reverseRelationship("remtest_RelTo");

        endTo.setMeaning("");
        endTo.setRevisionBehaviour(CloneBehaviour.NONE);
        endTo.setCloneBehaviour(CloneBehaviour.NONE);
        endTo.setCardinality(Cardinality.MANY);
        endTo.setPropagateModify(false);
        endTo.setPropagateConnection(false);

        return controlRelationship;
    }

    @Test
    public void testYAMLRelationshipCiStringifyThenParse() throws Exception {
        RelationshipCI controlRelationship = getControlYAMLRelationship();

        WriterCI writerCI = new YAMLWriter();
        String interfaceCi = writerCI.stringify(controlRelationship);
        System.out.println(interfaceCi);
        Parser<AbstractCI> parser = YAMLParser.fromString(interfaceCi);
        AbstractCI abstractCI = parser.parse();
//        assertEquals(controlRelationship, abstractCI);
        TestUtils.throwIfExpectedNotEqualsActualRelationship(controlRelationship, (RelationshipCI) abstractCI);

        return;
    }

    private RelationshipCI getControlYAMLRelationship() {
        RelationshipCI controlRelationship = new RelationshipCI("IGAKBNBoardLane", CIDiffMode.TARGET);
        //AdminObjectCI
        controlRelationship.setDescription("Connect Kanban board and lanes");
        controlRelationship.setHidden(false);
        controlRelationship.setProperty("MxUpdate Sub Path", "");
        controlRelationship.setProperty("file date", "05-22-2019 14:40:28.000");
        controlRelationship.setProperty("installed date", "03-07-2019");
        controlRelationship.setProperty("installer", "The MxUpdate Team");
        //TypeLikeCI
        controlRelationship.setAbstract(false);
//        controlRelationship.setParentType(new SlothString("Document"));
        controlRelationship.addAttribute("IGAKBNIsDefault");
        controlRelationship.addAttribute("IGAKBNState");
        controlRelationship.addAttribute("IGAKBNIndex");
        //RelationshipCI
        controlRelationship.setPreventDuplicates(true);

        RelCIEnd endFrom = controlRelationship.getEnd(End.FROM);
        endFrom.addType("IGAKBNBoard");
        endFrom.setMeaning("");
        endFrom.setRevisionBehaviour(CloneBehaviour.NONE);
        endFrom.setCloneBehaviour(CloneBehaviour.NONE);
        endFrom.setCardinality(Cardinality.ONE);
        endFrom.setPropagateModify(false);
        endFrom.setPropagateConnection(false);

        RelCIEnd endTo = controlRelationship.getEnd(End.TO);
        endTo.addType("IGAKBNLane");
        endTo.setMeaning("");
        endTo.setRevisionBehaviour(CloneBehaviour.NONE);
        endTo.setCloneBehaviour(CloneBehaviour.NONE);
        endTo.setCardinality(Cardinality.MANY);
        endTo.setPropagateModify(false);
        endTo.setPropagateConnection(false);

        return controlRelationship;
    }

    @Test
    public void testYAMLRelationshipWithUnknownKeywordsCIParsing() throws ParserException {
        String ciTestData = getTestYAMLRelationshipWithUnknownKeywords();
        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        assertThrows(ParserException.class, parser::parse);
    }

    private String getTestYAMLRelationshipWithUnknownKeywords() {
        return "_mode: DIFF           #Тип задан в режиме DIFF. Необязательное свойство, default: TARGET\n" +
                "adminType: relationship\n" +
                "name: IGAKBNBoardLane\n" +
                "abstract: false\n" +
                "dynamic: false\n" +
                "compositional: false\n" +
                "description: Connect Kanban board and lanes\n" +
                "sparse: false\n" +
                "attributes:\n" +
                "  - IGAKBNIsDefault\n" +
                "  - IGAKBNState\n" +
                "  - IGAKBNIndex\n" +
                "from:\n" +
                "  types: \n" +
                "    - IGAKBNBoard\n" +
                "  relationships: \n" +
                "    - none\n" +
                "  meaning: ''\n" +
                "  revision: none\n" +
                "  clone: none\n" +
                "  cardinality: one\n" +
                "  propagateModify: false\n" +
                "  propagateConnection: false\n" +
                "to:\n" +
                "  types: \n" +
                "    - IGAKBNLane\n" +
                "  relationships: \n" +
                "    - none\n" +
                "  meaning: ''\n" +
                "  revision: none\n" +
                "  clone: none\n" +
                "  cardinality: many\n" +
                "  propagateModify: false\n" +
                "  propagateConnection: false\n" +
                "hidden: false\n" +
                "properties:\n" +
                "  MxUpdate Sub Path: ''\n" +
                "  file date: 05-22-2019 14:40:28.000\n" +
                "  installed date: 03-07-2019\n" +
                "  installer: The MxUpdate Team\n" +
                "preventDuplicates: true";
    }

    //endregion

    //region Page

    @Test
    public void testYAMLPageCIParsing() throws Exception {
        String ciTestData = getTestYAMLPage();
//        String ciTestData = new String(Files.readAllBytes(Paths.get("C:\\Users\\a.klimov\\IGAProjects\\mqlsloth\\docs\\syntax\\Page\\Page.yaml")));
        PageCI controlPage = getControlYAMLPageDIFF();

        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();
        TestUtils.throwIfExpectedNotEqualsActualPage(controlPage, (PageCI) abstractCI);
    }

    private String getTestYAMLPage() throws IOException {
        File file = new File(getClass().getResource("Page.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private PageCI getControlYAMLPageDIFF() {
        PageCI controlPage = new PageCI("IGAKBNStringResource_en.properties");
        //AdminObjectCI
        controlPage.setDescription("IGA Kanban widget");
        controlPage.setHidden(false);
        controlPage.setProperty("MxUpdate Sub Path", "");
        controlPage.setProperty("file date", "02-08-2019 08:36:19.376");
        controlPage.setProperty("installed date", "02-08-2019");
        controlPage.setProperty("installer", "The MxUpdate Team");
        //PageCI
        controlPage.setMime("");
        controlPage.setContent("# Do not override these items in other string resource files\n" +
                "Filters.Parameters.DueDate.Format = DD.MM.YY\n" +
                "# Do not override these items in other string resource files\n" +
                "\n" +
                "Lane.Create.Title = Create\n" +
                "Lane.Assign.Title = Assign\n" +
                "Lane.Active.Title = Active\n" +
                "Lane.Review.Title = Review\n" +
                "Lane.Complete.Title = Complete\n" +
                "\n" +
                "# COMMON\n" +
                "Button.Edit = Edit\n" +
                "Button.Delete = Delete\n" +
                "\n" +
                "# FILTERS PANEL\n" +
                "Filters.SavedFilters.Label = Saved filters\n" +
                "Filters.SavedFilters.NoFiltersText = No saved filters yet...\n" +
                "Filters.SavedFilters.Input.Placeholder = Save current filter as ...\n" +
                "Filters.SavedFilters.Button.Save = Save\n" +
                "Filters.SavedFilters.Button.Add = Add\n" +
                "Filters.SavedFilters.Button.Add.Tooltip = Save current filter\n" +
                "\n" +
                "Filters.TypeAhead.NotFound = Not found\n" +
                "\n" +
                "Filters.Button.Filter = Filter\n" +
                "Filters.Button.ClearAll.Tooltip = Clear all filters\n" +
                "\n" +
                "Filters.Parameters.Assignee = Assignee\n" +
                "Filters.Parameters.Assignee.Multiple = Assignees\n" +
                "Filters.Parameters.Assignee.Me = Me\n" +
                "Filters.Parameters.Assignee.Placeholder = Name...\n" +
                "Filters.Parameters.Department = Department\n" +
                "Filters.Parameters.Department.Multiple = Departments\n" +
                "Filters.Parameters.Department.My = My department\n" +
                "Filters.Parameters.Department.Placeholder = Department...\n" +
                "Filters.Parameters.Type = Type\n" +
                "Filters.Parameters.Type.Multiple = Types\n" +
                "Filters.Parameters.ExactTask = Exact task\n" +
                "Filters.Parameters.ExactTask.Multiple = Tasks\n" +
                "Filters.Parameters.ExactTask.Placeholder = Task...\n" +
                "Filters.Parameters.Tag = Tag\n" +
                "Filters.Parameters.Tag.Multiple = Tags\n" +
                "Filters.Parameters.Tag.Placeholder = Tag...\n" +
                "Filters.Parameters.Priority = Priority\n" +
                "Filters.Parameters.Priority.Multiple = Priorities\n" +
                "Filters.Parameters.Project = Project\n" +
                "Filters.Parameters.Project.Multiple = Projects\n" +
                "Filters.Parameters.Project.Placeholder = Project...\n" +
                "Filters.Parameters.DueDate = Due date\n" +
                "Filters.Parameters.DueDate.Option.Today = Today\n" +
                "Filters.Parameters.DueDate.Option.NextWeek = Next week\n" +
                "Filters.Parameters.DueDate.Option.NextMonth = Next month\n" +
                "Filters.Parameters.DueDate.Option.Passed = Passed\n" +
                "\n" +
                "\n" +
                "# BOARD\n" +
                "Board.NewTask.Input.Placeholder = Type task title...\n" +
                "Board.NewTask.Button.Create = Create\n" +
                "Board.Kard.TaskType.Tooltip = Task type\n" +
                "Board.Kard.Comments.Tooltip = comments\n" +
                "Board.Kard.Attachments.Tooltip = attachments\n" +
                "\n" +
                "\n" +
                "# TASK PAGE\n" +
                "TaskPage.Property.State = Current state\n" +
                "TaskPage.Property.Priority = Priority\n" +
                "TaskPage.Property.Priority.Normal = Normal\n" +
                "TaskPage.Property.Priority.Critical = Critical\n" +
                "TaskPage.Property.Percent = Complete percent\n" +
                "TaskPage.Property.Subscription = Subscription\n" +
                "TaskPage.Property.Subscription.Action.Subscribe = Subscribe\n" +
                "TaskPage.Property.Subscription.Action.Unubscribe = Unsubscribe\n" +
                "TaskPage.Property.Subscription.Action.Push = Push subscription\n" +
                "TaskPage.Property.Subscription.YouAre = You are\n" +
                "TaskPage.Property.Subscription.Not = not\n" +
                "TaskPage.Property.Subscription.Subscribed = subscribed\n" +
                "TaskPage.Property.DueDate = Due date\n" +
                "TaskPage.Property.CreationDate = Creation date\n" +
                "TaskPage.Property.Assignee = Assignees\n" +
                "TaskPage.Property.Assignee.NoAssigneesText = No assignee\n" +
                "TaskPage.Property.Tag = Tags\n" +
                "TaskPage.Property.Tag.NoTagsText = No tags\n" +
                "TaskPage.Property.Owner = Owner\n" +
                "\n" +
                "# TASK PAGE: ATTACHMENTS\n" +
                "TaskPage.Att.Label = Attachments\n" +
                "TaskPage.Att.Label.Context = Context\n" +
                "TaskPage.Att.Label.Peferences = References\n" +
                "TaskPage.Att.Label.Deliverables = Deliverables\n" +
                "TaskPage.Att.NoAttText = No attachments\n" +
                "\n" +
                "\n" +
                "# TASK PAGE: HISTORY\n" +
                "TaskPage.History.Label = Task history\n" +
                "\n" +
                "\n" +
                "# TASK PAGE: COMMENTS\n" +
                "TaskPage.Comments.Label = Comments\n" +
                "TaskPage.Comments.Button.Comment = Comment\n" +
                "\n" +
                "\n" +
                "# VIEWS\n" +
                "View.DSpaceLink.Title = Show in 3DSpace\n" +
                "View.DueDate.NoDueDateText = no due date\n");

        return controlPage;
    }

    //endregion


    //region NumberGenerator

    @Test
    public void testYAMLNumberGeneratorCIParsing() throws Exception {
        String ciTestData = getTestYAMLNumberGenerator();
        NumberGeneratorCI controlRelationship = getControlYAMLNumberGeneratorDIFF();

        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();
        TestUtils.throwIfExpectedNotEqualsActualAbstractBusCi((NumberGeneratorCI) abstractCI, controlRelationship);
    }

    private String getTestYAMLNumberGenerator() throws IOException {
        File file = new File(getClass().getResource("NumberGenerator.yaml").getFile());
        return FileUtils.readFileToString(file);
    }

    private NumberGeneratorCI getControlYAMLNumberGeneratorDIFF() {
        NumberGeneratorCI numberGenerator = new NumberGeneratorCI("type_IGAKBNChecklist", "");
        //AdminObjectCI
        numberGenerator.setDescription("");
//        numberGenerator.setPolicy("eService Object Generator");
        numberGenerator.setState("Exists");

        numberGenerator.setAttribute("MxUpdate Installed Date", "05-20-2019");
        numberGenerator.setAttribute("MxUpdate File Date", "05-22-2019 14:40:28.000");
        numberGenerator.setAttribute("MxUpdate Sub Path", "");
        numberGenerator.setAttribute("MxUpdate Installer", "The MxUpdate Team");
        numberGenerator.setAttribute("eService Next Number", "0000001");

        return numberGenerator;
    }

//    @Test
//    public void testYAMLNumberGeneratorCiStringifyThenParse() throws Exception {
//        RelationshipCI controlRelationship = getControlYAMLRelationship();
//
//        WriterCI writerCI = new YAMLWriter();
//        String interfaceCi = writerCI.stringify(controlRelationship);
//        System.out.println(interfaceCi);
//        Parser<AbstractCI> parser = YAMLParser.fromString(interfaceCi);
//        AbstractCI abstractCI = parser.parse();
////        assertEquals(controlRelationship, abstractCI);
//        TestUtils.throwIfExpectedNotEqualsActualRelationship(controlRelationship, (RelationshipCI) abstractCI);
//
//        return;
//    }
//    private RelationshipCI getControlYAMLNumberGenerator() {
//        RelationshipCI controlRelationship = new RelationshipCI("IGAKBNBoardLane", CIDiffMode.TARGET);
//        //AdminObjectCI
//        controlRelationship.setDescription("Connect Kanban board and lanes");
//        controlRelationship.setHidden(false);
//        controlRelationship.setProperty("MxUpdate Sub Path", "");
//        controlRelationship.setProperty("file date", "05-22-2019 14:40:28.000");
//        controlRelationship.setProperty("installed date", "03-07-2019");
//        controlRelationship.setProperty("installer", "The MxUpdate Team");
//        //TypeLikeCI
//        controlRelationship.setAbstract(false);
////        controlRelationship.setParentType(new SlothString("Document"));
//        controlRelationship.addAttribute("IGAKBNIsDefault");
//        controlRelationship.addAttribute("IGAKBNState");
//        controlRelationship.addAttribute("IGAKBNIndex");
//        //RelationshipCI
//        controlRelationship.setPreventDuplicates(true);
//
//        RelCIEnd endFrom = controlRelationship.getEnd(End.FROM);
//        endFrom.addType("IGAKBNBoard");
//        endFrom.addRelationship("none");
//        endFrom.setMeaning("");
//        endFrom.setRevisionBehaviour(CloneBehaviour.NONE);
//        endFrom.setCloneBehaviour(CloneBehaviour.NONE);
//        endFrom.setCardinality(Cardinality.ONE);
//        endFrom.setPropagateModify(false);
//        endFrom.setPropagateConnection(false);
//
//        RelCIEnd endTo = controlRelationship.getEnd(End.TO);
//        endTo.addType("IGAKBNLane");
//        endTo.addRelationship("none");
//        endTo.setMeaning("");
//        endTo.setRevisionBehaviour(CloneBehaviour.NONE);
//        endTo.setCloneBehaviour(CloneBehaviour.NONE);
//        endTo.setCardinality(Cardinality.MANY);
//        endTo.setPropagateModify(false);
//        endTo.setPropagateConnection(false);
//
//        return controlRelationship;
//    }
//
//    @Test
//    public void testYAMLNumberGeneratorWithUnknownKeywordsCIParsing() throws ParserException {
//        String ciTestData = getTestYAMLRelationshipWithUnknownKeywords();
//        Parser<AbstractCI> parser = YAMLParser.fromString(ciTestData);
//        assertThrows(ParserException.class, parser::parse);
//    }
//    private String getTestYAMLNumberGeneratorWithUnknownKeywords() {
//        return  "_mode: DIFF           #Тип задан в режиме DIFF. Необязательное свойство, default: TARGET\n" +
//                "adminType: relationship\n" +
//                "name: IGAKBNBoardLane\n" +
//                "abstract: false\n" +
//                "dynamic: false\n" +
//                "compositional: false\n" +
//                "description: Connect Kanban board and lanes\n" +
//                "sparse: false\n" +
//                "attributes:\n" +
//                "  - IGAKBNIsDefault\n" +
//                "  - IGAKBNState\n" +
//                "  - IGAKBNIndex\n" +
//                "from:\n" +
//                "  types: \n" +
//                "    - IGAKBNBoard\n" +
//                "  relationships: \n" +
//                "    - none\n" +
//                "  meaning: ''\n" +
//                "  revision: none\n" +
//                "  clone: none\n" +
//                "  cardinality: one\n" +
//                "  propagateModify: false\n" +
//                "  propagateConnection: false\n" +
//                "to:\n" +
//                "  types: \n" +
//                "    - IGAKBNLane\n" +
//                "  relationships: \n" +
//                "    - none\n" +
//                "  meaning: ''\n" +
//                "  revision: none\n" +
//                "  clone: none\n" +
//                "  cardinality: many\n" +
//                "  propagateModify: false\n" +
//                "  propagateConnection: false\n" +
//                "hidden: false\n" +
//                "properties:\n" +
//                "  MxUpdate Sub Path: ''\n" +
//                "  file date: 05-22-2019 14:40:28.000\n" +
//                "  installed date: 03-07-2019\n" +
//                "  installer: The MxUpdate Team\n" +
//                "preventDuplicates: true";
//    }

    //endregion
}
