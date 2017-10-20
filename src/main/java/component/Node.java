package component;


import controller.Simulator;
import manager.PropertiesConfig;

import java.util.*;

/**
 * Created by dais on 2017-4-7.
 */
public abstract class Node {
    protected int nodeId;
    protected int load;
    protected int delay;
    protected boolean status;
    protected Map<Integer, Data> dataMap;
    protected static int processingTime = Integer.parseInt(PropertiesConfig.readData("nodeProcessingTime"));


    public Node(int nodeId, int delay, int load, boolean status) {
        this.nodeId = nodeId;
        this.delay = delay;
        this.load = load;
        this.status = status;
        dataMap = new HashMap<Integer, Data>();
        if (status) {
            System.out.println("Node " + nodeId + ": delay = " + delay + " ms, load = " + load + "%");
        } else {
            System.out.println("Node " + nodeId + " is offline. The package sent to this node will be lost.");
        }
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

    public boolean getStatus() {
        return status;
    }

    public void setDelay(int delay){this.delay = delay;}

    public void receivePacket(Packet packet) {
        if (this.status == false) {
            return;
        }
        switch (packet.getPacketType()) {
            case "r":
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " received the read request of data " + packet.getDataId() + " from clinet " + packet.getSourceId() + " (" + processingTime + "ms)");
                applyReadstrategy(packet);
                break;
            case "w":
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " received the write request of data " + packet.getDataId() + " from clinet " + packet.getSourceId() + " (" + processingTime + "ms)");
                applyWriteStrategy(packet);
                break;
            case "c":
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " received the copy of data " + packet.getDataId() + " from node " + packet.getSourceId() + " (" + processingTime + "ms)");
                applySynchronizeStrategy(packet);
                break;
            default:
                System.out.println("Undefined data type, the packet was Invalid");
                break;
        }
    }

    public abstract void applyReadstrategy(Packet packet);

    public abstract void applyWriteStrategy(Packet packet);

    public abstract void applySynchronizeStrategy(Packet packet);

    public void setStatus(boolean status) {
        this.status = status;
    }
}

