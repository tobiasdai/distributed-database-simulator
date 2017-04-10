import java.util.ArrayList;
import java.util.List;

/**
 * Created by dais on 2017-4-8.
 */
public class Buffer {
    private List<Data> buffer = new ArrayList<Data>();

    public boolean isBufferEmpty() {
        if(buffer.isEmpty()){
            return true;
        }
        return false;
    }
}
