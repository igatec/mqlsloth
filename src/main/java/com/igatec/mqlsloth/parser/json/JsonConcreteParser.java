package com.igatec.mqlsloth.parser.json;

import com.igatec.mqlsloth.ci.AbstractCI;

import java.util.Map;

public interface JsonConcreteParser<C extends AbstractCI> {
    String stringify(C ci);

    C parse(Map ciMap);
}
