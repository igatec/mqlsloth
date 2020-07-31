package com.igatec.mqlsloth.context;

import com.igatec.mqlsloth.framework.MQLCommand;
import com.igatec.mqlsloth.framework.MQLCommandImpl;
import com.igatec.mqlsloth.io.fs.FileWriterService;
import lombok.Getter;
import lombok.Setter;

public class ApplicationContext {

    private static final ApplicationContext CONTEXT = new ApplicationContext();

    public static ApplicationContext instance() {
        return CONTEXT;
    }

    @Getter
    @Setter
    private MQLCommand mqlCommand = MQLCommandImpl.instance();

    @Getter
    @Setter
    private FileWriterService fileWriterService = new FileWriterService();
}
