package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.iface.kernel.SearchLocation;

public class UpdateExecutor extends AbstractExportExecutor {

    public UpdateExecutor(SlothAppCLI cli) {
        super(cli);
    }

    @Override
    public String toString(){
        return SlothAppCLI.UPDATE_OPT_L;
    }

    @Override
    protected SearchLocation getSearchLocation() {
        return SearchLocation.TARGET;
    }

    @Override
    protected void printStatus(int amount, long time) {
        cli.print("Updated " + amount + " objects. Execution time, ms: " + time);
    }

    @Override
    protected void printSuccess() {
        cli.println("Update executed successfully");
    }

    @Override
    protected void printFailure() {
        cli.println("ERROR: Update execution failed");
    }

}
