import component.*;
import generator.ClientFactory;
import generator.DataFactory;
import generator.NodeFactory;
import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.List;
import java.util.Map;


/**
 * Created by dais on 2017-4-8.
 */
public class Main {
    private static String mode = PropertiesConfig.readData("mode");

    public static void main(String[] args) throws Exception {
//        System.out.println("Test start! Mode:"+PropertiesConfig.readData("mode"));
//        if(PropertiesConfig.readData("mode").equals("writeQuorum")){
//            writeQuorumTest();
//        }else if(PropertiesConfig.readData("mode").equals("readQuorum")){
//            readQuorumTest();
//        }else if(PropertiesConfig.readData("mode").equals("eazyRead")){
//            eazyReadTest();
//        }else if(PropertiesConfig.readData("mode").equals("eazyWrite")){
//            eazyWriteTest();
//        }
        init();
//        readTest(1,1,50);
//        readTest(1,500,55);
//        writeTest(1,2,50);
        writeTest(1,300,70);
        Simulator.go();
    }


    public static void readTest(int clientId,int dataId,long starttime) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendReadRequest(dataId);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    public static void writeTest(int clientId,int dataId,long starttime) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendWriteRequest(dataId);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }


//    public static void eazyWriteTest() throws InterruptedException{
//        TransportNode transportNode = new TransportNodeWithEazyReadWrite();
//        init(transportNode,"ReadWriteNode");
//        ClientManager.getClientWithClientId(1).sendWriteRequest(2);
//        ClientManager.getClientWithClientId(2).sendWriteRequest(2);
//    }


//    public static void eazyReadTest() throws InterruptedException{
//        TransportNode transportNode = new TransportNodeWithEazyReadWrite();
//        init(transportNode,"ReadWriteNode");
//        ClientManager.getClientWithClientId(1).sendReadRequest(1);
//        ClientManager.getClientWithClientId(2).sendReadRequest(1);
//    }
//
//    public static void readQuorumTest() throws InterruptedException {
//        TransportNode transportNode = new TransportNodeWithQuorum();
//        init(transportNode,"ReadWriteNodeWithQuorum");
//        ClientManager.getClientWithClientId(1).sendReadRequest(1);
//        ClientManager.getClientWithClientId(2).sendReadRequest(1);
//    }
//
//    public static void writeQuorumTest() throws InterruptedException {
//        TransportNode transportNode = new TransportNodeWithQuorum();
//        init(transportNode,"ReadWriteNodeWithQuorum");
//        ClientManager.getClientWithClientId(1).sendWriteRequest(4);
//        ClientManager.getClientWithClientId(2).sendWriteRequest(4);
//    }
//
    private static void init(){
        ClientManager.addAllClient(ClientFactory.clientGenerator(2));
        NodeManager.addAllNode(NodeFactory.nodeGenerator(3));
        Map<Integer, Data> datamap = DataFactory.dataMapGenerator(2);
        ClientManager.clientAddDataMap(datamap);
        NodeManager.nodeAddDataMap(datamap);
        ClientManager.clientAddNode();
    }

}
