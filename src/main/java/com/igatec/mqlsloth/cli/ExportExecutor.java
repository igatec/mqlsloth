package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.iface.kernel.SearchLocation;

public class ExportExecutor extends AbstractExportExecutor {

    public ExportExecutor(SlothAppCLI cli) {
        super(cli);
    }

    @Override
    public String toString(){
        return SlothAppCLI.EXPORT_OPT_L;
    }

    @Override
    protected SearchLocation getSearchLocation() {
        return SearchLocation.SOURCE;
    }

    @Override
    protected void printStatus(int amount, long time) {
        cli.print("Exported " + amount + " objects. Execution time, ms: " + time);
    }

    @Override
    protected void printSuccess() {
        cli.println("Export executed successfully");
    }

    @Override
    protected void printFailure() {
        cli.println("ERROR: Export execution failed");
    }

}
