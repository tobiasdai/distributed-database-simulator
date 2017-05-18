package component;


import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.*;

/**
 * Created by dais on 2017-4-7.
 */
public class Node {
    private int nodeId;
    private int load;
    private long delay;
    private Map<Integer, Data> dataMap;
    public static long processingTime = 5;
    private static long copytime = Long.parseLong(PropertiesConfig.readData("copytime"));;


    public Node(int id) {
        nodeId = id;
        dataMap = new HashMap<Integer, Data>();
        load = new Random().nextInt(100);
        delay = new Random().nextInt(1000);
        System.out.println("Node-delay " + nodeId + " is: " + delay + " ms");
    }

    public void setDataMap(Map<Integer, Data> dataMap2) {
        for (Map.Entry<Integer, Data> entry : dataMap2.entrySet()) {
            dataMap.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<Integer, Data> getDataMap() {
        return dataMap;
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


    public void receivePacket(Packet pac) {
        switch (pac.getPacketType()) {
            case "r":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Node " + nodeId + " received the read request of data " + pac.getDataId() + " from clinet " + pac.getSourceId());
                applyReadstrategy(pac);
                break;
            case "w":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Node " + nodeId + " received the write request of data " +  pac.getDataId() + " from clinet " + pac.getSourceId());
                applyWriteStrategy(pac);
                break;
            case "c":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Node " + nodeId + " received the copy of data " +  pac.getDataId() + " from node " + pac.getSourceId());
                applySynchronizeStrategy(pac);
                break;
            default:
                System.out.println("Undefined data type, the packet was Invalid");
                break;
        }

    }

    public void applyReadstrategy(Packet packet) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket(packet.getDataId(), "rn", null, packet.getTimestamp());
                System.out.println("Time " + SimulatorTimer.currentTime + ": Node " + nodeId + " tried to read data " + packet.getDataId() + " for client " + packet.getSourceId());
                if (dataMap.get(packet.getDataId()) == null) {
                    System.out.println("          The data " + packet.getDataId() + " was not found in node " + getNodeId());
                } else if (((ReadRequestPacket) packet).getNeededDataVersion() <= dataMap.get(packet.getDataId()).getVersionstamp()) {
                    System.out.println("          The data " + packet.getDataId() + " was found in node " + getNodeId());
                    backPacket = new ResponsePacket(packet.getDataId(), "rk", dataMap.get(packet.getDataId()), packet.getTimestamp());
                } else {
                    System.out.println("          The dataversion of data "+packet.getDataId()+" was lower than expected in node " + getNodeId());
                }
                backPacket.setSourceId(getNodeId());
                Network.transferPacket(backPacket, Node.this, ClientManager.getClientWithClientId(packet.getSourceId()));
                System.out.println("          Node " + Node.this.getNodeId() + " sent a packet back to client " + packet.getSourceId() + " (" + getDelay() / 2 + "ms)");
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
        SimulatorTimer.currentTime++;
    }

    public void applyWriteStrategy(Packet packet) {
        WriteRequestPacket pac = (WriteRequestPacket)packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket(packet.getDataId(), "wn", null, packet.getTimestamp());
                System.out.println("Time " + SimulatorTimer.currentTime + ": Node " + nodeId + " tried to write data " + packet.getDataId() + " for client " + packet.getSourceId());
                if (dataMap.get(pac.getDataToWrite().getDataId()) == null ||pac.getDataToWrite().getVersionstamp() > dataMap.get(pac.getDataToWrite().getDataId()).getVersionstamp()) {
                    System.out.println("          The data " + pac.getDataToWrite().getDataId() + " was writen in node " + getNodeId());
                    backPacket = new ResponsePacket(pac.getDataId(), "wk", null, packet.getTimestamp());
                    SimulatorEvent simulatorEvent1 = new SimulatorEvent() {
                        @Override
                        public void go() {
                            synchronizeData(pac.getDataToWrite());
                        }
                    };
                    System.out.println("          "+copytime+"ms later start to synchronize data "+pac.getDataId());
                    simulatorEvent1.setTime(SimulatorTimer.currentTime + processingTime+copytime);
                    Simulator.addEvent(simulatorEvent1);
                } else {
                    System.out.println("          The dataversion of data "+pac.getDataToWrite().getDataId()+" was lower than expected in node " + getNodeId());
                }
                backPacket.setSourceId(getNodeId());
                Network.transferPacket(backPacket, Node.this, ClientManager.getClientWithClientId(packet.getSourceId()));
                System.out.println("          Node " + Node.this.getNodeId() + " sent a packet back to client " + packet.getSourceId() + " (" + getDelay() / 2 + "ms)");
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
        SimulatorTimer.currentTime++;
    }

    public void applySynchronizeStrategy(Packet packet){
        DataCopyPacket pac = (DataCopyPacket)packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
        if (dataMap.get(pac.getDataId()) == null ||pac.getDataToCopy().getVersionstamp() > dataMap.get(pac.getDataId()).getVersionstamp()) {
            System.out.println("          The data " + pac.getDataId() + " was copied in node " + getNodeId());
        }else{

        }
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
    }

    public void synchronizeData(Data data){
        System.out.println("Time " + SimulatorTimer.currentTime + ": Node " + nodeId + " started to copy data " + data.getDataId() + " to other nodes");
        DataCopyPacket pac = new DataCopyPacket(data,SimulatorTimer.currentTime,getNodeId());
        for(Node node: NodeManager.nodeList){
            if(node.getNodeId()==getNodeId()){
                continue;
            }
            Network.transferPackage(pac,this,node);
        }
    }

    public int calculateWeighting(int delayWeighting, int loadWeighting) {
        return (int) ((100 - getDelay() / 10) * delayWeighting + (100 - getLoad()) * loadWeighting);
    }
}
