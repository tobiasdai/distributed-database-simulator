package component;

/**
 * Created by dais on 2017-5-17.
 */
public class Packet {
    private char packetType;
    private long timestamp;
    private int sourceId;
    public char getPacketType() {
        return packetType;
    }


    public void setPacketType(char packetType) {
        this.packetType = packetType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getSourceId() {
        return sourceId;
    }
}
