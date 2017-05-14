package component;

import manager.ClientManager;

import java.util.Map;
import java.util.Random;

/**
 * Created by dais on 2017-5-4.
 * This Class offers the base strategy "easy read and easy write"
 */
public class ReadWriteNode extends Node{
    public ReadWriteNode(int id){
        super(id);
    }

    public void receiveAndSendData(Data data) {
        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
        long randomNetDelay = new Random().nextInt(getTransportNode().getRandomDelayBound());
        if (data.getType() == 'r') {
            Data result = new Data(getDataMap().get(Integer.parseInt(data.getContent())));
            result.setTimestamp(data.getTimestamp() + getDelay() / 2 + randomNetDelay);
            System.out.println("Time "+TimeCounter.timeCounter+ ": Package from node "+getNodeId()+" to Cleint "+data.getClinetId()+" encountered a random delay of "+ randomNetDelay + " ms");
            TimeCounter.timeCounter++;
            targetClient.getBuffer().addData(result);
        }
        if (data.getType() == 'w') {
            Data result = new Data(0);
            result.setTimestamp(data.getTimestamp() + getDelay() / 2 + randomNetDelay);
            if (getDataMap().get(data.getDataId()) == null && data.getVersionstamp() == 1 || data.getVersionstamp() > getDataMap().get(data.getDataId()).getVersionstamp()) {
                data.setType('d');
                getDataMap().put(data.getDataId(), new Data(data));
                result = new Data('k', Integer.toString(data.getDataId()), 0);
                System.out.println("Time "+TimeCounter.timeCounter+ ": Node "+getNodeId()+" has wroten Data "+data.getClinetId()+" ,sent ack back");
                TimeCounter.timeCounter++;
            }else{
               result = new Data('n', "", 0);
            }
            result.setTimestamp(data.getTimestamp() + getDelay() / 2 + randomNetDelay);
            System.out.println("Time "+TimeCounter.timeCounter+ ": Ack/error package encountered a random delay of "+ randomNetDelay + " ms");
            TimeCounter.timeCounter++;
            targetClient.getBuffer().addData(result);
            copyData(data);
        }
    }

    private void copyData(Data data){
        for (Map.Entry<Integer, Node> entry : getTransportNode().getNodeMap().entrySet()) {
            if(entry.getKey()==getNodeId()){
                break;
            }
            if(entry.getValue().getDelay()==9999){
                System.out.println("Node "+entry.getKey()+" was off-line, failed to copy data "+data.getDataId());
                break;
            }
            entry.getValue().getDataMap().put(data.getDataId(), new Data(data));
        }
        System.out.println("Time "+TimeCounter.timeCounter+ ": All of nodes copied the data "+data.getDataId());
        TimeCounter.timeCounter++;
    }

}
