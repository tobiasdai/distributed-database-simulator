package component;

import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

/**
 * general Node
 * Created by dais on 2017-5-26.
 */
public class GeneralNode extends Node {
    public static int copytime = Integer.parseInt(PropertiesConfig.readData("nodeCopytime"));
    public GeneralNode(int nodeId,int delay,int load){
        super(nodeId,delay,load);
    }

    @Override
    public void applyReadstrategy(Packet packet) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket(packet.getDataId(), "rn", null, packet.getTimestamp());
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " tried to read data " + packet.getDataId() + " for client " + packet.getSourceId());
                if (dataMap.get(packet.getDataId()) == null) {
                    System.out.println("        The data " + packet.getDataId() + " was not found in node " + getNodeId()+"  <<= = = = = = = = = = = = = = = = Error-information");
                } else if (((ReadRequestPacket) packet).getNeededDataVersion() <= dataMap.get(packet.getDataId()).getVersionstamp()) {
                    System.out.println("        The data " + packet.getDataId() + " was found in node " + getNodeId());
                    backPacket = new ResponsePacket(packet.getDataId(), "rk", dataMap.get(packet.getDataId()), packet.getTimestamp());
                } else {
                    System.out.println("        The dataversion of data "+packet.getDataId()+" was lower than expected in node " + getNodeId()+"  <<= = = = = = = = = = = = = = = = Error-information");
                }
                backPacket.setSourceId(getNodeId());
                System.out.println("        Node " + GeneralNode.this.getNodeId() + " sent a packet back to client " + packet.getSourceId() + " (" + getDelay() / 2 + "ms)");
                Network.transferPacket(backPacket, GeneralNode.this, ClientManager.getClientWithClientId(packet.getSourceId()));
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
        Simulator.currentTime++;
    }

    @Override
    public void applyWriteStrategy(Packet packet) {
        WriteRequestPacket pac = (WriteRequestPacket)packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket(packet.getDataId(), "wn", null, pac.getTimestamp());
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " tried to write data " + pac.getDataId() + " for client " + pac.getSourceId());
                if (dataMap.get(pac.getDataId()) == null ||pac.getDataToWrite().getVersionstamp() > dataMap.get(pac.getDataId()).getVersionstamp()) {
                    System.out.println("        The data " + pac.getDataId() + " was writen in node " + getNodeId());
                    backPacket = new ResponsePacket(pac.getDataId(), "wk", null, pac.getTimestamp());
                    SimulatorEvent simulatorEvent1 = new SimulatorEvent() {
                        @Override
                        public void go() {
                            synchronizeData(pac.getDataToWrite());
                        }
                    };
                    System.out.println("        "+copytime+"ms later start to synchronize data "+pac.getDataId());
                    simulatorEvent1.setTime(Simulator.currentTime + processingTime+copytime);
                    Simulator.addEvent(simulatorEvent1);
                } else {
                    System.out.println("        The dataversion of data "+pac.getDataId()+" was lower than expected in node " + getNodeId()+"  <<= = = = = = = = = = = = = = = = Error-information");
                }
                backPacket.setSourceId(getNodeId());
                System.out.println("        Node " + GeneralNode.this.getNodeId() + " sent a packet back to client " + pac.getSourceId() + " (" + getDelay() / 2 + "ms)");
                Network.transferPacket(backPacket, GeneralNode.this, ClientManager.getClientWithClientId(pac.getSourceId()));
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
        Simulator.currentTime++;
    }

    private void synchronizeData(Data data) {
        System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " started to copy data " + data.getDataId() + " to other nodes");
        DataCopyPacket pac = new DataCopyPacket(data, Simulator.currentTime, getNodeId());
        for (Node node : NodeManager.nodeList) {
            if (node.getNodeId() == getNodeId()) {
                continue;
            }
            Network.transferPackage(pac, this, node);
        }
    }

    @Override
    public void applySynchronizeStrategy(Packet packet) {
        DataCopyPacket pac = (DataCopyPacket) packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                if (dataMap.get(pac.getDataId()) == null || pac.getDataToCopy().getVersionstamp() > dataMap.get(pac.getDataId()).getVersionstamp()) {
                    System.out.println("        The data " + pac.getDataId() + " was copied in node " + getNodeId());
                } else {

                }
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
    }
}
