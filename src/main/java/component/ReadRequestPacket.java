package component;

/**
 * Created by dais on 2017-5-17.
 */
public class ReadRequestPacket extends Packet{
    private int neededDataId;
    private int neededDataVersion;

    public ReadRequestPacket(int neededDataId,int neededDataVersion,long timestamp,int fromClinetId) {
        this.neededDataId = neededDataId;
        this.neededDataId = neededDataVersion;
        setSourceId(fromClinetId);
        setPacketType('r');
        setTimestamp(timestamp);
    }

    public int getNeededDataId() {
        return neededDataId;
    }

    public int getNeededDataVersion() {
        return neededDataVersion;
    }
}
