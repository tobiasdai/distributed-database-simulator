package component;

/**
 * Created by dais on 2017-5-17.
 */
public class WriteRequestPacket extends Packet {
    private Data dataToWrite;

    public WriteRequestPacket(Data data,int timestamp,int fromClinetId) {
        dataToWrite = data;
        setDataId(data.getDataId());
        setPacketType("w");
        setTimestamp(timestamp);
        setSourceId(fromClinetId);
    }

    public Data getDataToWrite() {
        return dataToWrite;
    }

}
