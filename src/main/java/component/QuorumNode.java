package component;

import controller.Simulator;
import controller.SimulatorEvent;
import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Created by dais on 2017-5-26.
 */
public class QuorumNode extends Node {
    private Node[] nodeArray;
    public static int readFactor;
    public static int writeFactor;
    private int quorumTime;

    static {
        if (PropertiesConfig.readData("readWriteFactor").equals("default")) {
            writeFactor = (int) Math.floor(Integer.parseInt(PropertiesConfig.readData("numberOfNode")) / 2);
            readFactor = Integer.parseInt(PropertiesConfig.readData("numberOfNode")) - writeFactor - 1;
        } else {
            writeFactor = Integer.parseInt(PropertiesConfig.readData("writeFactor"));
            readFactor = Integer.parseInt(PropertiesConfig.readData("readFactor"));
        }
    }

    public QuorumNode(int nodeId, int delay, int load, boolean status) {
        super(nodeId, delay, load, status);
    }

    @Override
    public void applyReadstrategy(Packet packet) {

        //check whether the number of online-nodes meet the condition
        if (NodeManager.numberOfOnlineNode < NodeManager.numberOfNode - writeFactor) {
            System.out.println(ansi().eraseScreen().fg(RED) + "number of online nodes :" + NodeManager.numberOfOnlineNode + " / " + NodeManager.numberOfNode);
            System.out.println("Error, the number of online nodes is too small to run the QUORUM test" + ansi().reset());
            System.exit(1);
        }

        //Get the sorted array of online nodes, with read-mode
        nodeArray = deleteCurrentNodeAndBubbleSort(NodeManager.nodeList.toArray(new Node[NodeManager.nodeList.size()]), 'r');
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                SimulatorEvent simulatorEvent1 = new SimulatorEvent() {
                    @Override
                    public void go() {
                        Packet backPacket = new ResponsePacket(packet.getDataId(), "rn", null, packet.getTimestamp());
                        boolean ifDataExist = false;
                        int latestVersion = ((ReadRequestPacket) packet).getNeededDataVersion();
                        Node chosenNode = null;

                        //if data already exits in dataMap, determines whether the version stamp of the data meets the requirements
                        if (dataMap.get(packet.getDataId()) != null) {
                            ifDataExist = true;
                            if (latestVersion <= dataMap.get(packet.getDataId()).getVersionstamp()) {
                                latestVersion = dataMap.get(packet.getDataId()).getVersionstamp();
                                chosenNode = QuorumNode.this;
                            }
                        }

                        //do the quorum operation
                        for (int i = readFactor - 1; i > -1; i--) {
                            if (nodeArray[i].getDataMap().get(packet.getDataId()) != null) {
                                ifDataExist = true;
                                if (latestVersion <= nodeArray[i].getDataMap().get(packet.getDataId()).getVersionstamp()) {
                                    latestVersion = nodeArray[i].getDataMap().get(packet.getDataId()).getVersionstamp();
                                    chosenNode = nodeArray[i];
                                }
                            }
                        }
                        System.out.println(ansi().eraseScreen().fg(YELLOW) + "Time " + Simulator.currentTime + ": The read quorum of data " + packet.getDataId() + " for client " + packet.getSourceId() + " ended" + ansi().reset());
                        if (!ifDataExist) {
                            System.out.println(ansi().eraseScreen().fg(RED) + "        The data " + packet.getDataId() + " was not found in all of nodes " + ansi().reset());
                        } else if (chosenNode != null) {
                            System.out.println(ansi().eraseScreen().fg(GREEN) + "        The data " + packet.getDataId() + " was found in node " + chosenNode.getNodeId() + ansi().reset() + ansi().eraseScreen().fg(BLUE) + " with dataversion: " + chosenNode.dataMap.get(packet.getDataId()).getVersionstamp() + ansi().reset());
                            backPacket = new ResponsePacket(packet.getDataId(), "rk", chosenNode.dataMap.get(packet.getDataId()), packet.getTimestamp());
                        } else {
                            System.out.println(ansi().eraseScreen().fg(RED) + "        The dataversion of data " + packet.getDataId() + " was lower than expected in all of the nodes " + ansi().reset());
                        }
                        backPacket.setSourceId(getNodeId());
                        System.out.println("        Node " + QuorumNode.this.getNodeId() + " sent a packet back to client " + packet.getSourceId() + " (" + getDelay() / 2 + "ms)");
                        Network.transferPacket(backPacket, QuorumNode.this, ClientManager.getClientWithClientId(packet.getSourceId()));
                    }
                };
                int maximumDelay = 0;
                for (int i = readFactor - 1; i > -1; i--) {
                    if (maximumDelay < nodeArray[i].getDelay()) {
                        maximumDelay = nodeArray[i].getDelay();
                    }
                }

                //calculate how long this quorum operation will cost.
                //we take the longst load between the target node and the current node as the quorumTime (including random delay during the network transfer)
                quorumTime = Math.abs(maximumDelay - getDelay()) + new Random().nextInt(Network.maximumRandomNetworkDelay) + processingTime;
                System.out.println(ansi().eraseScreen().fg(YELLOW) + "Time " + Simulator.currentTime + ": Node " + nodeId + " strated the read quorum of data " + packet.getDataId() + " for client " + packet.getSourceId() + " (" + quorumTime + "ms)" + ansi().reset());

                //print all nodes that participate in the operation
                for (int i = 0; i < readFactor; i++) {
                    System.out.println("        The Response from Node " + nodeArray[i].getNodeId() + " was received");
                }
                simulatorEvent1.setTime(quorumTime + Simulator.currentTime);
                Simulator.addEvent(simulatorEvent1);
                Simulator.currentTime++;
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
        Simulator.currentTime++;
    }

