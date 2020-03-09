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
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.util.SlothString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestParserMQL {

    @Test
    public void testMqlTypeCIParsing() throws ParserException {
        String ciTestData = getTestMQLType();
        TypeCI controlType = getControlMQLType();

        Parser<AbstractCI> parser = MqlParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();
        assertEquals(controlType, abstractCI);
    }

    private String getTestMQLType() {
        return "type ARCHDocument\n" +
                "  derived Document\n" +
                "  abstract true\n" +
                "  composed true\n" +
                "  description Scanned sheet with inventory number\n" +
                "  attribute ARCHStageOfProject,ARCHSheetsNumber,ARCHSheetNumber,ARCHSheetFormat,ARCHNotes,ARCHChangeStatus,ARCHChangeNumber,ARCHApprovalNumber,ARCHReferenceObject,ARCHDocumentType\n" +
                "  inherited attribute Access Type,Checkin Reason,Language,Originator,clau,File Created Date,File Dimension,File Duration,File Modified Date,File Size,File Type,Is Version Object,Move Files To Version,Suspend Versioning,Title,Designated User,File Version,Version Date,Version,CAD Type,Primary Key,Secondary Keys\n" +
                "  inherited trigger CreateAction:emxTriggerManager(TypeAllCreateAction TypeDocumentCreateAction),RevisionAction:emxTriggerManager(TypeAllReviseAction TypeDOCUMENTSReviseAction TypeDocumentReviseAction),DeleteCheck:emxTriggerManager(TypeDOCUMENTSDeleteCheck TypeDocumentDeleteCheck),GrantAction:emxTriggerManager(TypeDocumentGrantAction),CheckinAction:emxTriggerManager(TypeDOCUMENTSCheckinAction TypeDocumentCheckinAction),CheckinCheck:emxTriggerManager(TypeDOCUMENTSCheckinCheck),CopyAction:emxTriggerManager(DomainAccessClearInheritedOwnershipOnCloneAction TypeDocumentCopyAction),RevisionCheck:emxTriggerManager(TypeDocumentRevisionCheck),CreateAction:emxTriggerManager(TypeAllCreateAction TypeDOCUMENTSCreateAction),RevisionAction:emxTriggerManager(TypeAllReviseAction TypeDOCUMENTSReviseAction),ModifyAttributeAction:emxTriggerManager(TypeDOCUMENTSModifyAttributeAction),DeleteAction:emxTriggerManager(TypeAllDeleteAction TypeDOCUMENTSDeleteAction),DisconnectAction:emxTriggerManager(TypeDOCUMENTSDisconnectAction),CheckoutAction:emxTriggerManager(TypeDOCUMENTSCheckoutAction),ChangeNameAction:emxTriggerManager(TypeDOCUMENTSChangeNameAction),ModifyDescriptionAction:emxTriggerManager(TypeDOCUMENTSModifyDescriptionAction),ModifyAction:emxTriggerManager(TypeDOCUMENTSModifyAction),DeleteCheck:emxTriggerManager(TypeDOCUMENTSDeleteCheck),TransactionAction:emxTriggerManager(TypeAllTransactionAction),CheckinAction:emxTriggerManager(TypeDOCUMENTSCheckInAction),LockAction:emxTriggerManager(TypeDOCUMENTSLockAction),UnlockAction:emxTriggerManager(TypeDOCUMENTSUnlockAction)\n" +
                "  policy Document Release,Version\n" +
                "  nothidden\n" +
                "  property Specializable value Yes\n" +
                "  property UUID value 56284838-d843-11e8-9f8b-f2801f1b9fd1\n" +
                "  created 7/27/2018 2:33:15 AM\n" +
                "  modified 11/12/2018 6:18:20 PM";
    }

    private TypeCI getControlMQLType() {
        TypeCI controlType = new TypeCI("ARCHDocument");
        //AdminObjectCI
        controlType.setDescription("Scanned sheet with inventory number");
        controlType.setHidden(false);
        controlType.setProperty("Specializable", "Yes");
        controlType.setProperty("UUID", "56284838-d843-11e8-9f8b-f2801f1b9fd1");
        //TypeLikeCI
        controlType.setAbstract(true);
        controlType.setParentType(new SlothString("Document"));
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
        //TypeCI
        controlType.setComposed(true);
        return controlType;
    }

    @Test
    public void testMqlAttributeCIParsing() throws ParserException {
        String ciTestData = getTestMqlAttribute();
        AttributeCI controlAttribute = getControlMQLAttribute();

        Parser<AbstractCI> parser = MqlParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();

        assertEquals(controlAttribute, abstractCI);
    }

    private AttributeCI getControlMQLAttribute() {
        AttributeCI controlAttribute = new AttributeCI("IGAKBNState", AttributeType.STRING);
        //AdminObjectCI
        controlAttribute.setDescription("Stores OOTB Task states");
        controlAttribute.setHidden(false);
        controlAttribute.setProperty("MxUpdate Sub Path", "");
        controlAttribute.setProperty("file date", "05-22-2019 14:40:28.000");
        controlAttribute.setProperty("installed date", "03-07-2019");
        controlAttribute.setProperty("installer", "The MxUpdate Team");
        //AttributeCI
        controlAttribute.setMultivalue(true);
        controlAttribute.setMultiline(false);
        controlAttribute.setDefaultValue(new SlothString("Create"));
        controlAttribute.addRange("Active");
        controlAttribute.addRange("Assign");
        controlAttribute.addRange("Complete");
        controlAttribute.addRange("Create");
        controlAttribute.addRange("Review");
        controlAttribute.setMaxLength(0);
        controlAttribute.setResetOnClone(false);
        controlAttribute.setResetOnRevision(false);
        return controlAttribute;
    }

    private String getTestMqlAttribute() {
        return "attribute IGAKBNState\n" +
                "  type string\n" +
                "  description Stores OOTB Task states\n" +
                "  default Create\n" +
                "  range = Active\n" +
                "  range = Assign\n" +
                "  range = Complete\n" +
                "  range = Create\n" +
                "  range = Review\n" +
                "  trigger ModifyCheck:emxTriggerManager(AttributeIGAKBNStateModifyCheck)\n" +
                "  notmultiline\n" +
                "  multivalue\n" +
                "  notrangevalue\n" +
                "  maxlength 0\n" +
                "  notresetOnClone\n" +
                "  notresetOnRevision\n" +
                "  nothidden\n" +
                "  property MxUpdate Sub Path\n" +
                "  property file date value 05-22-2019 14:40:28.000\n" +
                "  property installed date value 03-07-2019\n" +
                "  property installer value The MxUpdate Team\n" +
                "  created 3/7/2019 8:35:02 AM\n" +
                "  modified 5/22/2019 7:16:38 PM";
    }

    @Test
    public void testMqlInterfaceCIParsing() throws ParserException {
        String ciTestData = getTestMQLInterface();
        InterfaceCI controlInterface = getControlInterface();

        Parser<AbstractCI> parser = MqlParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();

        assertEquals(controlInterface, abstractCI);
    }

    private String getTestMQLInterface() {
        return "interface IGAKBNExtension\n" +
                "  abstract true\n" +
                "  description \n" +
                "  attribute IGAKBNUID,IGAKBNPriority\n" +
                "  type Task,Inbox Task\n" +
                "  relationship Route Node,Test Relationship\n" +
                "  nothidden\n" +
                "  property MxUpdate Sub Path\n" +
                "  property file date value 05-22-2019 15:35:54.000\n" +
                "  property installed date value 05-22-2019\n" +
                "  property installer value The MxUpdate Team";
    }

    private InterfaceCI getControlInterface() {
        InterfaceCI controlInterface = new InterfaceCI("IGAKBNExtension");
        //AdminObjectCI
        controlInterface.setDescription("");
        controlInterface.setHidden(false);
        controlInterface.setProperty("MxUpdate Sub Path", "");
        controlInterface.setProperty("file date", "05-22-2019 15:35:54.000");
        controlInterface.setProperty("installed date", "05-22-2019");
        controlInterface.setProperty("installer", "The MxUpdate Team");
        //TypeLikeCI
        controlInterface.setAbstract(true);
        controlInterface.addAttribute("IGAKBNUID");
        controlInterface.addAttribute("IGAKBNPriority");
        //InterfaceCI
        controlInterface.addType("Task");
        controlInterface.addType("Inbox Task");
        controlInterface.addRelationship("Route Node");
        controlInterface.addRelationship("Test Relationship");
        return controlInterface;
    }

    private String getTestMQLRelationship() {
        return "relationship IGAKBNBoardLane\n" +
                "  abstract false\n" +
                "  dynamic false\n" +
                "  compositional false\n" +
                "  description Connect Kanban board and lanes\n" +
                "  sparse false\n" +
                "  attribute IGAKBNIsDefault,IGAKBNState,IGAKBNIndex\n" +
                "  from\n" +
                "    type IGAKBNBoard\n" +
                "    relationship none\n" +
                "    meaning \n" +
                "    revision none\n" +
                "    clone none\n" +
                "    cardinality one\n" +
                "    propagate modify true\n" +
                "    propagate connection false\n" +
                "  to\n" +
                "    type IGAKBNLane\n" +
                "    relationship none\n" +
                "    meaning \n" +
                "    revision none\n" +
                "    clone none\n" +
                "    cardinality many\n" +
                "    propagate modify true\n" +
                "    propagate connection false\n" +
                "  nothidden\n" +
                "  property MxUpdate Sub Path\n" +
                "  property file date value 05-22-2019 14:40:28.000\n" +
                "  property installed date value 03-07-2019\n" +
                "  property installer value The MxUpdate Team\n" +
                "  created 3/7/2019 8:41:02 AM\n" +
                "  modified 5/22/2019 7:16:40 PM\n" +
                "  preventduplicates";
    }

    private RelationshipCI getControlMQLRelationship() {
        RelationshipCI controlRelationship = new RelationshipCI("IGAKBNBoardLane");
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
        endFrom.setPropagateModify(true);
        endFrom.setPropagateConnection(false);

        RelCIEnd endTo = controlRelationship.getEnd(End.TO);
        endTo.addType("IGAKBNLane");
        endTo.setMeaning("");
        endTo.setRevisionBehaviour(CloneBehaviour.NONE);
        endTo.setCloneBehaviour(CloneBehaviour.NONE);
        endTo.setCardinality(Cardinality.MANY);
        endTo.setPropagateModify(true);
        endTo.setPropagateConnection(false);
        return controlRelationship;
    }

    @Test
    public void testMqlPageCIParsing() throws Exception {
        String ciTestData = getTestMQLPage();
        PageCI controlPage = getControlMQLPage();

        Parser<AbstractCI> parser = MqlParser.fromString(ciTestData);
        AbstractCI abstractCI = parser.parse();
//        assertEquals(controlPage, abstractCI);
        TestUtils.throwIfExpectedNotEqualsActualPage(controlPage, (PageCI) abstractCI);
    }

    private String getTestMQLPage() {
        return "page  IGAKBNStringResource_en.properties\n" +
                "  description IGA Kanban widget\n" +
                "  content \n" +
                "# Do not override these items in other string resource files\n" +
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
                "View.DueDate.NoDueDateText = no due date\n" +
                "\n" +
                "\n" +
                "  mime  \n" +
                "  nothidden\n" +
                "  property MxUpdate Sub Path\n" +
                "  property file date value 02-08-2019 08:36:19.376\n" +
                "  property installed date value 02-08-2019\n" +
                "  property installer value The MxUpdate Team\n" +
                "  created 2/8/2019 11:36:20 AM\n" +
                "  modified 2/8/2019 11:36:20 AM";
    }

    private PageCI getControlMQLPage() {
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
        controlPage.setContent("\n# Do not override these items in other string resource files\n" +
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
                "View.DueDate.NoDueDateText = no due date\n" +
                "\n");

        return controlPage;
    }

    @Test
    public void testMqlNumberGeneratorCIParsing() throws Exception {
        String ciTestData = getTestMQLNumberGenerator();
        NumberGeneratorCI controlNumberGenerator = getControlMQLNumberGenerator();

        Parser<AbstractCI> parser = MqlParser.fromStringAndObjectType(ciTestData, SlothAdminType.NUMBER_GENERATOR);
        AbstractCI abstractCI = parser.parse();
        TestUtils.throwIfExpectedNotEqualsActualAbstractBusCi(controlNumberGenerator, (NumberGeneratorCI) abstractCI);
    }

    private String getTestMQLNumberGenerator() {
        return "business object  eService Number Generator type_IGAKBNChecklist \n" +
                "    name = type_IGAKBNChecklist\n" +
                "    revision =\n" +
                "    policy = eService Object Generator\n" +
                "    state = Exists\n" +
                "    vault = eService Administration\n" +
                "    attribute[MxUpdate Installed Date].type = string\n" +
                "    attribute[MxUpdate File Date].type = string\n" +
                "    attribute[MxUpdate Sub Path].type = string\n" +
                "    attribute[MxUpdate Installer].type = string\n" +
                "    attribute[eService Next Number].type = string\n" +
                "    attribute[MxUpdate Installed Date].value = 05-20-2019\n" +
                "    attribute[MxUpdate File Date].value = 05-22-2019 14:40:28.000\n" +
                "    attribute[MxUpdate Sub Path].value = \n" +
                "    attribute[MxUpdate Installer].value = The MxUpdate Team\n" +
                "    attribute[eService Next Number].value = 0000001\n" +
                "    attribute[MxUpdate Installed Date].rule = all\n" +
                "    attribute[MxUpdate File Date].rule = all\n" +
                "    attribute[MxUpdate Sub Path].rule = all\n" +
                "    attribute[MxUpdate Installer].rule = all\n" +
                "    attribute[eService Next Number].rule = all\n" +
                "    attribute[MxUpdate Installed Date].inputvalue = 05-20-2019\n" +
                "    attribute[MxUpdate File Date].inputvalue = 05-22-2019 14:40:28.000\n" +
                "    attribute[MxUpdate Sub Path].inputvalue = \n" +
                "    attribute[MxUpdate Installer].inputvalue = The MxUpdate Team\n" +
                "    attribute[eService Next Number].inputvalue = 0000001\n" +
                "    attribute[MxUpdate Installed Date].inputunit = \n" +
                "    attribute[MxUpdate File Date].inputunit = \n" +
                "    attribute[MxUpdate Sub Path].inputunit = \n" +
                "    attribute[MxUpdate Installer].inputunit = \n" +
                "    attribute[eService Next Number].inputunit = \n" +
                "    attribute[MxUpdate Installed Date].dbvalue = 05-20-2019\n" +
                "    attribute[MxUpdate File Date].dbvalue = 05-22-2019 14:40:28.000\n" +
                "    attribute[MxUpdate Sub Path].dbvalue = \n" +
                "    attribute[MxUpdate Installer].dbvalue = The MxUpdate Team\n" +
                "    attribute[eService Next Number].dbvalue = 0000001\n" +
                "    attribute[MxUpdate Installed Date].dbunit = \n" +
                "    attribute[MxUpdate File Date].dbunit = \n" +
                "    attribute[MxUpdate Sub Path].dbunit = \n" +
                "    attribute[MxUpdate Installer].dbunit = \n" +
                "    attribute[eService Next Number].dbunit = \n" +
                "    attribute[MxUpdate Installed Date].unitvalue = 05-20-2019\n" +
                "    attribute[MxUpdate File Date].unitvalue = 05-22-2019 14:40:28.000\n" +
                "    attribute[MxUpdate Sub Path].unitvalue = \n" +
                "    attribute[MxUpdate Installer].unitvalue = The MxUpdate Team\n" +
                "    attribute[eService Next Number].unitvalue = 0000001\n" +
                "    attribute[MxUpdate Installed Date].generic = 05-20-2019\n" +
                "    attribute[MxUpdate File Date].generic = 05-22-2019 14:40:28.000\n" +
                "    attribute[MxUpdate Sub Path].generic = \n" +
                "    attribute[MxUpdate Installer].generic = The MxUpdate Team\n" +
                "    attribute[eService Next Number].generic = 0000001\n" +
                "    attribute[MxUpdate Installed Date].size = 1\n" +
                "    attribute[MxUpdate File Date].size = 1\n" +
                "    attribute[MxUpdate Sub Path].size = 1\n" +
                "    attribute[MxUpdate Installer].size = 1\n" +
                "    attribute[eService Next Number].size = 1\n";
    }

    private NumberGeneratorCI getControlMQLNumberGenerator() {
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
}
