package component;

import component.Buffer;
import generator.DataFactory;
import manager.PropertiesConfig;

import java.util.*;

/**
 * Created by dais on 2017-4-8.
 */


public class Client {
    private final int clientId;
    private Map<Integer, Data> dataMap;
    private List<Node> nodeList;
    private Buffer buffer;
    private Timer timer;
    private long interval = 0;
    public static long clientTimeOut = Long.parseLong(PropertiesConfig.readData("clientTimeOut"));


    public Client(int counter) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        nodeList = new ArrayList<Node>();
        this.buffer = new Buffer();
    }

    public Client(int counter, TransportNode transportNode) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        this.buffer = new Buffer();
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    /**
     * and check the answer
     * The read request is implemented as Data with type "r" and dataId 0
     *
     * @param neededDataId The id of the data which we need to read, was writen as content in read request(Data Instance)
     * @throws InterruptedException
     */
    public void sendReadRequest(int neededDataId) throws InterruptedException {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                int neededDataVersion = 1;
                int nodeNumber = 1;
                if (dataMap.get(neededDataId) != null) {
                    neededDataVersion = dataMap.get(neededDataId).getVersionstamp() + 1;
                }
                Packet sentPacket = new ReadRequestPacket(neededDataId, neededDataVersion, SimulatorTimer.currentTime, clientId);
                Network.transferPacket(sentPacket, Client.this, nodeList.get(nodeNumber - 1));
                System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " sent a read request to node " + nodeList.get(nodeNumber - 1).getNodeId() + " (" + nodeList.get(nodeNumber - 1).getDelay() / 2 + "ms)");
                System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " sent a read write request to node " + nodeList.get(nodeNumber - 1).getNodeId() + " (" + nodeList.get(nodeNumber - 1).getDelay() / 2 + "ms)");
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime);
        Simulator.addEvent(simulatorEvent);
    SimulatorTimer.currentTime++;
}
//        Data sdata = new Data(data);
//        sdata.setTimestamp(data.getTimestamp() + randomNetDelay);
//        transportNode.getBuffer().addData(sdata);
//        long interval = receiveData(data);
//        if (interval < clientTimeOut) {
//            System.out.println("Cleint " + clientId + ": Read test succeeded\n");
//        } else {
//            System.out.println("Client " + clientId + ": Time out, now sending readRequest " + dataId + " again\n");
//            System.out.println();
//            sendReadRequest(dataId);
//        }
//        ;
//System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + getClientId() + " sent a read write request to node " + nodeList.get(nodeNumber - 1).getNodeId() + " (" + nodeList.get(nodeNumber - 1).getDelay() / 2 + "ms)");
//System.out.println("Time " + SimulatorTimer.currentTime + ": Client " + client.getClientId() + " sent a read write request to node " + node.getNodeId() + " (" + node.getDelay()/2 + "ms)");
    public void receivePacket(Packet pac){
        switch (pac.getPacketType()){
            case 'k':
                System.out.println("Time "+SimulatorTimer.currentTime+": Client "+getClientId()+" received the ack-packet from node "+pac.getSourceId());
                if(SimulatorTimer.currentTime-pac.getTimestamp()<=clientTimeOut){
                    System.out.println("          Not timed out, client "+getClientId()+" read succeed");
                }else {
                    System.out.println("          Timed out, client "+getClientId()+" read failed");
                }
                break;
            case 'n':
                System.out.println("          Error, client "+getClientId()+" read failed");
        }
    }

    /**
     * This Method can send a write request to node(s) through TransportNode
     * and check the answer
     * The read request is implemented as Data with type "w"
     *
     * @param dataId The id of the data which we need to write, was writen as dataId in write request(Data Instance)
     * @throws InterruptedException
     */
//    public void sendWriteRequest(int dataId) throws InterruptedException {
//        Data data = new Data(dataId);
//        System.out.println("Time " + TimeCounter.timeCounter + ": " + "Cleint " + clientId + " sent write request of data " + dataId);
//        TimeCounter.timeCounter++;
//        if (dataMap.get(dataId) != null) {
//            data = dataMap.get(dataId);
//            data.setTimestamp(new Date().getTime());
//            data.setVersionstamp(data.getVersionstamp() + 1);
//        }
//        changeToWriteForm(data);
//        int randomNetDelay = new Random().nextInt(transportNode.randomDelayBound);
//        System.out.println("Time " + TimeCounter.timeCounter + ": Request package from Client " + clientId + " encountered a delay random delay of " + randomNetDelay + " ms");
//        TimeCounter.timeCounter++;
//        Data sdata = new Data(data);
//        sdata.setTimestamp(data.getTimestamp() + randomNetDelay);
//        transportNode.getBuffer().addData(sdata);
//        long interval = receiveData(data);
//        if (interval < clientTimeOut && interval >= 0) {
//            System.out.println("Cleint " + clientId + ": write test succeeded\n");
//        } else if (interval == -1) {
//            System.out.println("Clinet " + clientId + "： Writing invalid dataversion.Please update the latest version of data from server first! ");
//            System.exit(1);
//        } else {
//            System.out.println("Client " + clientId + ": time out, now sending writeRequest " + dataId + " again\n");
//            System.out.println();
//            sendWriteRequest(dataId);
//        }
//    }
//
//    /**
//     * This method checks the buffer to get the answer of node after the client sends the request
//     * Periodic check : 20ms
//     * @param sdata the request we sent
//     * @return
//     * @throws InterruptedException
//     */
//    public long receiveData(Data sdata) throws InterruptedException {
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!buffer.isBufferEmpty()) {
//                    for (Iterator<Data> it = buffer.getBufferList().iterator(); it.hasNext(); ) {
//                        Data data = it.next();
//                        if (data.getType() == 'd' && data.getDataId() == Integer.parseInt(sdata.getContent())) {
//                            interval = data.getTimestamp() - sdata.getTimestamp();
//                            System.out.println("Time " + TimeCounter.timeCounter + ": Client " + clientId + " received Data " + data.getDataId() + " in " + interval + " ms");
//                            TimeCounter.timeCounter++;
//                            if (interval < clientTimeOut) {
//                                dataMap.put(data.getDataId(), new Data(data));
//                            }
//                            timer.cancel();
//                            timer = null;
//                            it.remove();
//                        }
//                        if (data.getType() == 'k' && sdata.getDataId() == Integer.parseInt(data.getContent())) {
//                            interval = data.getTimestamp() - sdata.getTimestamp();
//                            System.out.println("Time " + TimeCounter.timeCounter + ": Client " + clientId + " received ack for Data " + sdata.getDataId() + " in " + interval + " ms");
//                            TimeCounter.timeCounter++;
//                            timer.cancel();
//                            timer = null;
//                            it.remove();
//                        }
//                        if (data.getType() == 'n') {
//                            long tempInterval = data.getTimestamp() - sdata.getTimestamp();
//                            interval = -1;
//                            System.out.println("Time " + TimeCounter.timeCounter + ": Client " + clientId + " received versionerror for Data " + sdata.getDataId() + " in " + tempInterval + " ms");
//                            TimeCounter.timeCounter++;
//                            timer.cancel();
//                            timer = null;
//                            it.remove();
//                        }
//                    }
//                }
//
//            }
//
//        }, 0, 20);
//        while (timer != null) {
//            Thread.sleep(20);
//        }
//        return interval;
//    }

    public int getClientId() {
        return clientId;
    }

    public Buffer getBuffer() {
        return buffer;
    }
}
