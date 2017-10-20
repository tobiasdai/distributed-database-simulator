package controller;

/**
 * Created by dais on 2017-5-17.
 */
public abstract class SimulatorEvent {
    private int time;

    private boolean forTimeoutCheck;

    public abstract void go();

    public void setForTimeoutCheck() {
        forTimeoutCheck = true;
    }

    public boolean getForTimeoutCheck(){
        return forTimeoutCheck;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
