package component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dais on 2017-4-8.
 */
public class Buffer {
    private List<Packet> buffer = new ArrayList<Packet>();

    public boolean isBufferEmpty() {
        return buffer.isEmpty();
    }


    public Buffer() {

    }

    public void addPacket(Packet pac){
        buffer.add(pac);
    }

    public void deleteData(Data data) {
        buffer.remove(data);
    }

//    public void addData(Buffer buff) {
//        addData(buff.getBufferList());
//    }

    public void clearBuffer() {
        buffer.clear();
    }

}
