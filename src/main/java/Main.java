import component.*;
import generator.ClientFactory;
import generator.DataFactory;
import generator.NodeFactory;
import manager.ClientManager;
import manager.PropertiesConfig;

import java.util.List;
import java.util.Map;


/**
 * Created by dais on 2017-4-8.
 */
public class Main {
    private static String mode = PropertiesConfig.readData("mode");
    public static void main(String[] args) throws Exception {
        System.out.println("Test start! Mode:"+PropertiesConfig.readData("mode"));
        if(PropertiesConfig.readData("mode").equals("writeQuorum")){
            writeQuorumTest();
        }else if(PropertiesConfig.readData("mode").equals("readQuorum")){
            readQuorumTest();
        }else if(PropertiesConfig.readData("mode").equals("eazyRead")){
            eazyReadTest();
        }else if(PropertiesConfig.readData("mode").equals("eazyWrite")){
            eazyWriteTest();
        }
    }








    public static void eazyWriteTest() throws InterruptedException{
        TransportNode transportNode = new TransportNodeWithEazyReadWrite();
        init(transportNode,"ReadWriteNode");
        ClientManager.getClientWithClientId(1).sendWriteRequest(2);
        ClientManager.getClientWithClientId(2).sendWriteRequest(2);
    }



    public static void eazyReadTest() throws InterruptedException{
        TransportNode transportNode = new TransportNodeWithEazyReadWrite();
        init(transportNode,"ReadWriteNode");
        ClientManager.getClientWithClientId(1).sendReadRequest(1);
        ClientManager.getClientWithClientId(2).sendReadRequest(1);
    }

    public static void readQuorumTest() throws InterruptedException {
        TransportNode transportNode = new TransportNodeWithQuorum();
        init(transportNode,"ReadWriteNodeWithQuorum");
        ClientManager.getClientWithClientId(1).sendReadRequest(1);
        ClientManager.getClientWithClientId(2).sendReadRequest(1);
    }

    public static void writeQuorumTest() throws InterruptedException {
        TransportNode transportNode = new TransportNodeWithQuorum();
        init(transportNode,"ReadWriteNodeWithQuorum");
        ClientManager.getClientWithClientId(1).sendWriteRequest(4);
        ClientManager.getClientWithClientId(2).sendWriteRequest(4);
    }

    private static void init(TransportNode transportNode,String nodetype){
        transportNode.generateNodeMap(NodeFactory.nodeGenerator(2,nodetype));
        Map<Integer, Data> datamap = DataFactory.dataMapGenerator(1);
        for(Map.Entry<Integer,Node> entry:transportNode.getNodeMap().entrySet()){
            entry.getValue().setDataMap(datamap);
            entry.getValue().setTransportNode(transportNode);
        }
        ClientManager.addAllClient(ClientFactory.clientGenerator(2, transportNode));
    }

}
