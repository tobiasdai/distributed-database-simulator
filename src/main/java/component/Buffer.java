package component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dais on 2017-4-8.
 */
public class Buffer {
    private List<Data> buffer = new ArrayList<Data>();

    public boolean isBufferEmpty() {
        return buffer.isEmpty();
    }

    public List<Data> getBufferList() {
        return buffer;
    }

    public Buffer() {
    }

//    public Buffer(List<Data> dataList){
//        buffer.clear();
//        for (Data n:dataList){
//            buffer.add(new Data(n));
//        }
//    }
//
//    public Buffer (Buffer buffer){
//        this.buffer.clear();
//        for (Data n : buffer.getBufferList()){
//            this.buffer.add(new Data(n));
//        }
//    }

    public void addData(Data data) {
        buffer.add(new Data(data));
    }

    public void addData(List<Data> dataList) {
        for (Data n : dataList) {
            buffer.add(new Data(n));
        }
    }

    public void deleteData(Data data) {
        buffer.remove(data);
    }

    public void addData(Buffer buff) {
        addData(buff.getBufferList());
    }

    public void clearBuffer() {
        buffer.clear();
    }

}
