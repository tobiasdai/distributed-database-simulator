package component;

import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by dais on 2017-5-26.
 */
public class QuorumNode extends Node {
    private Map<Integer, Integer> checksumMap;
    private Map<Integer, Integer> weightingMap;
    private Node[] nodeArray;
    private int readFactor;
    private int writeFactor;
    private int delayWeighting;
    private int loadWeighting;
    private int quorumTime;


    public QuorumNode(int nodeId, int delay, int load) {
        super(nodeId, delay, load);
        checksumMap = new HashMap<Integer, Integer>();
        weightingMap = new HashMap<Integer, Integer>();
        delayWeighting = Integer.parseInt(PropertiesConfig.readData("delayWeighting"));
        loadWeighting = Integer.parseInt(PropertiesConfig.readData("loadWeighting"));
        if (PropertiesConfig.readData("readWriteFactor").equals("default")) {
            writeFactor = (int) Math.floor(Integer.parseInt(PropertiesConfig.readData("numberOfNode")) / 2);
            readFactor = Integer.parseInt(PropertiesConfig.readData("numberOfNode")) - writeFactor - 1;
        } else {
            writeFactor = Integer.parseInt(PropertiesConfig.readData("writeFactor"));
            readFactor = Integer.parseInt(PropertiesConfig.readData("readFactor"));
        }
    }

    @Override
    public void applyReadstrategy(Packet packet) {
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
                        if (dataMap.get(packet.getDataId()) != null) {
                            ifDataExist = true;
                            if (latestVersion <= dataMap.get(packet.getDataId()).getVersionstamp()) {
                                latestVersion = dataMap.get(packet.getDataId()).getVersionstamp();
                                chosenNode = QuorumNode.this;
                            }
                        }
                        for (int i = readFactor - 1; i > -1; i--) {
                            if (nodeArray[i].getDataMap().get(packet.getDataId()) != null) {
                                ifDataExist = true;
                                if (latestVersion <= nodeArray[i].getDataMap().get(packet.getDataId()).getVersionstamp()) {
                                    latestVersion = nodeArray[i].getDataMap().get(packet.getDataId()).getVersionstamp();
                                    chosenNode = nodeArray[i];
                                }
                            }
                        }
                        System.out.println("Time " + Simulator.currentTime + ": The read quorum of data " + packet.getDataId() + " for client " + packet.getSourceId() + " ended");
                        if (!ifDataExist) {
                            System.out.println("        The data " + packet.getDataId() + " was not found in all of nodes " + "  <<= = = = = = = = = = = = = = = = Error-information");
                        } else if (chosenNode != null) {
                            System.out.println("        The data " + packet.getDataId() + " was found, after the quorum system chose the latest version from node " + chosenNode.getNodeId());
                            backPacket = new ResponsePacket(packet.getDataId(), "rk", chosenNode.dataMap.get(packet.getDataId()), packet.getTimestamp());
                        } else {
                            System.out.println("        The dataversion of data " + packet.getDataId() + " was lower than expected in all of the nodes " + "  <<= = = = = = = = = = = = = = = = Error-information");
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
                quorumTime = Math.abs(maximumDelay - getDelay()) + new Random().nextInt(Network.maximumRandomNetworkDelay) + processingTime;
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " strated the read quorum of data " + packet.getDataId() + " for client " + packet.getSourceId() + " (" + quorumTime + "ms)");
                for (int i = 0; i < readFactor; i++) {
                    System.out.println("        Node " + nodeArray[i].getNodeId() + " was chosen as a number of read quorum node(s) with weighting value: " + weightingMap.get(nodeArray[i].getNodeId()));
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
                        boolean result = true;
                        if (dataMap.get(pac.getDataId()) != null && dataVersion >= dataMap.get(pac.getDataId()).getVersionstamp()) {
                            result = false;
                        }
                        for (int i = writeFactor - 1; i > -1; i--) {
                            if (nodeArray[i].getDataMap().get(pac.getDataId()) != null && dataVersion >= nodeArray[i].dataMap.get(pac.getDataId()).getVersionstamp()) {
                                result = false;
                            }
                        }
                        System.out.println("Time " + Simulator.currentTime + ": The write quorum of data " + pac.getDataId() + " for client " + pac.getSourceId() + " ended");
                        if (result) {
                            dataMap.put(pac.getDataId(),pac.getDataToWrite());
                            for(int i = writeFactor - 1; i > -1; i--){
                                nodeArray[i].getDataMap().put(pac.getDataId(),pac.getDataToWrite());
                            }
                            backPacket = new ResponsePacket(pac.getDataId(), "wk", null, pac.getTimestamp());
                        } else {
                            System.out.println("        The dataversion of data " + pac.getDataId() + " was lower than expected in one(or more) of the nodes " + "  <<= = = = = = = = = = = = = = = = Error-information");
                        }
                        backPacket.setSourceId(getNodeId());
                        System.out.println("        Node " + QuorumNode.this.getNodeId() + " sent a packet back to client " + pac.getSourceId() + " (" + getDelay() / 2 + "ms)");
                        Network.transferPacket(backPacket, QuorumNode.this, ClientManager.getClientWithClientId(pac.getSourceId()));
                    }
                };
                quorumTime = Math.abs(nodeArray[writeFactor - 1].getDelay() - getDelay())*2 + new Random().nextInt(Network.maximumRandomNetworkDelay) + processingTime;
                System.out.println("Time " + Simulator.currentTime + ": Node " + nodeId + " strated the read quorum of data " + pac.getDataId() + " for client " + pac.getSourceId() + " (" + quorumTime + "ms)");
                for (int i = 0; i < writeFactor; i++) {
                    System.out.println("        Node " + nodeArray[i].getNodeId() + " was chosen as a number of write quorum node(s) with weighting value: " + weightingMap.get(nodeArray[i].getNodeId()));
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





    private Node[] deleteCurrentNodeAndBubbleSort(Node[] nodes, char type) {
        int counter = 0;
        Node[] t = new Node[nodes.length - 1];
        for (Node node : nodes) {
            if (!(node == this)) {
                weightingMap.put(node.getNodeId(), calculateWeighting(node));
                t[counter] = node;
                counter++;
            }
        }
        nodes = t;
        for (int i = 0; i < nodes.length - 1; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                Node temp;
                if (type == 'r') {
                    if (weightingMap.get(nodes[i].getNodeId()) < weightingMap.get(nodes[j].getNodeId())) {
                        temp = nodes[i];
                        nodes[i] = nodes[j];
                        nodes[j] = temp;
                    }
                }
                if (type == 'w') {
                    if (nodes[i].getDelay() < nodes[j].getDelay()) {
                        temp = nodes[i];
                        nodes[i] = nodes[j];
                        nodes[j] = temp;
                    }
                }
            }

        }
        return nodes;
    }

    public int calculateWeighting(Node node) {
        return (int) ((100 - Math.abs(getDelay() - node.getDelay()) / 100) * delayWeighting + (100 - node.getLoad()) * loadWeighting);
    }
}