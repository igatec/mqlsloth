package com.igatec.mqlsloth.parser;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.script.MqlKeywords;

public interface ReaderCIParser<C> extends MqlKeywords {

    C parse() throws ParserException;

}
