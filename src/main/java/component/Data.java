package component;

import java.util.Date;

/**
 * Created by dais on 2017-4-8.
 */
public class Data {
    private int dataId;
    private char type;
    private long timestamp;
    private String content;
    private int versionstamp;

    public Data() {
        versionstamp = 1;
        timestamp = new Date().getTime();
        System.out.println(timestamp);
    }

    public void shuchuTime() {
        System.out.println(timestamp);
    }

}
