package component;

import manager.ClientManager;
import manager.PropertiesConfig;

import java.util.Map;
import java.util.Random;

/**
 * Created by dais on 2017-5-11.
 */
public class TransportNodeWithEazyReadWrite extends TransportNode{

//    @Override
//    public void chooseNode(Data data) {
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Choosing Node to process read request from Client "+data.getClinetId());
//        TimeCounter.timeCounter++;
//        int numberOfNodes = nodeMap.size();
//        Node[] nodes = nodeMap.values().toArray(new Node[numberOfNodes]);
//        nodeBubbleSort(nodes, 'r');
//        if (nodes[0].getDelay() == 9999) {
//            System.out.println("All Nodes are off-line. Please check node status.");
//            System.exit(1);
//        }
//        System.out.println("Time "+TimeCounter.timeCounter+ ": The data of node "+nodes[0].getNodeId()+" was sent back to client "+data.getClinetId());
//        TimeCounter.timeCounter++;
//        data.setTimestamp(data.getTimestamp() + nodes[0].getDelay() / 2 );
//        nodes[0].getBuffer().addData(data);
//    }
//
//
//    @Override
//    public void notifyNodes(Data data) throws InterruptedException {
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Choosing Node to write data "+data.getClinetId());
//        TimeCounter.timeCounter++;
//        int numberOfNodes = nodeMap.size();
//        Random random = new Random();
//        Node[] nodes = nodeMap.values().toArray(new Node[numberOfNodes]);
//        nodeBubbleSort(nodes, 'w');
//        if (nodes[0].getDelay() == 9999) {
//            System.out.println("Time "+TimeCounter.timeCounter+ ": All Nodes are off-line. Please check node status.");
//            System.exit(1);
//        }
//        data.setTimestamp(data.getTimestamp() + nodes[0].getDelay() / 2 );
//        nodes[0].getBuffer().addData(new Data(data));
//    }
}
