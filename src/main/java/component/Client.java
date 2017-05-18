package component;

import manager.PropertiesConfig;

import java.util.*;

/**
 * Created by dais on 2017-4-8.
 */


public class Client {
    private final int clientId;
    private Map<Integer, Data> dataMap;
    private List<Node> nodeList;
    private int retry = 0;
    public static long clientTimeOut = Long.parseLong(PropertiesConfig.readData("clientTimeOut"));

    public Client(int counter) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        nodeList = new ArrayList<Node>();
    }


    public int getClientId() {
        return clientId;
    }


    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void setDataMap(Map<Integer, Data> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * and check the answer
     * The read request is implemented as Data with type "r" and dataId 0
     *
     * @param neededDataId The id of the data which we need to read, was writen as content in read request(Data Instance)
     * @throws InterruptedException
     */
    public void sendReadRequest(int neededDataId) {
        int nodeNumber = new Random().nextInt(nodeList.size());
        System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " sent a read request of data " + neededDataId + " to node " + nodeList.get(nodeNumber).getNodeId() + " (" + nodeList.get(nodeNumber).getDelay() / 2 + "ms)");
        int neededDataVersion = 1;
        if (dataMap.get(neededDataId) != null) {
            neededDataVersion = dataMap.get(neededDataId).getVersionstamp() + 1;
        }
        ReadRequestPacket sentPacket = new ReadRequestPacket(neededDataId, neededDataVersion, SimulatorTimer.currentTime, clientId);
        Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber));
        if (retry < Integer.parseInt(PropertiesConfig.readData("numberOfReconnection"))) {
            SimulatorEvent simulatorEvent = new SimulatorEvent() {
                @Override
                public void go() {
                    System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
                    sendReadRequest(neededDataId);
                    retry++;
                }
            };
            simulatorEvent.setTime(SimulatorTimer.currentTime + clientTimeOut);
            Simulator.addEvent(simulatorEvent);
        } else {
            System.out.println("Client " + getClientId() + " has tried 3 times, stopped to send read request of data " + neededDataId + " again");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        }
    }

    public void sendWriteRequest(int dataId) {
        int nodeNumber = new Random().nextInt(nodeList.size());
        System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " sent a write request of data " + dataId + " to node " + nodeList.get(nodeNumber).getNodeId() + " (" + nodeList.get(nodeNumber).getDelay() / 2 + "ms)");
        Data data = new Data(dataId);
        if (dataMap.get(dataId) != null) {
            data = dataMap.get(dataId);
            data.setVersionstamp(data.getVersionstamp() + 1);
        }
        WriteRequestPacket sentPacket = new WriteRequestPacket(data, SimulatorTimer.currentTime, clientId);
        Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber));
        if (retry < Integer.parseInt(PropertiesConfig.readData("numberOfReconnection"))) {
            SimulatorEvent simulatorEvent = new SimulatorEvent() {
                @Override
                public void go() {
                    System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
                    sendWriteRequest(dataId);
                    retry++;
                }
            };
            simulatorEvent.setTime(SimulatorTimer.currentTime + clientTimeOut);
            Simulator.addEvent(simulatorEvent);
        } else {
            System.out.println("Client " + getClientId() + " has tried 3 times, stopped to send write request of data " + dataId + " again");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        }


    }

    public void receivePacket(Packet pac) {
        switch (pac.getPacketType()) {
            case "rk":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " received the result packet for reading data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (SimulatorTimer.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println("          Time cost: " + (SimulatorTimer.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " read data " + pac.getDataId() + " successfully");
                    dataMap.put(pac.getDataId(), ((ResponsePacket) pac).getResponseData());
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else {
                    System.out.println("          Time cost: " + (SimulatorTimer.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid");
                }
                break;
            case "rn":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " received the result packet for reading data " + pac.getDataId() + " from node " + pac.getSourceId());
                System.out.println("          Time cost: " + (SimulatorTimer.currentTime - pac.getTimestamp()) + "ms, error, please check the error information above, client " + getClientId() + " failed to read data " + pac.getDataId());
                Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                break;
            case "wk":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " received the result packet for writing data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (SimulatorTimer.currentTime - pac.getTimestamp() <= clientTimeOut){
                    System.out.println("          Time cost: " + (SimulatorTimer.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " wrote data " + pac.getDataId() + " successfully");
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                }else{
                    System.out.println("          Time cost: " + (SimulatorTimer.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid");
                }
                break;
            case "wn":
                System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " received the result packet for writing data " + pac.getDataId() + " from node " + pac.getSourceId());
                System.out.println("          Time cost: " + (SimulatorTimer.currentTime - pac.getTimestamp()) + "ms, error, please check the error information above, client " + getClientId() + " failed to write data " + pac.getDataId());
                Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
        }
    }


}
