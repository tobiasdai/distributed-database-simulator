package component;

import java.util.Date;

/**
 * Created by dais on 2017-4-8.
 */

/**
 * Read-Request has type 'r', dataId 0, the dataId we needed is writen in content field
 * data has type 'd'
 * ack has type 'k' , dataId 0
 * Write-Request has type 'w'
 */
public class Data {
    private int dataId;
    private char type;
    private long timestamp;
    private String content;
    private int versionstamp;
    private int clinetId;

    public Data(Data data) {
        this.dataId = data.getDataId();
        this.type = data.getType();
        this.timestamp = data.getTimestamp();
        this.content = data.getContent();
        this.versionstamp = data.getVersionstamp();
        this.clinetId = data.getClinetId();
    }

    public Data(int dataId) {
        this.dataId = dataId;
        type = 'd';
        versionstamp = 1;
        timestamp = new Date().getTime();
    }

    public Data(int dataId, int clinetId) {
        this.clinetId = clinetId;
        this.dataId = dataId;
        type = 'd';
        versionstamp = 1;
        timestamp = new Date().getTime();
    }

    public Data(char type, String content, int clinetId) {
        this.type = type;
        this.content = content;
        this.clinetId = clinetId;
    }

    public int getClinetId() {
        return clinetId;
    }

    public void setClinetId(int clinetId) {
        this.clinetId = clinetId;
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
