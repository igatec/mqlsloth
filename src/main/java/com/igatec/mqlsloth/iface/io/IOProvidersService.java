package com.igatec.mqlsloth.iface.io;

public interface IOProvidersService {

    InputProvider buildFileInputProvider(String directory);
    OutputProvider buildFileOutputProvider(String directory, PersistenceFormat format);
    InputProvider buildDBInputProvider();

}
