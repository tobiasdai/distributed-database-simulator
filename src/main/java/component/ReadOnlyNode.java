package component;

import manager.ClientManager;

import java.util.Random;

/**
 * Created by dais on 2017-5-4.
 */
public class ReadOnlyNode extends Node {
    public ReadOnlyNode(int id) {
        super(id);
    }

//    @Override
//    public void receiveAndSendData(Data data) {
//        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
//        long randomNetDelay = new Random().nextInt(getTransportNode().getRandomDelayBound());
//        if (data.getType() == 'r') {
//            Data result = new Data(getDataMap().get(Integer.parseInt(data.getContent())));
//            result.setTimestamp(data.getTimestamp() + getDelay() / 2 + randomNetDelay);
//            System.out.println("Time  " + TimeCounter.timeCounter + ": Package from node " + getNodeId() + " to Cleint " + data.getClinetId() + " encountered a random delay of " + randomNetDelay + " ms");
//            TimeCounter.timeCounter++;
//            targetClient.getBuffer().addData(result);
//        } else {
//            System.out.println("This node can not write data,please check your program again.");
//            System.exit(1);
//        }
//    }

}

