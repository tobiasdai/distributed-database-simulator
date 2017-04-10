import java.util.HashMap;
import java.util.Map;

/**
 * Created by dais on 2017-4-8.
 */
public class Client {
    private int clientId;
    private Map<Integer,Data> dataMap;
    private TransportNode transportNode;
    private Buffer buffer;
    private static int counter=0;

    public Client() {
        this.clientId = counter+1;
        this.dataMap = new HashMap<Integer,Data>();
        this.transportNode = null;
        this.buffer = new Buffer();
    }

    public Client(TransportNode transportNode) {
        this.clientId = counter+1;
        this.dataMap = new HashMap<Integer,Data>();
        this.transportNode = transportNode;
        this.buffer = new Buffer();
    }


}
