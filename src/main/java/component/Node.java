package component;


import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.*;

/**
 * Created by dais on 2017-4-7.
 */
public abstract class Node {
    protected int nodeId;
    protected int load;
    protected int delay;
    protected Map<Integer, Data> dataMap;
    public static int processingTime = Integer.parseInt(PropertiesConfig.readData("nodeProcessingTime"));



    public Node(int nodeId, int delay, int load) {
        this.nodeId = nodeId;
        this.delay = delay;
        this.load = load;
        dataMap = new HashMap<Integer, Data>();
        System.out.println("Node "+nodeId+": delay = "+delay+" ms, load = "+load+"%");
    }

    public void setDataMap(Map<Integer, Data> dataMap2) {
        for (Map.Entry<Integer, Data> entry : dataMap2.entrySet()) {
            dataMap.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<Integer, Data> getDataMap() {
        return dataMap;
    }

    public int getLoad() {
        return load;
    }

    public int getDelay() {
        return delay;
    }

    public int getNodeId() {
        return nodeId;
    }


    public void receivePacket(Packet pac) {
        switch (pac.getPacketType()) {
            case "r":
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " received the read request of data " + pac.getDataId() + " from clinet " + pac.getSourceId()+" ("+processingTime+"ms)");
                applyReadstrategy(pac);
                break;
            case "w":
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " received the write request of data " + pac.getDataId() + " from clinet " + pac.getSourceId()+" ("+processingTime+"ms)");
                applyWriteStrategy(pac);
                break;
            case "c":
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " received the copy of data " + pac.getDataId() + " from node " + pac.getSourceId()+" ("+processingTime+"ms)");
                applySynchronizeStrategy(pac);
                break;
            default:
                System.out.println("Undefined data type, the packet was Invalid");
                break;
        }
    }

    public abstract void applyReadstrategy(Packet packet);

    public abstract void applyWriteStrategy(Packet packet);

    public void applySynchronizeStrategy(Packet packet){};


}

