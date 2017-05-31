package component;

/**
 * Created by dais on 2017-5-17.
 */
public class ResponsePacket extends Packet{
    private Data responseData;

    public ResponsePacket(int dataId,String type,Data data,int timestamp){
        setDataId(dataId);
        setPacketType(type);
        responseData = data;
        setTimestamp(timestamp);
    }

    public Data getResponseData() {
        return responseData;
    }
}
