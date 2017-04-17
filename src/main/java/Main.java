import component.Client;
import component.Data;
import component.Node;
import component.TransportNode;
import generator.ClientFactory;
import generator.DataFactory;
import manager.ClientManager;


/**
 * Created by dais on 2017-4-8.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        TransportNode transportNode = new TransportNode();
        Node node1 = new Node();
        transportNode.addNode(node1);
        node1.setDataMap(DataFactory.dataGenerator(1));
        Client client1 = ClientFactory.clientGenerator(1, transportNode).get(0);
        ClientManager.add(client1);
        client1.sendReadRequest(1);
    }
}
