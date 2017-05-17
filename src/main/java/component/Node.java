package component;


import manager.ClientManager;

import java.util.*;

/**
 * Created by dais on 2017-4-7.
 */
public class Node {
    private int nodeId;
    private TransportNode transportNode;
    private int load;
    private long delay;
    private Buffer buffer;
    private Map<Integer, Data> dataMap;
    private long processingTime;



    public Node(int id) {
        nodeId = id;
        dataMap = new HashMap<Integer, Data>();
        buffer = new Buffer();
        load = new Random().nextInt(100);
        delay = new Random().nextInt(1000);
        processingTime = 5;
        System.out.println("Node-delay "+nodeId+ " is: "+delay+" ms" );
    }

public void setDataMap(Map<Integer, Data> dataMap2) {
        for (Map.Entry<Integer, Data> entry : dataMap2.entrySet()) {
            dataMap.put(entry.getKey(),entry.getValue());
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

    public Buffer getBuffer() {
        return buffer;
    }

    public void receivePacket(Packet pac){
        switch (pac.getPacketType()){
            case 'r':
                System.out.println("Time "+SimulatorTimer.currentTime+": Node "+nodeId+" received the read request from clinet "+pac.getSourceId());
                applyReadstrategy(pac);
                break;
            case 'w':
                break;
        }

    }

    public void applyReadstrategy(Packet packet){
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                Packet backPacket = new ResponsePacket('n',null,packet.getTimestamp());
                System.out.println("Time "+SimulatorTimer.currentTime+": Node "+nodeId+" tried to read data "+((ReadRequestPacket) packet).getNeededDataId()+" for client "+packet.getSourceId());
                if(dataMap.get(((ReadRequestPacket) packet).getNeededDataId())==null){
                    System.out.println("          The data "+((ReadRequestPacket) packet).getNeededDataId()+" was not found in node "+getNodeId());
                }else if(((ReadRequestPacket) packet).getNeededDataVersion()<=dataMap.get(((ReadRequestPacket) packet).getNeededDataId()).getVersionstamp()){
                    System.out.println("          The data "+((ReadRequestPacket) packet).getNeededDataId()+" was found in node "+getNodeId());
                    backPacket = new ResponsePacket('k',dataMap.get(((ReadRequestPacket) packet).getNeededDataId()),packet.getTimestamp());
                }else {
                    System.out.println("          The dataversion was lower than expected in node "+getNodeId());
                }
                backPacket.setSourceId(getNodeId());
                Network.transferPacket(backPacket,Node.this,ClientManager.getClientWithClientId(packet.getSourceId()));
                System.out.println("          Node "+Node.this.getNodeId()+" sent a packet back to client "+packet.getSourceId()+" ("+getDelay()/2+"ms)");
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime+processingTime);
        Simulator.addEvent(simulatorEvent);
        SimulatorTimer.currentTime++;
    }

    public void applyWriteStrategy(Packet packet){

    }


//    public void checkBufferStatus() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!buffer.isBufferEmpty()) {
//                    for (Iterator<Data> iterator = buffer.getBufferList().iterator(); iterator.hasNext(); ) {
//                        Data data = iterator.next();
//                        iterator.remove();
//                        receiveAndSendData(data);
//                    }
//                }
//            }
//        }, 0, 40);
//    }

    public int calculateWeighting(int delayWeighting, int loadWeighting) {
        return (int) ((100 - getDelay() / 10) * delayWeighting + (100 - getLoad()) * loadWeighting);
    }
}
