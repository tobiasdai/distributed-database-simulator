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

    public Data(Data data) {
        this.dataId = data.getDataId();
        this.type = data.getType();
        this.timestamp = data.getTimestamp();
        this.content = data.getContent();
        this.versionstamp = data.getVersionstamp();
    }

    public Data(int id) {
        dataId = id;
        versionstamp = 1;
        timestamp = new Date().getTime();
    }

    public Data(char type) {
        this.type = type;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVersionstamp() {
        return versionstamp;
    }

    public void setVersionstamp(int versionstamp) {
        this.versionstamp = versionstamp;
    }
}
