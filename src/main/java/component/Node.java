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
    //    private int tempClientId;
    private static int nodeCounter = 0;

    public Node() {
        nodeId = ++nodeCounter;
        checkBufferStatus();
        buffer = new Buffer();
        load = new Random().nextInt(100);
        delay = new Random().nextInt(10000);
    }

    public void setDataMap(Map<Integer, Data> dataMap) {
        this.dataMap = dataMap;
    }

    public Map<Integer, Data> getDataMap() {
        return dataMap;
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



    public void receiveAndSendData(Data data) {
        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
        long randomNetDelay = new Random().nextInt(transportNode.getRandomDelayBound());
        if (data.getType() == 'r') {
//            Data ndata = dataMap.get(Integer.parseInt(data.getContent()));
//            Data result = new Data(ndata);
            System.out.println("random delay of node : " + randomNetDelay + " ms");
            Data result = new Data(dataMap.get(Integer.parseInt(data.getContent())));
            result.setTimestamp(data.getTimestamp() + delay / 2 + randomNetDelay);
            targetClient.getBuffer().addData(result);
        }
        if (data.getType() == 'w') {
            if (dataMap.get(data.getDataId()) == null && data.getVersionstamp() == 1 || data.getVersionstamp() > dataMap.get(data.getDataId()).getVersionstamp()) {
                data.setType('d');
                dataMap.put(data.getDataId(), new Data(data));
                transportNode.plusDataCheckSum(data.getDataId());
            } else {
                transportNode.minusDataCheckSum(data.getDataId());
            }
        }
    }


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
        }, 0, 20);
    }

    public int calculateWeighting(int delayWeighting, int loadWeighting) {
        return (int) ((100 - getDelay() / 10) * delayWeighting + (100 - getLoad()) * loadWeighting);
    }
}
