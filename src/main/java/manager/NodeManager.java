package manager;


import component.Data;
import component.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dais on 2017-5-18.
 */
public class NodeManager {
    public static List<Node> nodeList = new ArrayList<Node>();

    public static void add(Node node) {
        nodeList.add(node);
    }

    public static void addAllNode(List<Node> nodeList2){
        nodeList.addAll(nodeList2);
    }

    public static Node getNodeWithClientId(int id) {
        return nodeList.get(id-1);
    }

    public static void nodeAddDataMap(Map<Integer, Data> datamap){
        for(Node node:nodeList){
            node.setDataMap(datamap);
        }
    }
}
