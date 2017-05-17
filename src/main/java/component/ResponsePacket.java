package component;

/**
 * Created by dais on 2017-5-17.
 */
public class ResponsePacket extends Packet{
    private Data responseData;

    public ResponsePacket(char type,Data data,long timestamp){
        setPacketType(type);
        responseData = data;
        setTimestamp(timestamp);
    }


}
