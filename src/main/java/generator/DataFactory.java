package generator;

import component.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dais on 2017-4-10.
 */
public class DataFactory {
    private static int counter = 1;

    public static Map<Integer, Data> dataMapGenerator(int num) {
        Map<Integer, Data> dataMap = new HashMap<Integer, Data>();
        for (int i = 0; i < num; i++) {
            Data data = new Data(counter);
            dataMap.put(counter, data);
            counter++;
        }
        return dataMap;
    }

    public static Data dataGenerator() {
        return new Data(counter++);
    }




}

