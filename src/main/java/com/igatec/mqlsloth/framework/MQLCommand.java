package com.igatec.mqlsloth.framework;

import com.igatec.mqlsloth.kernel.SlothException;

import java.util.List;

public interface MQLCommand {

    String executeOrThrow(Context context, String cmd, List<String> args) throws SlothException;
}
