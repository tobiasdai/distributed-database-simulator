package component;

/**
 * Created by dais on 2017-5-17.
 */
public class WriteRequestPacket extends Packet {
    private Data dataToWrite;

    public WriteRequestPacket(Data data,long timestamp) {
        dataToWrite = data;
        setPacketType('w');
        setTimestamp(timestamp);
    }
}
