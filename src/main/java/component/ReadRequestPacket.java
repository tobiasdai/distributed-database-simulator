package component;

/**
 * Created by dais on 2017-5-17.
 */
public class ReadRequestPacket extends Packet{
    private int neededDataVersion;

    public ReadRequestPacket(int neededDataId,int neededDataVersion,int timestamp,int fromClinetId) {
        this.neededDataVersion = neededDataVersion;
        setDataId(neededDataId);
        setSourceId(fromClinetId);
        setPacketType("r");
        setTimestamp(timestamp);
    }

    public int getNeededDataVersion() {
        return neededDataVersion;
    }

    public void setNeededDataVersion(int neededDataVersion) {
        this.neededDataVersion = neededDataVersion;
    }
}
