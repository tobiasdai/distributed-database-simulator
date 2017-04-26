package component;

import component.Buffer;
import generator.DataFactory;
import manager.PropertiesConfig;

import java.util.*;

/**
 * Created by dais on 2017-4-8.
 */


public class Client {
    private final int clientId;
    private Map<Integer, Data> dataMap;
    private TransportNode transportNode;
    private Buffer buffer;
    private Timer timer;
    private long interval = 0;
    private static long clientTimeOut = Long.parseLong(PropertiesConfig.readData("clientTimeOut"));


    public Client(int counter) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        this.transportNode = null;
        this.buffer = new Buffer();
    }

    public Client(int counter, TransportNode transportNode) {
        this.clientId = counter;
        this.dataMap = new HashMap<Integer, Data>();
        this.transportNode = transportNode;
        this.buffer = new Buffer();
    }

//    public void editTransportNode(TransportNode transportNode) {
//        this.transportNode = transportNode;
//    }

    public void sendReadRequest(int dataId) throws InterruptedException {
        Data data = new Data('r', Integer.toString(dataId), clientId);
        transportNode.getBuffer().addData(data);
        System.out.println("Read request already sent");
        long interval = receiveData(data);
        if (interval < clientTimeOut) {
            System.out.println("Read test succeeded");
        } else {
            System.out.println("time out, now sending readRequest " + dataId + " again");

            sendReadRequest(dataId);
        }
        ;
    }

    public void sendWriteRequest(int dataId) throws InterruptedException {
        Data data = new Data(dataId);
        if (dataMap.get(dataId) != null) {
            data = dataMap.get(dataId);
            data.setTimestamp(new Date().getTime());
            data.setVersionstamp(data.getVersionstamp() + 1);
        }
        changeToWriteForm(data);
        transportNode.getBuffer().addData(data);
        System.out.println("Write request already sent");
        long interval = receiveData(data);
        if (interval < clientTimeOut && interval >= 0) {
            System.out.println("Write test succeeded");
        } else if (interval == -1) {
            System.out.println("Writing invalid dataversion. please update the latest version of data from server first! ");
        } else {
            System.out.println("time out, now sending WriteRequest " + dataId + " again");
            sendWriteRequest(dataId);
        }
    }

    //	scheduleAtFixedRate是不在乎执行时间，如果运算得慢会多次。而直接schedule会考虑执行时间，往后加100ms
    //  return bufferCheck ,true means got, false means not
    public long receiveData(Data sdata) throws InterruptedException {
        timer = new Timer();
//        System.out.println("bb");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!buffer.isBufferEmpty()) {
//                    System.out.println("Buffer不为空了");
                    for (Iterator<Data> it = buffer.getBufferList().iterator(); it.hasNext(); ) {
                        Data data = it.next();
                        if (data.getType() == 'd' && data.getDataId() == Integer.parseInt(sdata.getContent())) {
                            interval = data.getTimestamp() - sdata.getTimestamp();
                            System.out.println("Client received Data " + data.getDataId() + " in " + interval + " ms");
                            if (interval < clientTimeOut) {
                                dataMap.put(data.getDataId(), new Data(data));
                            }
                            timer.cancel();
                            timer = null;
                            it.remove();
                        }
                        if (data.getType() == 'k' && sdata.getDataId() == Integer.parseInt(data.getContent())) {
                            interval = data.getTimestamp() - sdata.getTimestamp();
                            System.out.println("Client received ack for Data " + sdata.getDataId() + " in " + interval + " ms");
                            timer.cancel();
                            timer = null;
                            it.remove();
                        }
                        if (data.getType() == 'n') {
                            long tempInterval = data.getTimestamp() - sdata.getTimestamp();
                            interval = -1;
                            System.out.println("Client received versionerror for writing Data " + sdata.getDataId() + " in " + tempInterval + " ms");
                            timer.cancel();
                            timer = null;
                            it.remove();
                        }
                    }
                }

            }

        }, 0, 20);
        while (timer != null) {
            Thread.sleep(10);
        }
        return interval;
    }

    private void changeToWriteForm(Data data) {
        data.setType('w');
        data.setContent("Content");
        data.setClinetId(clientId);
    }


    public Buffer getBuffer() {
        return buffer;
    }
}
