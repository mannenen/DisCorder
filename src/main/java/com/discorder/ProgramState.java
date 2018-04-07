package com.discorder;

public class ProgramState {
    public enum State {
        PAUSED, RECORDING, STOPPED
    }

    public ProgramState() {
        ProgramState.state = State.STOPPED;
    }

    public static synchronized void setProgramState(State state) {
        ProgramState.state = state;
    }
    
    public static synchronized State getProgramState() {
        return ProgramState.state;
    }

    private static State state;
}
