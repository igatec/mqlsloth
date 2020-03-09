package com.igatec.mqlsloth.io.fs;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.iface.io.OutputProvider;
import com.igatec.mqlsloth.iface.util.Readable;
import com.igatec.mqlsloth.io.AbstractOutputProvider;
import com.igatec.mqlsloth.kernel.SlothException;

import java.io.File;

public class FSComposedOutputProvider extends AbstractOutputProvider {

    private final static String DIFF_DIR = "diff";
    private final static String SCRIPT_DIR = "script";

    private final OutputProvider diffProvider;
    private final OutputProvider scriptProvider;

    public FSComposedOutputProvider(String directory) {
        File dir = new File(directory);
        if (!dir.exists())
            dir.mkdir();
        diffProvider = new FileSystemOutputProvider(directory + File.separator + DIFF_DIR);
        scriptProvider = new FileSystemOutputProvider(directory + File.separator + SCRIPT_DIR);
    }


    @Override
    public void saveCIDefinition(AbstractCI ci) throws SlothException {
        diffProvider.saveCIDefinition(ci);
    }

    @Override
    public void saveUpdateScript(Readable readable) throws SlothException {
        scriptProvider.saveUpdateScript(readable);
    }

    @Override
    public boolean clearAll() {
        return diffProvider.clearAll();
    }
}
