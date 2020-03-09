package com.igatec.mqlsloth.parser;

import com.igatec.mqlsloth.ci.AbstractBusCI;
import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.AdminObjectCI;
import com.igatec.mqlsloth.ci.AttributeCI;
import com.igatec.mqlsloth.ci.InterfaceCI;
import com.igatec.mqlsloth.ci.PageCI;
import com.igatec.mqlsloth.ci.RelationshipCI;
import com.igatec.mqlsloth.ci.TypeCI;
import com.igatec.mqlsloth.ci.TypeLikeCI;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.writers.YAMLWriter;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

;

public class TestUtils {

    public static void throwIfExpectedNotEqualsActualType(TypeCI expected, TypeCI actual) throws Exception {
        throwIfExpectedNotEqualsActualTypeLike(expected, actual);

        if (!Objects.equals(expected.isComposed(), actual.isComposed())) {
            throw new Exception("IsComposed are not equals. \n\tExpected: " + expected.isComposed() + "\n\tActual: " + actual.isComposed());
        }

        return;
    }

    public static void throwIfExpectedNotEqualsActualInterface(InterfaceCI expected, InterfaceCI actual) throws Exception {
        throwIfExpectedNotEqualsActualTypeLike(expected, actual);

        Set<String> expectedTypes = expected.getTypes();
        Set<String> actualTypes = actual.getTypes();
        try {
            throwIfExpectedNotEqualsActualCollection(expectedTypes, actualTypes);
        } catch (Exception e) {
            throw new Exception("Types are not equals. \n\tExpected: " + expectedTypes + "\n\tActual: " + actualTypes, e);
        }


        Set<String> expectedRelationships = expected.getRelationships();
        Set<String> actualRelationships = actual.getRelationships();
        try {
            throwIfExpectedNotEqualsActualCollection(expectedRelationships, actualRelationships);
        } catch (Exception e) {
            throw new Exception("Relationships are not equals. \n\tExpected: " + expectedRelationships + "\n\tActual: " + actualRelationships, e);
        }

        return;
    }

    public static void throwIfExpectedNotEqualsActualRelationshipCIEnd(RelationshipCI.RelCIEnd expected, RelationshipCI.RelCIEnd actual) throws Exception {
        Set<String> expectedTypes = expected.getTypes();
        Set<String> actualTypes = actual.getTypes();
        try {
            throwIfExpectedNotEqualsActualCollection(expectedTypes, actualTypes);
        } catch (Exception e) {
            throw new Exception("Types are not equals. \n\tExpected: " + expectedTypes + "\n\tActual: " + actualTypes, e);
        }


        Set<String> expectedRelationships = expected.getRelationships();
        Set<String> actualRelationships = actual.getRelationships();
        try {
            throwIfExpectedNotEqualsActualCollection(expectedRelationships, actualRelationships);
        } catch (Exception e) {
            throw new Exception("Relationships are not equals. \n\tExpected: " + expectedRelationships + "\n\tActual: " + actualRelationships, e);
        }

        if (!Objects.equals(expected.getMeaning(), actual.getMeaning())) {
            throw new Exception("Meaning are not equals. \n\tExpected: " + expected.getMeaning() + "\n\tActual: " + actual.getMeaning());
        }

        if (!Objects.equals(expected.getRevisionBehaviour(), actual.getRevisionBehaviour())) {
            throw new Exception("RevisionBehaviour are not equals. \n\tExpected: " + expected.getRevisionBehaviour() + "\n\tActual: " + actual.getRevisionBehaviour());
        }

        if (!Objects.equals(expected.getCloneBehaviour(), actual.getCloneBehaviour())) {
            throw new Exception("CloneBehaviour are not equals. \n\tExpected: " + expected.getCloneBehaviour() + "\n\tActual: " + actual.getCloneBehaviour());
        }

        if (!Objects.equals(expected.getPropagateModify(), actual.getPropagateModify())) {
            throw new Exception("PropagateModify are not equals. \n\tExpected: " + expected.getPropagateModify() + "\n\tActual: " + actual.getPropagateModify());
        }

        if (!Objects.equals(expected.getPropagateConnection(), actual.getPropagateConnection())) {
            throw new Exception("PropagateConnection are not equals. \n\tExpected: " + expected.getPropagateConnection() + "\n\tActual: " + actual.getPropagateConnection());
        }

        return;
    }

