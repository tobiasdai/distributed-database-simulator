package component;

/**
 * Created by dais on 2017-5-17.
 */
public abstract class SimulatorEvent {
    private int time;

    public abstract void go();

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
