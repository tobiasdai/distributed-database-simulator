import java.util.Map;

/**
 * Created by dais on 2017-4-7.
 */
public class Node {


    private int nodeId;
    private int priority;
    private TransportNode transportNode;
    private int load;
    private long delay;
    private Buffer buffer;
    private Map<Integer,Data> dataMap;
    private Map<Integer,Client> clientMap;

    public Data read(int dataId){

    }

    public long getDelay() {
        return delay;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getLoad() {
        return load;
    }
}
