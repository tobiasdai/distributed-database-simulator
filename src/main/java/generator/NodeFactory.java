package generator;

import component.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dais on 2017-5-13.
 */
public class NodeFactory {
    private static int count = 1;

    public static List<Node> nodeGenerator(int num, String type) {
        List<Node> nodeList = new ArrayList<Node>();
        for (int i = 0; i < num; i++) {
            if (type.equals("normal")) {
                nodeList.add(new Node(count));
            }
//            else if (type.equals("ReadWriteNode")) {
//                nodeMap.put(count, new ReadWriteNode(count));
//            }else if(type.equals("ReadWriteNodeWithQuorum")){
//                nodeMap.put(count,new ReadWriteNodeWithQuorum(count));
//            }else if(type.equals("")){
//                nodeMap.put(count, new Node(count));
//            }else{
//                System.out.println("No such type of Node, please check it before you start the test.");
//                System.exit(1);
//            }
            count++;
        }
        return nodeList;
    }

    public static List<Node> nodeGenerator(int num) {
        List<Node> nodeList = new ArrayList<Node>();
        for (int i = 0; i < num; i++) {
            nodeList.add(new Node(count));
            count++;
        }

        return nodeList;
    }

}
