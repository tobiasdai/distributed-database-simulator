package component;

import controller.Simulator;
import controller.SimulatorEvent;
import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * general Node
 * Created by dais on 2017-5-26.
 */
public class GeneralNode extends Node {
    public static int copytime = Integer.parseInt(PropertiesConfig.readData("nodeCopytime"));

    public GeneralNode(int nodeId, int delay, int load, boolean status) {
        super(nodeId, delay, load, status);
    }

    @Override
    public void applyReadstrategy(Packet packet) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket(packet.getDataId(), "rn", null, packet.getTimestamp());
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " tried to read data " + packet.getDataId() + " for client " + packet.getSourceId());
                if (dataMap.get(packet.getDataId()) == null) {

                    //this node can not find data
                    System.out.println(ansi().eraseScreen().fg(RED) + "        The data " + packet.getDataId() + " was not found in node " + getNodeId() + ansi().reset());
                } else if (dataMap.get(packet.getDataId()).getVersionstamp() < ((ReadRequestPacket) packet).getNeededDataVersion()) {

                    //this node has a lower version of data. Error
                    System.out.println(ansi().eraseScreen().fg(BLUE) + "        The dataversion of data " + packet.getDataId() + ": " + dataMap.get(packet.getDataId()).getVersionstamp() + ansi().eraseScreen().fg(RED) + " which was much lower in this node. Please check the needed dataversion first.  " + getNodeId() + ansi().reset());
                } else {
                    System.out.println(ansi().eraseScreen().fg(GREEN) + "        The data " + packet.getDataId() + " was found in node " + getNodeId() + ansi().eraseScreen().fg(BLUE) + " with the versionstamp: " + dataMap.get(packet.getDataId()).getVersionstamp() + ansi().reset());
                    backPacket = new ResponsePacket(packet.getDataId(), "rk", dataMap.get(packet.getDataId()), packet.getTimestamp());
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
        WriteRequestPacket pac = (WriteRequestPacket) packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket(packet.getDataId(), "wn", null, pac.getTimestamp());
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " tried to write data " + pac.getDataId() + " for client " + pac.getSourceId());

                if (dataMap.get(packet.getDataId()) == null || pac.getDataToWrite().getVersionstamp() > dataMap.get(pac.getDataId()).getVersionstamp()) {
                    dataMap.put(pac.getDataId(), pac.getDataToWrite());
                    System.out.println(ansi().eraseScreen().fg(GREEN) + "        The data " + pac.getDataId() + " was writen in node " + getNodeId() + ansi().eraseScreen().fg(BLUE) + " with versionstamp " + pac.getDataToWrite().getVersionstamp() + ansi().reset());
                    backPacket = new ResponsePacket(pac.getDataId(), "wk", null, pac.getTimestamp());
                    SimulatorEvent simulatorEvent1 = new SimulatorEvent() {
                        @Override
                        public void go() {
                            synchronizeData(pac.getDataToWrite());
                        }
                    };
                    System.out.println(ansi().eraseScreen().fg(YELLOW) + "        " + copytime + "ms later start to synchronize data " + pac.getDataId() + ansi().reset());
                    simulatorEvent1.setTime(Simulator.currentTime + processingTime + copytime);
                    Simulator.addEvent(simulatorEvent1);
                } else {

                    //the versionstamp <= the versionstamp in array
                    System.out.println(ansi().eraseScreen().fg(RED) + "        The dataversion of data " + pac.getDataId() + " in the packet: "+ansi().eraseScreen().fg(BLUE)+pac.getDataToWrite().getVersionstamp()+ ansi().eraseScreen().fg(RED)+" which was not greater than expected in this node: " + ansi().eraseScreen().fg(BLUE) + dataMap.get(pac.getDataId()).getVersionstamp() + ", please read the latest data first " + ansi().reset());
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
            Network.transferPacket(pac, this, node);
        }
    }

    @Override
    public void applySynchronizeStrategy(Packet packet) {
        DataCopyPacket pac = (DataCopyPacket) packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                if (getStatus() == false) {
                    System.out.println("Time " + Simulator.currentTime + ":"+ ansi().eraseScreen().fg(RED) +"Error. The Node " + getNodeId() + " was offline, copy data falied" + ansi().reset());
                    return;
                }
                if (dataMap.get(pac.getDataId()) == null || dataMap.get(pac.getDataId()).getVersionstamp() <= pac.getDataToCopy().getVersionstamp()) {
                    System.out.println("Time " + Simulator.currentTime +":"+ansi().eraseScreen().fg(GREEN) +"The data " + pac.getDataId() + " was copied in node " + getNodeId() + ansi().eraseScreen().fg(BLUE)+" with versionstamp " + pac.getDataToCopy().getVersionstamp() + ansi().reset());
                    dataMap.put(pac.getDataId(), pac.getDataToCopy());
                } else {
                    System.out.println("Time " + Simulator.currentTime+":"+ ansi().eraseScreen().fg(RED) + "Error. The data " + pac.getDataId() + " was according version error failed to be copied in node " + getNodeId() + ". Node has already a higher version of data." + ansi().reset());
                }
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
    }
}
