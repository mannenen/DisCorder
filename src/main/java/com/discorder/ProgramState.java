package com.discorder;

public class ProgramState {
    public enum State {
        PAUSED, RECORDING, STOPPED
    }

    public ProgramState() {
        ProgramState.state = State.STOPPED;
    }

    public static synchronized void setProgramState(ProgramState state) {
        ProgramState.state = state;
    }

    private static State state;
}
