package com.igatec.mqlsloth.context;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.framework.MQLCommand;
import com.igatec.mqlsloth.framework.MQLCommandImpl;
import com.igatec.mqlsloth.io.fs.FileWriterService;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is a handmade implementation of the dependency injection pattern.
 * All test preparations should be done before launching SlothAppCLI.main() method.
 */
@Getter
public class ApplicationContext {

    private static final ApplicationContext INSTANCE = new ApplicationContext();

    public static ApplicationContext instance() {
        return INSTANCE;
    }

    @Setter
    private FileWriterService fileWriterService = new FileWriterService();

    private MQLCommand mqlCommand = new MQLCommandImpl();

    private Context frameworkContext = null;

    public void setMqlCommand(MQLCommand mqlCommand) {
        this.mqlCommand = mqlCommand;
        if (mqlCommand instanceof MQLCommandImpl) {
            ((MQLCommandImpl) mqlCommand).setContext(frameworkContext);
        }
    }

    public void setFrameworkContext(Context frameworkContext) {
        this.frameworkContext = frameworkContext;
        if (mqlCommand instanceof MQLCommandImpl) {
            ((MQLCommandImpl) mqlCommand).setContext(frameworkContext);
        }
    }
}
