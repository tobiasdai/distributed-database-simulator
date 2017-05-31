package component;

/**
 * Created by dais on 2017-5-17.
 */
public class Packet {
    private String packetType;
    private int timestamp;
    private int sourceId;
    private int dataId;
    public String getPacketType() {
        return packetType;
    }


    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }
}
