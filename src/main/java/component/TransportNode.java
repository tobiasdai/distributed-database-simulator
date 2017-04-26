package component;

import manager.ClientManager;
import manager.PropertiesConfig;

import java.util.*;


/**
 * Created by dais on 2017-4-8.
 */
public class TransportNode {
    private Map<Integer, Node> nodeMap;
    private Map<Integer, Long> delayMap;
    private Map<Integer, Integer> loadMap;
    private Map<Integer, Integer> weightingMap;
    private Map<Integer, Integer> dataCheckSumMap;
    private Buffer buffer;
    private int delayWeighting;
    private int loadWeighting;
    private int randomDelayBound;
    private int writeFactor;

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
        checkBufferStatus();
        System.out.println("randomDelayBound is " + randomDelayBound + " ms");
    }

    //add node,delay,load in maps
    public void addNode(Node node) {
        int id = node.getNodeId();
        nodeMap.put(id, node);
        delayMap.put(id, node.getDelay());
        loadMap.put(id, node.getLoad());
        weightingMap.put(id, node.calculateWeighting(delayWeighting, loadWeighting));
        node.setTransportNode(this);
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
        System.out.println("The node has been deleted!");
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

    public int getRandomDelayBound() {
        return randomDelayBound;
    }

    public void chooseNode(Data data) {
        System.out.println("now start choosing Node");
        int numberOfNodes = nodeMap.size();
        writeFactor = (int) Math.floor(numberOfNodes / 2) + 1;
        int readFactor = numberOfNodes - writeFactor + 1;
        int randomNetDelay = 0;
        int chosenNodeId = 0;
        int maxVersionStamp = 0;
        long longstDelay = 0;
        Node[] chosenNodes = nodeMap.values().toArray(new Node[numberOfNodes]);
        nodeBubbleSort(chosenNodes, 'r');
        if (chosenNodes[readFactor - 1].getDelay() == 9999) {
            System.out.println("The number of selected nodes is insufficient. Please check node status.");
            return;
        }
        randomNetDelay = new Random().nextInt(randomDelayBound);
        System.out.println("random delay of transportNode: " + randomNetDelay + " ms");
        for (int i = 0; i < readFactor; i++) {
            int versionStamp = chosenNodes[i].getDataMap().get(Integer.parseInt(data.getContent())).getVersionstamp();
            if (versionStamp > maxVersionStamp) {
                chosenNodeId = i;
            }
            if (delayMap.get(chosenNodes[i].getNodeId()) > longstDelay) {
                longstDelay = delayMap.get(chosenNodes[i].getNodeId());
            }
        }
        data.setTimestamp(data.getTimestamp() + longstDelay / 2 + randomNetDelay);
        chosenNodes[chosenNodeId].getBuffer().addData(data);
        System.out.println("choose Node ended");
    }

    /**
     * This method can only be applied when all nodes have priority 1
     */
    public void notifyNodes(Data data) throws InterruptedException {
        System.out.println("now start writing");
        long randomNetDelay1 = 0;
        long randomNetDelay2 = 0;
        int numberOfNodes = nodeMap.size();
        writeFactor = (int) Math.floor(numberOfNodes / 2) + 1;
        Random random = new Random();
        Node[] nodes = nodeMap.values().toArray(new Node[numberOfNodes]);
        nodeBubbleSort(nodes, 'w');
        dataCheckSumMap.put(data.getDataId(), 0);
        if (nodes[writeFactor - 1].getDelay() == 9999) {
            System.out.println("Writing operator unpossible. Please check node status.");
            return;
        }
        randomNetDelay1 = random.nextInt(randomDelayBound);
        randomNetDelay2 = random.nextInt(randomDelayBound);
        System.out.println("Random delay 1 : " + randomNetDelay1 + " ms");
        System.out.println("Random delay 2 : " + randomNetDelay2 + " ms");
        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
            entry.getValue().getBuffer().addData(data);
        }
        while (dataCheckSumMap.get(data.getDataId()) != -1 * numberOfNodes || dataCheckSumMap.get(data.getDataId()) < writeFactor && dataCheckSumMap.get(data.getDataId()) > 0) {
            Thread.sleep(10);
        }
        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
        Data result = new Data(0);
        if (dataCheckSumMap.get(data.getDataId()) == -1 * numberOfNodes) {
            result = new Data('n', "", 0);
        } else {
            result = new Data('k', Integer.toString(data.getDataId()), 0);
        }
        System.out.println("the type of writing result is " + result.getType());
        result.setTimestamp(data.getTimestamp() + nodes[writeFactor - 1].getDelay() + randomNetDelay1 + randomNetDelay2);
        targetClient.getBuffer().addData(result);
        System.out.println("Now send ack/error back to client");
    }


    public void checkBufferStatus() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!buffer.isBufferEmpty()) {
                    for (Iterator<Data> it = buffer.getBufferList().iterator(); it.hasNext(); ) {
                        Data data = it.next();
                        it.remove();
                        if (data.getType() == 'r') {
                            chooseNode(data);
                        }
                        if (data.getType() == 'w') {
                            try {
                                notifyNodes(data);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        }, 0, 20);
    }

    public synchronized void plusDataCheckSum(int dataId) {
        dataCheckSumMap.put(dataId, dataCheckSumMap.get(dataId) + 1);
    }

    public synchronized void minusDataCheckSum(int dataId) {
        dataCheckSumMap.put(dataId, dataCheckSumMap.get(dataId) - 1);
    }

    public Buffer getBuffer() {
        return buffer;
    }

    private void nodeBubbleSort(Node[] nodes, char type) {
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

