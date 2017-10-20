package manager;


import component.Data;
import component.GeneralNode;
import component.Node;
import component.QuorumNode;
import controller.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Created by dais on 2017-5-18.
 */
public class NodeManager {
    public static int numberOfNode = Integer.parseInt(PropertiesConfig.readData("numberOfNode"));
    public static int numberOfOnlineNode;

    public static List<Node> nodeList = new ArrayList<Node>();

    public static void add(Node node) {
        nodeList.add(node);
    }

    static void addAllNode(List<Node> nodeList2) {
        nodeList.addAll(nodeList2);
    }

    public static Node getNodeWithNodeId(int id) {
        return nodeList.get(id - 1);
    }

    static void nodeAddDataMap(Map<Integer, Data> datamap) {
        String mode = PropertiesConfig.readData("strategy");
        if(mode .equals( "quorum")){
            System.out.println("Current mode : " + mode+", read factor: "+ QuorumNode.readFactor+", write factor: "+QuorumNode.writeFactor);
        }else{
        System.out.println("Current mode : " + mode);}
        for (Node node : nodeList) {
            String s = "node" + node.getNodeId() + "_initialData";
            if (PropertiesConfig.readData(s) == null) {
            } else if (PropertiesConfig.readData(s).equals("true")) {
                node.setDataMap(datamap);
            }
        }
    }

    //nodeNumber = nodeId - 1,is array subscript
    public static boolean getNodeStatus(int nodeNumber) {
        return nodeList.get(nodeNumber).getStatus();
    }

    static void changeNodeStatus(int nodeId) {
        if (nodeList.get(nodeId - 1).getStatus()) {
            nodeList.get(nodeId - 1).setStatus(false);
            System.out.println(ansi().eraseScreen().fg(YELLOW) + "        Time " + Simulator.currentTime + ": Node " + nodeId + " change status to false" + ansi().reset());
        } else {
            nodeList.get(nodeId - 1).setStatus(true);
            System.out.println(ansi().eraseScreen().fg(YELLOW) + "        Time " + Simulator.currentTime + ": Node " + nodeId + " change status to true" + ansi().reset());
        }
    }

    static void changeNodeDelay(int nodeId, int newDelay) {
        nodeList.get(nodeId - 1).setDelay(newDelay);
        System.out.println(ansi().eraseScreen().fg(YELLOW) + "        Time " + Simulator.currentTime + ": Node " + nodeId + " change delay to "+newDelay+"ms" + ansi().reset());
    }
}
