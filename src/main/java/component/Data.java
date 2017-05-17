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
 * type 'n' means failed
 */


public class Data {
    private int dataId;
    private String content;
    private int versionstamp;

    public Data(int dataId){
        this.dataId = dataId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
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
