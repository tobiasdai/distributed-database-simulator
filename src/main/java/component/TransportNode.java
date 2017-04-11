package component;

import component.Buffer;
import component.Node;

import java.util.Map;

/**
 * Created by dais on 2017-4-8.
 */
public class TransportNode {
    private Map<Integer, Node> nodeMap;
    private Map<Integer, Long> delayMap;
    private Map<Integer, Integer> loadMap;
    private Buffer buffer;
    private int delayWeighting = 1;
    private int loadWeighting = 1;


    //add node,delay,load in maps
    public void addNode(Node node) {
        nodeMap.put(node.getNodeId(), node);
        delayMap.put(node.getNodeId(), node.getDelay());
        loadMap.put(node.getNodeId(), node.getLoad());
    }

    public boolean deleteNode(Node node) {
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            if (entry.getKey() == node.getNodeId()) {
                nodeMap.remove(entry.getKey());
                delayMap.remove(entry.getKey());
                loadMap.remove(entry.getKey());
                return true;
            }
        }
        System.out.println("nodeId not found1!");
        return false;
    }

    public boolean deleteNode(int nodeId) {
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            if (entry.getKey() == nodeId) {
                nodeMap.remove(entry.getKey());
                delayMap.remove(entry.getKey());
                loadMap.remove(entry.getKey());
                return true;
            }
        }
        System.out.println("nodeId not found!!");
        return false;
    }

    public void showDelay() {
        for (Map.Entry<Integer, Long> entry : delayMap.entrySet()) {
            System.out.println("the delay of node " + entry.getKey() + ": " + entry.getValue() + "ms");
        }
    }

    public void showLoad() {
        for (Map.Entry<Integer, Integer> entry : loadMap.entrySet()) {
            System.out.println("the load of node " + entry.getKey() + ": " + entry.getValue() + "%");
        }
    }

    public void updataDelay() {
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            delayMap.put(entry.getKey(), entry.getValue().getDelay());
        }
    }

    public void updataLoad() {
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            loadMap.put(entry.getKey(), entry.getValue().getLoad());
        }
    }


    public boolean chooseNode() {
        if (!nodeMap.isEmpty()) {
            return false;
        }
        int chosenNodeId = 0;
        double maxWeightedValue = 0;
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            int key = entry.getKey();
            int weightedValue = (int) (100 - delayMap.get(key) / 10) * delayWeighting + (100 - loadMap.get(key)) * loadWeighting;
            if (weightedValue > maxWeightedValue) {
                maxWeightedValue = weightedValue;
                chosenNodeId = entry.getKey();
            }
        }
//        nodeMap.get(chosenNodeId).receiveData();
        return true;
    }


}
