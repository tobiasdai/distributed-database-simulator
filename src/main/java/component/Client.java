package component;

import component.Buffer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dais on 2017-4-8.
 */

/**
 * Read-Request has type 'r', clientId 0
 * data has type 'd'
 * ack has type 'k' ,clinetId 0
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

    public void editTransportNode(TransportNode transportNode) {
        this.transportNode = transportNode;
    }

    public void sendReadRequest(int dataId) {
        Data data = new Data('r');
        transportNode.getBuffer().getBufferList().add(data);
        transportNode.noticeChanges();
    }


}
