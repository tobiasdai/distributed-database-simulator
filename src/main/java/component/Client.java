package component;

import component.Buffer;

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
    private boolean bufferCheck;


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
        if (receiveData(data)) {
            System.out.println("Test succeeded");
        } else {
            System.out.println("time out,now try again");
            sendReadRequest(dataId);
        }
        ;
    }

    public void sendWriteRequest() {
        Data data = new Data('w', "Content", clientId);
        transportNode.getBuffer().addData(data);
        System.out.println("Write request already sent");
    }

    //	scheduleAtFixedRate是不在乎执行时间，如果运算得慢会多次。而直接schedule会考虑执行时间，往后加100ms
    public boolean receiveData(Data sdata) throws InterruptedException {
        timer = new Timer();
        bufferCheck = false;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                timer = null;
            }
        }, 2000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!buffer.isBufferEmpty()) {
                    for (Iterator<Data> it = buffer.getBufferList().iterator(); it.hasNext(); ) {
                        Data data = it.next();
                        it.remove();
                        if (data.getDataId() == Integer.parseInt(sdata.getContent())) {
                            System.out.println("Client received successfully");
                            dataMap.put(data.getDataId(), new Data(data));
                            timer.cancel();
                            timer = null;
                            bufferCheck = true;
                        }
                    }
                }

            }

        }, 0, 100);
        while (timer != null) {
            Thread.sleep(10);
        }
        return bufferCheck;
    }


    public Buffer getBuffer() {
        return buffer;
    }
}
