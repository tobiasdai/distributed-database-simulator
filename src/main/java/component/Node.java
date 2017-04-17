package component;


import manager.ClientManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    private Map<Integer, Data> dataMap;
    private int tempClientId;
    private static int nodeCounter = 0;

    public Node() {
        nodeId = ++nodeCounter;
        checkBufferStatus();
        buffer = new Buffer();
    }

    public void setDataMap(Map<Integer, Data> dataMap) {
        this.dataMap = dataMap;
    }

    public void setTransportNode(TransportNode transportNode) {
        this.transportNode = transportNode;
    }

    public void setTempClientId(int tempClientId) {
        this.tempClientId = tempClientId;
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


    public void receiveAndSendData(Data data) {
        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
        if (data.getType() == 'r') {
//            System.out.println("服务器已收到");
            Data result = new Data(dataMap.get(Integer.parseInt(data.getContent())));
            targetClient.getBuffer().addData(result);
//            System.out.println("服务器已发回");
        }
        if (data.getType() == 'w') {
            //codes for writing data
        }
    }


    public void checkBufferStatus() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!buffer.isBufferEmpty()) {
                    for (Data data : buffer.getBufferList()) {
                        receiveAndSendData(data);
                    }
                }
            }
        }, 0, 100);
    }
}