    public static void throwIfExpectedNotEqualsActualRelationship(RelationshipCI expected, RelationshipCI actual) throws Exception {
        throwIfExpectedNotEqualsActualTypeLike(expected, actual);

        if (!Objects.equals(expected.doesPreventDuplicates(), actual.doesPreventDuplicates())) {
            throw new Exception("PreventDuplicates are not equals. \n\tExpected: " + expected.doesPreventDuplicates() + "\n\tActual: " + actual.doesPreventDuplicates());
        }

        RelationshipCI.RelCIEnd expectedFrom = expected.getEnd(RelationshipCI.End.FROM);
        RelationshipCI.RelCIEnd actualFrom = actual.getEnd(RelationshipCI.End.FROM);
        throwIfExpectedNotEqualsActualRelationshipCIEnd(expectedFrom, actualFrom);

        RelationshipCI.RelCIEnd expectedTo = expected.getEnd(RelationshipCI.End.TO);
        RelationshipCI.RelCIEnd actualTo = actual.getEnd(RelationshipCI.End.TO);
        throwIfExpectedNotEqualsActualRelationshipCIEnd(expectedTo, actualTo);

        return;
    }

    public static void throwIfExpectedNotEqualsActualTypeLike(TypeLikeCI expected, TypeLikeCI actual) throws Exception {
        throwIfExpectedNotEqualsActualAdminObject(expected, actual);

        if (!Objects.equals(expected.isAbstract(), actual.isAbstract())) {
            throw new Exception("IsAbstract are not equals. \n\tExpected: " + expected.isAbstract() + "\n\tActual: " + actual.isAbstract());
        }

        if (!Objects.equals(expected.getParentType(), actual.getParentType())) {
            throw new Exception("ParentTypes are not equals. \n\tExpected: " + expected.getParentType() + "\n\tActual: " + actual.getParentType());
        }

        Set<String> expectedAttributes = expected.getAttributes();
        Set<String> actualAttributes = actual.getAttributes();
        try {
            throwIfExpectedNotEqualsActualCollection(expectedAttributes, actualAttributes);
        } catch (Exception e) {
            throw new Exception("Attributes are not equals. \n\tExpected: " + expectedAttributes + "\n\tActual: " + actualAttributes, e);
        }
    }

    public static void throwIfExpectedNotEqualsActualAttribute(AttributeCI expected, AttributeCI actual) throws Exception {
        throwIfExpectedNotEqualsActualAdminObject(expected, actual);
        if (!Objects.equals(expected.getType(), actual.getType())) {
            throw new Exception("AttributeTypes are not equals. \n\tExpected: " + expected.getType() + "\n\tActual: " + actual.getType());
        }

        if (!Objects.equals(expected.isMultivalue(), actual.isMultivalue())) {
            throw new Exception("IsMultivalue are not equals. \n\tExpected: " + expected.isMultivalue() + "\n\tActual: " + actual.isMultivalue());
        }

        if (!Objects.equals(expected.isMultiline(), actual.isMultiline())) {
            throw new Exception("IsMultiline are not equals. \n\tExpected: " + expected.isMultiline() + "\n\tActual: " + actual.isMultiline());
        }

        if (!Objects.equals(expected.getDefaultValue(), actual.getDefaultValue())) {
            throw new Exception("DefaultValues are not equals. \n\tExpected: " + expected.getDefaultValue() + "\n\tActual: " + actual.getDefaultValue());
        }

        Set<String> expectedRanges = expected.getRange();
        Set<String> actualRanges = actual.getRange();
        try {
            throwIfExpectedNotEqualsActualCollection(expectedRanges, actualRanges);
        } catch (Exception e) {
            throw new Exception("Ranges are not equals. \n\tExpected: " + expectedRanges + "\n\tActual: " + actualRanges, e);
        }

        if (!Objects.equals(expected.getMaxLength(), actual.getMaxLength())) {
            throw new Exception("MaxLength are not equals. \n\tExpected: " + expected.getMaxLength() + "\n\tActual: " + actual.getMaxLength());
        }

        if (!Objects.equals(expected.isResetOnClone(), actual.isResetOnClone())) {
            throw new Exception("IsResetOnClone are not equals. \n\tExpected: " + expected.isResetOnClone() + "\n\tActual: " + actual.isResetOnClone());
        }

        if (!Objects.equals(expected.isResetOnRevision(), actual.isResetOnRevision())) {
            throw new Exception("IsResetOnRevision are not equals. \n\tExpected: " + expected.isResetOnRevision() + "\n\tActual: " + actual.isResetOnRevision());
        }
    }

