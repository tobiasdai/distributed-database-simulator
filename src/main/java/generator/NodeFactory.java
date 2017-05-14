package generator;

import component.Node;
import component.ReadOnlyNode;
import component.ReadWriteNode;
import component.ReadWriteNodeWithQuorum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dais on 2017-5-13.
 */
public class NodeFactory {
    private static int count = 1;

    public static Map<Integer, Node> nodeGenerator(int num, String type) {
        Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        for (int i = 0; i < num; i++) {
            if (type.equals("ReadOnlyNode")) {
                nodeMap.put(count, new ReadOnlyNode(count));
            } else if (type.equals("ReadWriteNode")) {
                nodeMap.put(count, new ReadWriteNode(count));
            }else if(type.equals("ReadWriteNodeWithQuorum")){
                nodeMap.put(count,new ReadWriteNodeWithQuorum(count));
            }else if(type.equals("")){
                nodeMap.put(count, new Node(count));
            }else{
                System.out.println("No such type of Node, please check it before you start the test.");
                System.exit(1);
            }
            count++;
        }
        return nodeMap;
    }

    public static Map<Integer, Node> nodeGenerator(int num) {
        Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        for (int i = 0; i < num; i++) {
            nodeMap.put(count, new Node(count));
            count++;
        }

        return nodeMap;
    }

}
