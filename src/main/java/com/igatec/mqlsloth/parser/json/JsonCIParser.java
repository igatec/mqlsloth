package com.igatec.mqlsloth.parser.json;

public class JsonCIParser /*implements ReaderCIParser, WriterCI*/ {
//    String ciData;
//
//    private final static String SLOTH_ADMIN_TYPE_KEY = "objectType";
//
//    @Override
//    public String stringify(AbstractCI ci) throws ParserException {
//        JsonConcreteParser parser;
//        try {
//            parser = Mapper.getParser(ci.getSlothAdminType()).newInstance();
//            return parser.stringify(ci);
//        } catch (InstantiationException | IllegalAccessException e) {
//            throw new ParserException();
//        }
//    }
//
//    @Override
//    public AbstractCI parse() throws ParserException {
//        Map ciMap;
//        try {
//            ciMap = new ObjectMapper().readValue(ciData, Map.class);
//        } catch (IOException e) {
//            throw new ParserException();
//        }
//        String aTypeStr = (String) ciMap.get(SLOTH_ADMIN_TYPE_KEY);
//        SlothAdminType aType = SlothAdminType.getByKey(aTypeStr);
//        if (aType == null)
//            throw new ParserException("Sloth objectType '" + aTypeStr + "' is not valid");
//        try {
//            JsonConcreteParser parser = Mapper.getParser(aType).newInstance();
//            return parser.parse(ciMap);
//        } catch (InstantiationException | IllegalAccessException e) {
//            throw new ParserException();
//        }
//    }
}
