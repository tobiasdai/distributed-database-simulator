package useless;

import component.Data;
import useless.ReadWriteNode;

/**
 * Created by dais on 2017-5-13.
 * This Class implements the strategy "read-and writequorum"
 */
public class ReadWriteNodeWithQuorum extends ReadWriteNode {
    public ReadWriteNodeWithQuorum(int id){
        super(id);
    }

    public void receiveAndSendData(Data data) {
//        Client targetClient = ClientManager.getClientWithClientId(data.getClinetId());
//        long randomNetDelay = new Random().nextInt(getTransportNode().getRandomDelayBound());
//        if (data.getType() == 'r') {
//            Data result = new Data(getDataMap().get(Integer.parseInt(data.getContent())));
//            result.setTimestamp(data.getTimestamp() + getDelay() / 2 + randomNetDelay);
//            System.out.println("Time "+TimeCounter.timeCounter+ ": Package from node "+getNodeId()+" to Cleint "+data.getClinetId()+" encountered a random delay of "+ randomNetDelay + " ms");
//            TimeCounter.timeCounter++;
//            targetClient.getBuffer().addData(result);
//        }
//        if (data.getType() == 'w') {
//            if (getDataMap().get(data.getDataId()) == null && data.getVersionstamp() == 1 || data.getVersionstamp() > getDataMap().get(data.getDataId()).getVersionstamp()) {
//                data.setType('d');
//                getDataMap().put(data.getDataId(), new Data(data));
//                getTransportNode().plusDataCheckSum(data.getDataId());
//            } else {
//                getTransportNode().minusDataCheckSum(data.getDataId());
//            }
//        }
    }
}
