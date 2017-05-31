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
    private int retry;
    public static int clientTimeOut = Integer.parseInt(PropertiesConfig.readData("clientTimeOut"));

    public Client(int counter) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        nodeList = new ArrayList<Node>();
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


    public void sendReadRequest(int neededDataId,int nodeId) {
        int nodeNumber = 0;
        if(nodeId<=0){
            nodeNumber = new Random().nextInt(nodeList.size());
        }else{
            nodeNumber = nodeId-1;
        }
        System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " sent a read request of data " + neededDataId + " to node " + nodeList.get(nodeNumber).getNodeId() + " (" + nodeList.get(nodeNumber).getDelay() / 2 + "ms)");
        int neededDataVersion = 1;
        if (dataMap.get(neededDataId) != null) {
            neededDataVersion = dataMap.get(neededDataId).getVersionstamp() + 1;
        }
        ReadRequestPacket sentPacket = new ReadRequestPacket(neededDataId, neededDataVersion, Simulator.currentTime, clientId);
        Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber));
        if (retry < Integer.parseInt(PropertiesConfig.readData("numberOfReconnection"))) {
            SimulatorEvent simulatorEvent = new SimulatorEvent() {
                @Override
                public void go() {
                    retry++;
                    System.out.println("- - - - - - - - - - - - - - Timeout, The "+retry +". attempt - - - - - - - - - - - - - - - - ");
                    sendReadRequest(neededDataId,nodeId);

                }
            };
            simulatorEvent.setTime(Simulator.currentTime + clientTimeOut);
            Simulator.addEvent(simulatorEvent);
        } else {
            System.out.println("        Client " + getClientId() + " has tried 3 times, stopped to send read request of data " + neededDataId + " again");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        }
    }

    public void sendWriteRequest(int dataId,int nodeId) {
        int nodeNumber = 0;
        if(nodeId<=0){
            nodeNumber = new Random().nextInt(nodeList.size());
        }else{
            nodeNumber = nodeId-1;
        }
        System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " sent a write request of data " + dataId + " to node " + nodeList.get(nodeNumber).getNodeId() + " (" + nodeList.get(nodeNumber).getDelay() / 2 + "ms)");
        Data data = new Data(dataId);
        if (dataMap.get(dataId) != null) {
            data = dataMap.get(dataId);
            data.setVersionstamp(data.getVersionstamp() + 1);
        }
        WriteRequestPacket sentPacket = new WriteRequestPacket(data, Simulator.currentTime, clientId);
        Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber));
        if (retry < Integer.parseInt(PropertiesConfig.readData("numberOfReconnection"))) {
            SimulatorEvent simulatorEvent = new SimulatorEvent() {
                @Override
                public void go() {
                    retry++;
                    System.out.println("- - - - - - - - - - - - - - Timeout, The "+retry +". attempt - - - - - - - - - - - - - - - - ");
                    sendWriteRequest(dataId,nodeId);

                }
            };
            simulatorEvent.setTime(Simulator.currentTime + clientTimeOut);
            Simulator.addEvent(simulatorEvent);
        } else {
            System.out.println("        Client " + getClientId() + " has tried 3 times, stopped to send write request of data " + dataId + " again  <===================================result");
        }


    }

    public void receivePacket(Packet pac) {
        switch (pac.getPacketType()) {
            case "rk":
                System.out.println("Time " + Simulator.currentTime + ":   Client " + getClientId() + " received the result packet for reading data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " read data " + pac.getDataId() + " successfully    <<= = = = = = = = = = = = = = = = Result");
                    dataMap.put(pac.getDataId(), ((ResponsePacket) pac).getResponseData());
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else {
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid<<= = = = = = = = = = = = = = = = Invalid Packet,timeout");
                }
                break;
            case "rn":
                System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " received the result packet for reading data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + "ms, error, please check the error information above, client " + getClientId() + " failed to read data " + pac.getDataId()+"  <<= = = = = = = = = = = = = = = = Result");
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                } else{
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid    <<= = = = = = = = = = = = = = = = Invalid Packet,timeout");
                }

                break;
            case "wk":
                System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " received the result packet for writing data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut){
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, not timed out, client " + getClientId() + " wrote data " + pac.getDataId() + " successfully    <<= = = = = = = = = = = = = = = = Result");
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                }else{
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid<<= = = = = = = = = = = = = = = = Invalid Packet,timeout");
                }
                break;
            case "wn":
                System.out.println("Time " + Simulator.currentTime + ": Client " + getClientId() + " received the result packet for writing data " + pac.getDataId() + " from node " + pac.getSourceId());
                if (Simulator.currentTime - pac.getTimestamp() <= clientTimeOut) {
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + "ms, error, please check the error information above, client " + getClientId() + " failed to write data " + pac.getDataId()+"  <<= = = = = = = = = = = = = = = = Result");
                    Simulator.deleteEvent(pac.getTimestamp() + clientTimeOut);
                }else{
                    System.out.println("        Time cost: " + (Simulator.currentTime - pac.getTimestamp()) + " ms, timed out, this packet was Invalid<<= = = = = = = = = = = = = = = = Invalid Packet,timeout");
                }
        }
    }


}
