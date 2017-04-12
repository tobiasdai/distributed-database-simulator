package component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dais on 2017-4-8.
 */
public class Buffer {
    private List<Data> buffer = new ArrayList<Data>();

    public boolean isBufferEmpty() {
        if (buffer.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<Data> getBufferList() {
        return buffer;
    }

    public void putData(Data data) {
        buffer.add(data);
    }

    public void putData(List<Data> dataList) {
        for (Data data : dataList) {
            buffer.add(data);
        }
    }

}
