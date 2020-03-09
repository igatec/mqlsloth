package com.igatec.mqlsloth.parser;

import com.igatec.mqlsloth.writers.WriterCI;
import com.igatec.mqlsloth.iface.io.PersistenceFormat;

import java.util.HashMap;
import java.util.Map;

public class CIParserService {

    private final Map<PersistenceFormat, WriterCI> parsers = new HashMap<>();
    private ReaderCIParser dbParser = null;

    public ReaderCIParser getDataBaseCIParser(){
        if (dbParser == null){
            //dbParser = new ...
        }
        return dbParser;
    }

    public WriterCI getCIParser(PersistenceFormat format){
        if (!parsers.containsKey(format)){
            WriterCI parser = null; // Initialization
            parsers.put(format, parser);
        }
        return parsers.get(format);
    }


}
