package component;

import java.util.Date;

/**
 * Created by dais on 2017-4-8.
 */



public class Data {
    private int dataId;
    private String content;
    private int versionstamp;

    public Data(){}

    public Data(int dataId){
        this.dataId = dataId;
        versionstamp = 1;
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
