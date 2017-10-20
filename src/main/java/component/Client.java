package component;

import controller.Simulator;
import controller.SimulatorEvent;
import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.*;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Created by dais on 2017-4-8.
 */


public class Client {
    private final int clientId;
    private Map<Integer, Data> dataMap;
    private List<Node> nodeList;
    private int retry;

    //the temporaryData means when write operation runs, if the operation succeed, the client will save the data in datamap.
    //but first the data will be saved in temporaryDataMap until the client gets the response.
    private Map<Integer, Data> temporaryDataMap;
    private static int clientTimeOut = Integer.parseInt(PropertiesConfig.readData("clientTimeOut"));

    public Client(int counter) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        nodeList = new ArrayList<Node>();
        temporaryDataMap = new HashMap<Integer, Data>();
        retry = 1;
    }


    public int getClientId() {
        return clientId;
    }

    public void setDataMap(Map<Integer, Data> dataMap) {
        this.dataMap = dataMap;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }


    public void sendReadRequest(int neededDataId, int nodeId) {
        if (NodeManager.numberOfOnlineNode == 0) {
            System.out.println(ansi().eraseScreen().fg(RED).a("Error, All Nodes Offline").reset());
            System.exit(1);
        }
        int nodeNumber = new Random().nextInt(nodeList.size());
        if (nodeId <= 0) {

            //if node is offline, the client will try to choose a new node
            while (!NodeManager.getNodeStatus(nodeNumber)) {
                nodeNumber = new Random().nextInt(nodeList.size());
            }
        } else if (NodeManager.getNodeStatus(nodeId - 1)) {
            nodeNumber = nodeId - 1;
        } else {
            System.out.println(ansi().eraseScreen().fg(RED).a("The node you choose is offline").reset());
            System.exit(1);
        }
        int currentDataVersion = 0;
        if (dataMap.get(neededDataId) != null) {
            currentDataVersion = dataMap.get(neededDataId).getVersionstamp();
        }
        System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " sent a read request of data " + neededDataId + " to node " + nodeList.get(nodeNumber).getNodeId() + " (" + nodeList.get(nodeNumber).getDelay() / 2 + "ms),"+ansi().eraseScreen().fg(BLUE)+" current dataversion: "+currentDataVersion+ansi().reset());
        ReadRequestPacket sentPacket = new ReadRequestPacket(neededDataId, currentDataVersion, Simulator.currentTime, clientId);
        Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber));
        if (retry < Integer.parseInt(PropertiesConfig.readData("numberOfRetry"))) {
            SimulatorEvent simulatorEvent = new SimulatorEvent() {
                @Override
                public void go() {
                    retry++;
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Timeout for the read request of Client " + getClientId() + ", now starts the " + retry + ". attempt" + ansi().reset());
                    sendReadRequest(neededDataId, nodeId);
                }
            };
            simulatorEvent.setForTimeoutCheck();
            simulatorEvent.setTime(Simulator.currentTime + clientTimeOut);
            Simulator.addEvent(simulatorEvent);
        } else {
            System.out.println(ansi().eraseScreen().fg(RED) + "        Client " + getClientId() + " has tried 3 times, stopped to send read request of data " + neededDataId + " again, test failed" + ansi().reset());
        }
    }

    public void sendWriteRequest(int dataId, int nodeId,String content) {
        if (NodeManager.numberOfOnlineNode == 0) {
            System.out.println(ansi().eraseScreen().fg(RED).a("Error, All Nodes Offline").reset());
            System.exit(1);
        }
        int nodeNumber = new Random().nextInt(nodeList.size());
        if (nodeId <= 0) {

            //if node is offline, the client will try to choose a new node
            while (!NodeManager.getNodeStatus(nodeNumber)) {
                nodeNumber = new Random().nextInt(nodeList.size());
            }
        } else if (NodeManager.getNodeStatus(nodeId - 1)) {
            nodeNumber = nodeId - 1;
        } else {
            System.out.println(ansi().eraseScreen().fg(RED).a("The node you choose is offline").reset());
            System.exit(1);
        }
        Data data = new Data(dataId);
        if (dataMap.get(dataId) != null) {
            data = new Data(dataMap.get(dataId));
            data.setVersionstamp(data.getVersionstamp() + 1);
        }
        System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " sent a write request of data " + dataId + " to node " + nodeList.get(nodeNumber).getNodeId() + " (" + nodeList.get(nodeNumber).getDelay() / 2 + "ms),"+ansi().eraseScreen().fg(BLUE)+" original versiondataversion in client: "+ (data.getVersionstamp()-1)+", revised dataversion: "+data.getVersionstamp()+ansi().reset());
        data.setContent(content);
        temporaryDataMap.put(dataId, data);
        WriteRequestPacket sentPacket = new WriteRequestPacket(data, Simulator.currentTime, clientId);
        Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber));
        if (retry < Integer.parseInt(PropertiesConfig.readData("numberOfRetry"))) {
            SimulatorEvent simulatorEvent = new SimulatorEvent() {
                @Override
                public void go() {
                    retry++;
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Timeout for the write request of Client " + getClientId() + ", now starts the " + retry + ". attempt" + ansi().reset());
                    sendWriteRequest(dataId, nodeId,content);
                }
            };
            simulatorEvent.setForTimeoutCheck();
            simulatorEvent.setTime(Simulator.currentTime + clientTimeOut);
            Simulator.addEvent(simulatorEvent);
        } else {
            System.out.println(ansi().eraseScreen().fg(RED) + "        Client " + getClientId() + " has tried 3 times, stopped to send write request of data " + dataId + " again, test failed" + ansi().reset());
        }


    }

    public void receivePacket(Packet pac) {
        switch (pac.getPacketType()) {
            case "rk":
                System.out.println("Time " + Simulator.currentTime + ":   Client " + getClientId() + " received the result packet for reading data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {

                    if((((ResponsePacket)pac).getResponseData().getContent()).equals("")){
                        System.out.println(ansi().eraseScreen().fg(GREEN) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " read data " + pac.getDataId() + " successfully, the content is : NULL,"+ansi().eraseScreen().fg(BLUE)+" the dataversion is : "+((ResponsePacket) pac).getResponseData().getVersionstamp() + ansi().reset());
                    }else {
                        System.out.println(ansi().eraseScreen().fg(GREEN) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " read data " + pac.getDataId() + " successfully, the content is :" + ((ResponsePacket) pac).getResponseData().getContent() +","+" the dataversion is : "+ ((ResponsePacket) pac).getResponseData().getVersionstamp()+ansi().reset());
                    }
                    dataMap.put(pac.getDataId(), ((ResponsePacket) pac).getResponseData());
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else {
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid" + ansi().reset());
                }
                break;
            case "rn":
                System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " received the result packet for reading data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + "ms, error, please check the error information above, client " + getClientId() + " failed to read data " + pac.getDataId() + ansi().reset());
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else {
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid" + ansi().reset());
                }

                break;
            case "wk":
                System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " received the result packet for writing data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println(ansi().eraseScreen().fg(GREEN) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " wrote data " + pac.getDataId() + " successfully" + ansi().reset());

                    //if the write operation succeed, save the data in the own datamap of this client
                    dataMap.put(pac.getDataId(), temporaryDataMap.get(pac.getDataId()));
                    temporaryDataMap.remove(pac.getDataId());
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else {
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid" + ansi().reset());
                }
                break;
            case "wn":
                System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " received the result packet for writing data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + "ms, error, please check the error information above, client " + getClientId() + " failed to write data " + pac.getDataId() + ansi().reset());
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else {
                    System.out.println(ansi().eraseScreen().fg(RED) + "        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid" + ansi().reset());
                }
        }
    }


}
