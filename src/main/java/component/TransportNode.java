package component;

import manager.ClientManager;
import manager.PropertiesConfig;

import java.util.*;


/**
 * Created by dais on 2017-4-8.
 * The class "TransportNode" simulate the network or you are regard it as Reverse proxy node
 * It receives the Data from node and implements the strategy of chooing node to process request
 */
public class TransportNode {
    protected Map<Integer, Node> nodeMap;
    protected Map<Integer, Long> delayMap;
    protected Map<Integer, Integer> loadMap;
    protected Map<Integer, Integer> weightingMap;
    protected Map<Integer, Integer> dataCheckSumMap;
    protected Buffer buffer;
    protected int delayWeighting;
    protected int loadWeighting;
    protected int randomDelayBound;
    protected int writeFactor;
    protected int readFactor;

    public TransportNode() {
        nodeMap = new HashMap<Integer, Node>();
        delayMap = new HashMap<Integer, Long>();
        loadMap = new HashMap<Integer, Integer>();
        weightingMap = new HashMap<Integer, Integer>();
        dataCheckSumMap = new HashMap<Integer, Integer>();
        buffer = new Buffer();
        randomDelayBound = Integer.parseInt(PropertiesConfig.readData("maximumRandomDelay"));
        delayWeighting = Integer.parseInt(PropertiesConfig.readData("delayWeighting"));
        loadWeighting = Integer.parseInt(PropertiesConfig.readData("loadWeighting"));
//        checkBufferStatus();
        System.out.println("RandomDelayBound: " + randomDelayBound + " ms");
        System.out.println("Client Timeout: " + Client.clientTimeOut + "ms");
        System.out.println("Writefactor :"+PropertiesConfig.readData("writeFactor"));
    }

    //add node,delay,load in maps
    public void addNode(Node node) {
        int id = node.getNodeId();
        nodeMap.put(id, node);
        delayMap.put(id, node.getDelay());
        loadMap.put(id, node.getLoad());
        weightingMap.put(id, node.calculateWeighting(delayWeighting, loadWeighting));
    }

    public void generateNodeMap(Map<Integer, Node> nodeMap) {
        this.nodeMap = nodeMap;
        updataDelay();
        updataLoad();
        updataWeighting();
    }

    public boolean deleteNode(Node node) throws Exception {
        return deleteNode(node.getNodeId());
    }

    public boolean deleteNode(int nodeId) throws Exception {
        try {
            nodeMap.remove(nodeId);
            delayMap.remove(nodeId);
            loadMap.remove(nodeId);
        } catch (Exception e) {
            System.out.println("nodeId not found1!");
            return false;
        }
        return true;
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

    public void showWeighting() {
        for (Map.Entry<Integer, Integer> entry : weightingMap.entrySet()) {
            System.out.println("the weighting of node " + entry.getKey() + ": " + entry.getValue() + "points");
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

    public void updataWeighting(){
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            weightingMap.put(entry.getKey(), entry.getValue().calculateWeighting(delayWeighting, loadWeighting));
        }
    }

    public Map<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    public Map<Integer, Integer> getDataCheckSumMap() {
        return dataCheckSumMap;
    }

    public int getRandomDelayBound() {
        return randomDelayBound;
    }

    public void chooseNode(Data data) {
    }

    public void notifyNodes(Data data) throws InterruptedException {
    }


//    public void checkBufferStatus() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!buffer.isBufferEmpty()) {
//                    if(PropertiesConfig.readData("writeFactor").equals("default")){
//                        writeFactor = (int) Math.floor(nodeMap.size() / 2) + 1;
//                        readFactor = nodeMap.size() - writeFactor + 1;
//                    }else{
//                        //new writeFactor here
//                    }
//                    for (ListIterator<Data> it = buffer.getBufferList().listIterator(); it.hasNext(); ) {
//                        Data data = it.next();
//                        it.remove();
//                        try {
//                            if (data.getType() == 'r') {
//                                chooseNode(data);
//                                break;
//                            }
//                            if (data.getType() == 'w') {
//                                notifyNodes(data);
//                                break;
//                            }
//                            Thread.sleep(20);
//                        } catch (InterruptedException e) {
//                        }
//                    }
//                }
//            }
//        }, 0, 40);
//    }

    public synchronized void plusDataCheckSum(int dataId) {
        dataCheckSumMap.put(dataId, dataCheckSumMap.get(dataId) + 1);
    }

    public synchronized void minusDataCheckSum(int dataId) {
        dataCheckSumMap.put(dataId, dataCheckSumMap.get(dataId) - 1);
    }

    public Buffer getBuffer() {
        return buffer;
    }

    protected void nodeBubbleSort(Node[] nodes, char type) {
        for (int i = 0; i < nodes.length - 1; i++)
            for (int j = i + 1; j < nodes.length; j++) {
                Node temp;
                if (type == 'r') {
                    if (weightingMap.get(nodes[i].getNodeId()) < weightingMap.get(nodes[j].getNodeId())) {
                        temp = nodes[i];
                        nodes[i] = nodes[j];
                        nodes[j] = temp;
                    }
                }
                if (type == 'w') {
                    if (delayMap.get(nodes[i].getNodeId()) < delayMap.get(nodes[j].getNodeId())) {
                        temp = nodes[i];
                        nodes[i] = nodes[j];
                        nodes[j] = temp;
                    }
                }

            }
    }
}

