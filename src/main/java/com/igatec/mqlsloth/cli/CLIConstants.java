package com.igatec.mqlsloth.cli;

// todo remove checkstyleignore and refactor
// CHECKSTYLE.OFF: InterfaceIsType
public interface CLIConstants {
    String SYNC_OPT = "y";
    String SYNC_OPT_L = "sync";
    String SYNC_OPT_D = "Synchronous execution";

    String HELP_OPT = "h";
    String HELP_OPT_L = "help";
    String HELP_OPT_D = "TASK:\nPrint this message";

    String VERBOSE_OPT = "v";
    String VERBOSE_OPT_L = "verbose";
    String VERBOSE_OPT_D = "Enables verbose mode. All errors in program execution will be printed with their stack traces";

    String KEEP_OPT = "k";
    String KEEP_OPT_L = "keep";
    String KEEP_OPT_D = "Setting this option true cause launching this application in interactive mode. "
            + "This option is invalid when you work in MQL command line client";

    String EXEC_OPT = "x";
    String EXEC_OPT_L = "execute";
    String EXEC_OPT_D = "TASK:\nExecute MQL command";

    String EXPORT_OPT = "e";
    String EXPORT_OPT_L = "export";
    String EXPORT_OPT_D = "TASK:\nExport data from the source location (database or directory) to "
            + " the target location (directory)\n"
            + "NEEDS: -p, -t(l)\n"
            + "OPTIONAL: -v, -c, -y";
    String UPDATE_OPT = "u";
    String UPDATE_OPT_L = "update";
    String UPDATE_OPT_D = "TASK:\nUpdate data in target location (directory) from the source location (database or directory)\n"
            + "NEEDS: -p, -t(l)\n"
            + "OPTIONAL: -v, -c, -y";
    String IMPORT_OPT = "i";
    String IMPORT_OPT_L = "import";
    String IMPORT_OPT_D = "TASK:\nImport data from the source location (directory) to the target "
            + "location (database). This task includes script execution that leads to changes in database\n"
            + "NEEDS: -p, -s(l)\n"
            + "OPTIONAL: -v, -c, -y";
    String DIFF_OPT = "d";
    String DIFF_OPT_L = "diff";
    String DIFF_OPT_D = "TASK:\nCompare data in the source location (database or directory) and "
            + "in the target location (database or directory) and save the difference to the output location\n"
            + "NEEDS: -p, -s(l)/ -t\n"
            + "OPTIONAL: -s, -t, -v, -c, -y";

    String SOURCE_OPT = "s";
    String SOURCE_OPT_L = "source";
    String SOURCE_OPT_D = "Specify source location. Add path to this option as parameter to set directory as source. "
            + "If this parameter is not applied, the local database is assumed as source location";
    String TARGET_OPT = "t";
    String TARGET_OPT_L = "target";
    String TARGET_OPT_D = "Specify target location. Add path to this option as parameter to set directory as source."
            + " If this parameter is not applied, the local database is assumed as source location";
    String LOCATION_OPT = "l";
    String LOCATION_OPT_L = "location";
    String LOCATION_OPT_D = "Alias for '" + TARGET_OPT + "' in Export and Update tasks. Alias for '" + SOURCE_OPT
            + "' in Import and Diff tasks";
    String OUTPUT_OPT = "o";
    String OUTPUT_OPT_L = "output";
    String OUTPUT_OPT_D = "Specify output location. 'Output' option is valid only for 'Import' and 'Diff' tasks. "
            + "Add path to this option as parameter to set directory as output location";

    String NEW_OPT = "n";
    String NEW_OPT_L = "new";
    String NEW_OPT_D = "TASK:\nCreate in directory '" + LOCATION_OPT + "' new file of CI defined by argument parameters\n"
            + "NEEDS: -p, -s(l)\n"
            + "OPTIONAL: -v";

    String PATTERN_OPT = "p";
    String PATTERN_OPT_L = "pattern";
    String PATTERN_OPT_D = "Specify CI objectName pattern to look for in source or/and target locations";

    String CONTEXT_SEPARATOR = "-!-";
    String CONTEXT_OPT = "c";
    String CONTEXT_OPT_L = "context";
    String CONTEXT_OPT_D = "Specify remote context for database connection. Need an argument matching pattern {HOST}"
            + CONTEXT_SEPARATOR + "{USER}" + CONTEXT_SEPARATOR + "{PASSWORD}";

}
// CHECKSTYLE.ON: InterfaceIsType