    @Override
    public void applyWriteStrategy(Packet packet) {

        //check whether the number of online-nodes meet the condition
        if (NodeManager.numberOfOnlineNode < NodeManager.numberOfNode - writeFactor) {
            System.out.println(ansi().eraseScreen().fg(RED) + "number of online nodes :" + NodeManager.numberOfOnlineNode + " / " + NodeManager.numberOfNode);
            System.out.println("Error, the number of online nodes is too small to run the QUORUM test" + ansi().reset());
            System.exit(1);
        }

        //Get the sorted array of online nodes, with write-mode
        nodeArray = deleteCurrentNodeAndBubbleSort(NodeManager.nodeList.toArray(new Node[NodeManager.nodeList.size()]), 'w');
        WriteRequestPacket pac = (WriteRequestPacket) packet;
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                SimulatorEvent simulatorEvent1 = new SimulatorEvent() {
                    @Override
                    public void go() {
                        Packet backPacket = new ResponsePacket(pac.getDataId(), "wn", null, pac.getTimestamp());
                        int dataVersion = pac.getDataToWrite().getVersionstamp();

                        //variable "result" means whether this quorum operation succeed or not
                        boolean result = true;

                        //the quorum operation begins
                        if (dataMap.get(pac.getDataId()) != null && dataVersion <= dataMap.get(pac.getDataId()).getVersionstamp()) {
                            result = false;
                        }
                        for (int i = writeFactor - 1; i > -1; i--) {
                            if (nodeArray[i].getDataMap().get(pac.getDataId()) != null && dataVersion <= nodeArray[i].dataMap.get(pac.getDataId()).getVersionstamp()) {
                                result = false;
                            }
                        }
                        System.out.println(ansi().eraseScreen().fg(YELLOW) + "Time " + Simulator.currentTime + ": The write quorum of data " + pac.getDataId() + " for client " + pac.getSourceId() + " ended" + ansi().reset());
                        if (result) {
                            dataMap.put(pac.getDataId(), pac.getDataToWrite());
                            for (int i = writeFactor - 1; i > -1; i--) {
                                nodeArray[i].getDataMap().put(pac.getDataId(), pac.getDataToWrite());
                            }
                            System.out.println(ansi().eraseScreen().fg(GREEN) + "        Write Quorum of data " + pac.getDataId() + " succeed" + ansi().reset() + ", " + ansi().eraseScreen().fg(BLUE) + "the current datavion in node(s): " + pac.getDataToWrite().getVersionstamp() + ansi().reset());
                            backPacket = new ResponsePacket(pac.getDataId(), "wk", null, pac.getTimestamp());
                        } else {
                            System.out.println(ansi().eraseScreen().fg(RED) + "        The dataversion of data " + pac.getDataId() + " was lower than expected in one(or more) of the nodes " + ansi().reset());
                        }
                        backPacket.setSourceId(getNodeId());
                        System.out.println("        Node " + QuorumNode.this.getNodeId() + " sent a packet back to client " + pac.getSourceId() + " (" + getDelay() / 2 + "ms)");
                        Network.transferPacket(backPacket, QuorumNode.this, ClientManager.getClientWithClientId(pac.getSourceId()));
                    }
                };

                //calculate how long this quorum operation will cost.
                //we take the longst load between the target node and the current node as the quorumTime (including random delay during the network transfer)
                quorumTime = Math.abs(nodeArray[writeFactor - 1].getDelay() - getDelay()) * 2 + new Random().nextInt(Network.maximumRandomNetworkDelay) + processingTime;
                System.out.println(ansi().eraseScreen().fg(YELLOW) + "Time " + Simulator.currentTime + ": Node " + nodeId + " strated the write quorum of data " + pac.getDataId() + " for client " + pac.getSourceId() + " (" + quorumTime + "ms)" + ansi().reset());

                //print all nodes that participate in the operation
                for (int i = 0; i < writeFactor; i++) {
                    System.out.println("        The Data from Node " + nodeArray[i].getNodeId() + " was received");
                }
                simulatorEvent1.setTime(quorumTime + Simulator.currentTime);
                Simulator.addEvent(simulatorEvent1);
                Simulator.currentTime++;
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + processingTime);
        Simulator.addEvent(simulatorEvent);
        Simulator.currentTime++;
    }

    @Override
    public void applySynchronizeStrategy(Packet packet) {
    }

    //  type 'r' for read method, type 'w' for write method. Just leave online-nodes
    //  the offline-nodes will also be deleted from the Array
    private Node[] deleteCurrentNodeAndBubbleSort(Node[] nodes, char type) {
        int counter = 0;
        int numberOfOnlineNode = NodeManager.numberOfOnlineNode;
        Node[] t = new Node[numberOfOnlineNode - 1];

        //delete current node from nodes-Array
        for (Node node : nodes) {
            if (!(node == this)) {
                if (node.getStatus() == true) {
                    t[counter] = node;
                    counter++;
                }
            }
        }
        nodes = t;
        for (int i = 0; i < nodes.length - 1; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                Node temp;
                if (nodes[i].getDelay() < nodes[j].getDelay()) {
                    temp = nodes[i];
                    nodes[i] = nodes[j];
                    nodes[j] = temp;
                }
            }

        }
        return nodes;
    }

}