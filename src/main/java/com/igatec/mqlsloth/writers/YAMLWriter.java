package com.igatec.mqlsloth.writers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.Objects;

import static com.igatec.mqlsloth.script.YAMLKeywords.Y_MODE;

public class YAMLWriter implements WriterCI {
    private ObjectMapper mapper;

    public YAMLWriter() {
        initObjectMapper();
    }

    private void initObjectMapper() {
        this.mapper = new ObjectMapper(new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .disable(YAMLGenerator.Feature.SPLIT_LINES)
        );
    }

    @Override
    public String stringify(AbstractCI ci) throws ParserException {
        try {
            return makeObjectString(ci);
        } catch (JsonProcessingException e) {
            ParserException parserException = new ParserException();
            parserException.initCause(e);
            throw parserException;
        }
    }

    private String makeObjectString(AbstractCI ci) throws JsonProcessingException {
        Map<String, Object> fieldsValues = ci.toMap();

        CIDiffMode mode = (CIDiffMode) fieldsValues.get(Y_MODE);
        if (mode.equals(CIDiffMode.TARGET)) {
            fieldsValues.keySet().removeIf(key -> key.equals(Y_MODE));
        }

        if (mode.equals(CIDiffMode.DIFF)) {
            fieldsValues.values().removeIf(Objects::isNull);
        }

        return mapper.writeValueAsString(fieldsValues);
    }
}