    public static void throwIfExpectedNotEqualsActualPage(PageCI expected, PageCI actual) throws Exception {
        throwIfExpectedNotEqualsActualAdminObject(expected, actual);
        if (!Objects.equals(expected.getMime(), actual.getMime())) {
            throw new Exception("Mime are not equals. \n\tExpected: " + expected.getMime() + "\n\tActual: " + actual.getMime());
        }

        if (!Objects.equals(expected.getContent(), actual.getContent())) {
            throw new Exception("Contents are not equals. \n\tExpected: " + expected.getContent() + "\n\tActual: " + actual.getContent());
        }
    }

    public static void throwIfExpectedNotEqualsActualAdminObject(AdminObjectCI expected, AdminObjectCI actual) throws Exception {
        throwIfExpectedNotEqualsActualAbstract(expected, actual);

        if (!Objects.equals(expected.isHidden(), actual.isHidden())) {
            throw new Exception("IsHidden are not equals. \n\tExpected: " + expected.isHidden() + "\n\tActual: " + actual.isHidden());
        }

        Map<String, String> expectedProperties = expected.getProperties();
        Map<String, String> actualProperties = actual.getProperties();
        if (!Objects.equals(expectedProperties, actualProperties)) {
            throw new Exception("Properties are not equals. \n\tExpected: " + expectedProperties + "\n\tActual: " + actualProperties);
        }
    }

    public static void throwIfExpectedNotEqualsActualAbstractBusCi(AbstractBusCI expected, AbstractBusCI actual) throws Exception {
        throwIfExpectedNotEqualsActualAbstract(expected, actual);

        if (!Objects.equals(expected.getPolicy(), actual.getPolicy())) {
            throw new Exception("Policies are not equals. \n\tExpected: " + expected.getPolicy() + "\n\tActual: " + actual.getPolicy());
        }

        if (!Objects.equals(expected.getState(), actual.getState())) {
            throw new Exception("States are not equals. \n\tExpected: " + expected.getState() + "\n\tActual: " + actual.getState());
        }

        if (!Objects.equals(expected.getVault(), actual.getVault())) {
            throw new Exception("Vaults are not equals. \n\tExpected: " + expected.getVault() + "\n\tActual: " + actual.getVault());
        }

        Map<String, String> expectedAttributes = expected.getAttributes();
        Map<String, String> actualAttributes = actual.getAttributes();
        if (!Objects.equals(expectedAttributes, actualAttributes)) {
            throw new Exception("Attributes are not equals. \n\tExpected: " + expectedAttributes + "\n\tActual: " + actualAttributes);
        }
    }

    public static void throwIfExpectedNotEqualsActualAbstract(AbstractCI expected, AbstractCI actual) throws Exception {
        if (!Objects.equals(expected.getSlothAdminType(), actual.getSlothAdminType())) {
            throw new Exception("AdminTypes are not equals. \n\tExpected: " + expected.getSlothAdminType() + "\n\tActual: " + actual.getSlothAdminType());
        }

        if (!Objects.equals(expected.getCIName(), actual.getCIName())) {
            throw new Exception("CINames are not equals. \n\tExpected: " + expected.getCIName() + "\n\tActual: " + actual.getCIName());
        }

        if (!Objects.equals(expected.getDescription(), actual.getDescription())) {
            throw new Exception("Descriptions are not equals. \n\tExpected: " + expected.getDescription() + "\n\tActual: " + actual.getDescription());
        }
    }

    public static void throwIfExpectedNotEqualsActualCollection(Collection expected, Collection actual) throws Exception {
        if (expected.size() != actual.size()) {
            throw new Exception("Different size of collections");
        }

        if (!expected.containsAll(actual)) {
            throw new Exception("Different collections");
        }

        return;
    }

    public static void writeToYamlFileIfNotExist(Path file, AbstractCI abstractCI) throws IOException, ParserException {
        boolean exists = Files.exists(file);
        if (!exists) {
            writeToYamlFile(file, abstractCI);
        }
    }

    public static void writeToYamlFile(Path file, AbstractCI abstractCI) throws IOException, ParserException {
        YAMLWriter yamlWriter = new YAMLWriter();
        String ciDefinition = yamlWriter.stringify(abstractCI);
        writeToFile(file, ciDefinition);
    }

    public static void writeToFile(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.createFile(file);

        FileUtils.writeStringToFile(file.toFile(), content);
    }

    public static String getNullExceprionMessage(String adminType, String objectName) {
        String nullMessageFormat = "Can't parse %s %s. Object is null";
        return String.format(nullMessageFormat, adminType, objectName);
    }

    public static Context createContext(String host, String user, String password) throws Exception {
        Context context = Context.instance(host);
        context.setUser(user);
        context.setPassword(password);
        context.connect();
        if (!context.isConnected()) {
            throw new Exception("Context isn't connected");
        }
        return context;
    }
}
