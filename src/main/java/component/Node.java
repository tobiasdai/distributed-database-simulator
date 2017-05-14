package component;


import manager.ClientManager;

import java.util.*;

/**
 * Created by dais on 2017-4-7.
 */
public class Node {
    private int nodeId;
    private TransportNode transportNode;
    private int load;
    private long delay;
    private Buffer buffer;
    private Map<Integer, Data> dataMap;



    public Node(int id) {
        nodeId = id;
        dataMap = new HashMap<Integer, Data>();
        checkBufferStatus();
        buffer = new Buffer();
        load = new Random().nextInt(100);
        delay = new Random().nextInt(1000);
        System.out.println("Node-delay "+nodeId+ " is: "+delay+" ms" );
    }

    public void setDataMap(Map<Integer, Data> dataMap2) {
        for (Map.Entry<Integer, Data> entry : dataMap2.entrySet()) {
            dataMap.put(entry.getKey(),new Data(entry.getValue()));
        }
    }

    public Map<Integer, Data> getDataMap() {
        return dataMap;
    }

    public TransportNode getTransportNode() {
        return transportNode;
    }

    public void setTransportNode(TransportNode transportNode) {
        this.transportNode = transportNode;
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

    public Buffer getBuffer() {
        return buffer;
    }



    public void receiveAndSendData(Data data) {}


    public void checkBufferStatus() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!buffer.isBufferEmpty()) {
                    for (Iterator<Data> iterator = buffer.getBufferList().iterator(); iterator.hasNext(); ) {
                        Data data = iterator.next();
                        iterator.remove();
                        receiveAndSendData(data);
                    }
                }
            }
        }, 0, 40);
    }

    public int calculateWeighting(int delayWeighting, int loadWeighting) {
        return (int) ((100 - getDelay() / 10) * delayWeighting + (100 - getLoad()) * loadWeighting);
    }
}
