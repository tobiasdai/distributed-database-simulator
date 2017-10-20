package component;

/**
 * Created by dais on 2017-5-18.
 */
public class DataCopyPacket extends Packet{
    private Data dataToCopy;

    public DataCopyPacket(Data data,int timestamp,int fromNodeId){
        setDataId(data.getDataId());
        setPacketType("c");
        setTimestamp(timestamp);
        setSourceId(fromNodeId);
        dataToCopy = data;
    }

    public Data getDataToCopy() {
        return dataToCopy;
    }
}
