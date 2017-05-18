package useless;

/**
 * Created by dais on 2017-5-5.
 */
public class TransportNodeWithQuorum extends TransportNode {

//    private Map<Integer, Integer> dataCheckSumMap;
//
//    @Override
//    public void chooseNode(Data data) {
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Choose Node to process read request from Client "+data.getClinetId());
//        TimeCounter.timeCounter++;
//        int numberOfNodes = nodeMap.size();
//        writeFactor = (int) Math.floor(numberOfNodes / 2) + 1;
//        int readFactor = numberOfNodes - writeFactor + 1;
//        int chosenNodeId = 0;
//        int maxVersionStamp = 0;
//        long longstDelay = 0;
//        Node[] chosenNodes = nodeMap.values().toArray(new Node[numberOfNodes]);
//        nodeBubbleSort(chosenNodes, 'r');
//        if (chosenNodes[readFactor - 1].getDelay() == 9999) {
//            System.out.println("The number of selected nodes is insufficient. Please check node status.");
//            return;
//        }
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Read quorum started (readFactor :"+readFactor+")");
//        TimeCounter.timeCounter++;
//        for (int i = 0; i < readFactor; i++) {
//            System.out.println("         Node "+chosenNodes[i].getNodeId()+" is chosen als one of Quorum factors");
//            int versionStamp = chosenNodes[i].getDataMap().get(Integer.parseInt(data.getContent())).getVersionstamp();
//            if (versionStamp > maxVersionStamp) {
//                chosenNodeId = i;
//            }
//            if (delayMap.get(chosenNodes[i].getNodeId()) > longstDelay) {
//                longstDelay = delayMap.get(chosenNodes[i].getNodeId());
//            }
//        }
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Read quorum ended, the data of node "+chosenNodes[chosenNodeId].getNodeId()+" was sent back to client "+data.getClinetId());
//        TimeCounter.timeCounter++;
//        data.setTimestamp(data.getTimestamp() + longstDelay / 2 );
//        chosenNodes[chosenNodeId].getBuffer().addData(data);
//    }
//
//
//    @Override
//    public void notifyNodes(Data data) throws InterruptedException {
//        System.out.println("Time "+TimeCounter.timeCounter+ ": All the nodes started to write data "+data.getClinetId());
//        TimeCounter.timeCounter++;
//        long randomNetDelay2 = 0;
//        int numberOfNodes = nodeMap.size();
//        writeFactor = (int) Math.floor(numberOfNodes / 2) + 1;
//        Random random = new Random();
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Write quorum started (writeFactor :"+writeFactor+")");
//        TimeCounter.timeCounter++;
//        Node[] nodes = nodeMap.values().toArray(new Node[numberOfNodes]);
//        nodeBubbleSort(nodes, 'w');
//        dataCheckSumMap=getDataCheckSumMap();
//        dataCheckSumMap.put(data.getDataId(), 0);
//        if (nodes[writeFactor - 1].getDelay() == 9999) {
//            System.out.println("Time "+TimeCounter.timeCounter+ ": Writing operation unpossible. Please check node status.");
//            System.exit(1);
//        }
//        for (Map.Entry<Integer, Node> entry : nodeMap.entrySet()) {
//            entry.getValue().getBuffer().addData(data);
//        }
//        while (true){
//            if(dataCheckSumMap.get(data.getDataId()) == -1 * numberOfNodes && dataCheckSumMap.get(data.getDataId())< 0){
//                break;
//            }
//            if(dataCheckSumMap.get(data.getDataId()) >= writeFactor && dataCheckSumMap.get(data.getDataId()) > 0){
//                break;
//            }
//            Thread.sleep(20);
//        }
//        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
//        Data result = new Data(0);
//        if (dataCheckSumMap.get(data.getDataId()) == -1 * numberOfNodes) {
//            System.out.println("Time "+TimeCounter.timeCounter+ ": All of the nodes failed to write Data "+data.getClinetId());
//            TimeCounter.timeCounter++;
//            result = new Data('n', "", 0);
//        } else {
//            result = new Data('k', Integer.toString(data.getDataId()), 0);
//            System.out.println("Time "+TimeCounter.timeCounter+ ": "+dataCheckSumMap.get(data.getDataId())+" Node(s) have wroten Data "+data.getClinetId()+" ,sent ack back");
//            TimeCounter.timeCounter++;
//        }
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Write quorum ended");
//        TimeCounter.timeCounter++;
//        randomNetDelay2 = random.nextInt(randomDelayBound);
//        result.setTimestamp(data.getTimestamp() + nodes[writeFactor - 1].getDelay() + randomNetDelay2);
//        targetClient.getBuffer().addData(result);
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Node sent ack /error back to client");
//        TimeCounter.timeCounter++;
//        System.out.println("Time "+TimeCounter.timeCounter+ ": Ack/error package encountered a random delay of "+ randomNetDelay2 + " ms");
//        TimeCounter.timeCounter++;
//    }

}
