package component;

/**
 * Created by dais on 2017-5-17.
 */
public abstract class SimulatorEvent {
    private long time;

    public abstract void go();

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
