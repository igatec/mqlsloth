package com.igatec.mqlsloth.parser;

import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.parser.objects.AbstractObjectParser;
import com.igatec.mqlsloth.parser.objects.AttributeObjectParser;
import com.igatec.mqlsloth.parser.objects.ChannelObjectParser;
import com.igatec.mqlsloth.parser.objects.CommandObjectParser;
import com.igatec.mqlsloth.parser.objects.ExpressionObjectParser;
import com.igatec.mqlsloth.parser.objects.FormObjectParser;
import com.igatec.mqlsloth.parser.objects.GroupObjectParser;
import com.igatec.mqlsloth.parser.objects.InterfaceObjectParser;
import com.igatec.mqlsloth.parser.objects.MenuObjectParser;
import com.igatec.mqlsloth.parser.objects.NumberGeneratorObjectParser;
import com.igatec.mqlsloth.parser.objects.ObjectGeneratorObjectParser;
import com.igatec.mqlsloth.parser.objects.ObjectParser;
import com.igatec.mqlsloth.parser.objects.PageObjectParser;
import com.igatec.mqlsloth.parser.objects.PolicyObjectParser;
import com.igatec.mqlsloth.parser.objects.PortalObjectParser;
import com.igatec.mqlsloth.parser.objects.ProgramObjectParser;
import com.igatec.mqlsloth.parser.objects.RelationshipObjectParser;
import com.igatec.mqlsloth.parser.objects.RoleObjectParser;
import com.igatec.mqlsloth.parser.objects.TableObjectParser;
import com.igatec.mqlsloth.parser.objects.TriggerObjectParser;
import com.igatec.mqlsloth.parser.objects.TypeObjectParser;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Parser<T> implements ReaderCIParser, KeyWordFinder {
    public static final String ADMIN_TYPE_KEY = "adminType";
    public static final String ADMIN_TYPE_VALUE = "name";

    protected String ciData;
    protected ObjectParser objectParser;
    protected AbstractObjectParser.Format format;

    protected void setObjectParser(ObjectParser objectParser) {
        this.objectParser = objectParser;
    }

    public final T parse() throws ParserException {
        try {
            T abstractCI = parseObject();
            return abstractCI;
        } catch (ParserException e) {
            throw e;
        } catch (Exception e) {
            ParserException parserException = new ParserException();
            parserException.initCause(e);
            throw parserException;
        }
    }

    protected final T parseObject() throws Exception {
        beforeParseObject();
        T parsedObject = parseConcreteObject();
        afterParseObject();

        return parsedObject;
    }

    protected void beforeParseObject() throws Exception {
        throwIfCiDataNullOrEmpty();

        if (objectParser == null) {
            makeObjectParserIfNotSet();
        }

        return;
    }

    private void throwIfCiDataNullOrEmpty() throws Exception {
        if (ciData == null) {
            Exception nullPointerException = new NullPointerException("String to parse is null. Set the string.");
            throw nullPointerException;
        }

        if (ciData.isEmpty()) {
            Exception emptyStringException = new IllegalArgumentException("String to parse is empty. Set the string.");
            throw emptyStringException;
        }
    }

    protected abstract void makeObjectParserIfNotSet() throws Exception;

    protected void afterParseObject() throws Exception {
        return;
    }

    protected final T parseConcreteObject() throws ParserException {

        Map<String, Function> keyWords = objectParser.getKeyWordsToValueMakers();
        Map<String, Object> fieldsValues = getValuesForKeys(keyWords);
        T parsedObject = (T) objectParser.createFilledObject(fieldsValues);

        return parsedObject;
    }

    protected final ObjectParser getObjectParser(SlothAdminType adminType) throws ParserException {
        switch (adminType) {
            case TYPE:
                return new TypeObjectParser(format);
            case ATTRIBUTE:
                return new AttributeObjectParser(format);
            case INTERFACE:
                return new InterfaceObjectParser(format);
            case RELATIONSHIP:
                return new RelationshipObjectParser(format);
            case PAGE:
                return new PageObjectParser(format);
            case PROGRAM:
                return new ProgramObjectParser(format);
            case ROLE:
                return new RoleObjectParser(format);
            case POLICY:
                return new PolicyObjectParser(format);
            case GROUP:
                return new GroupObjectParser(format);
            case NUMBER_GENERATOR:
                return new NumberGeneratorObjectParser(format);
            case OBJECT_GENERATOR:
                return new ObjectGeneratorObjectParser(format);
            case TRIGGER:
                return new TriggerObjectParser(format);
            case COMMAND:
                return new CommandObjectParser(format);
            case MENU:
                return new MenuObjectParser(format);
            case CHANNEL:
                return new ChannelObjectParser(format);
            case PORTAL:
                return new PortalObjectParser(format);
            case EXPRESSION:
                return new ExpressionObjectParser(format);
            case FORM:
                return new FormObjectParser(format);
            case WEB_TABLE:
                return new TableObjectParser(format);
            default:
                throw new ParserException("Can't find parser for admin type " + adminType);
        }
    }

    protected abstract List<String> getReservedKeysForCreationObject();
}
