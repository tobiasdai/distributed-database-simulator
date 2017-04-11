package component;

import component.Buffer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dais on 2017-4-8.
 */
public class Client {
    private int clientId;
    private Map<Integer, Data> dataMap;
    private TransportNode transportNode;
    private Buffer buffer;

    public Client(int counter) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        this.transportNode = null;
        this.buffer = new Buffer();
    }

    public Client(int counter, TransportNode transportNode) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        this.transportNode = transportNode;
        this.buffer = new Buffer();
    }


}
