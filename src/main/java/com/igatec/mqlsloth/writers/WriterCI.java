package com.igatec.mqlsloth.writers;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.parser.ParserException;

public interface WriterCI<C extends AbstractCI> {

    String stringify(C ci) throws ParserException;

}
