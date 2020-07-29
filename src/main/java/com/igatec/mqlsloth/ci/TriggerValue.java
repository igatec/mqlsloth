package com.igatec.mqlsloth.ci;

import java.util.Objects;

public class TriggerValue {

    private static final String DEFAULT_PROGRAM = "emxTriggerManager";
    private final String program;
    private final String input;

    public TriggerValue(String program, String input) {
        this.program = program == null ? DEFAULT_PROGRAM : program;
        this.input = input;
    }

    public String getProgram() {
        return program;
    }

    public String getInput() {
        return input;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriggerValue that = (TriggerValue) o;
        return Objects.equals(program, that.program) &&
                Objects.equals(input, that.input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(program, input);
    }
}
