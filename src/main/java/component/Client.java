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
    public static long clientTimeOut = Long.parseLong(PropertiesConfig.readData("clientTimeOut"));


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

    /**
     * This Method can send a read request to node(s) through TransportNode
     * and check the answer
     * The read request is implemented as Data with type "r" and dataId 0
     *
     * @param dataId The id of the data which we need to read, was writen as content in read request(Data Instance)
     * @throws InterruptedException
     */
    public void sendReadRequest(int dataId) throws InterruptedException {
        System.out.println("Client Timeout: " + clientTimeOut + "ms");
        Data data = new Data('r', Integer.toString(dataId), clientId);
        System.out.println("Time " + TimeCounter.timeCounter + ": " + "Cleint " + clientId + " sent read request of data " + dataId);
        TimeCounter.timeCounter++;
        int randomNetDelay = new Random().nextInt(transportNode.randomDelayBound);
        System.out.println("Time " + TimeCounter.timeCounter + ": Request package from client " + clientId + " encountered a delay random delay of " + randomNetDelay + " ms");
        TimeCounter.timeCounter++;
        Data sdata = new Data(data);
        sdata.setTimestamp(data.getTimestamp() + randomNetDelay);
        transportNode.getBuffer().addData(sdata);
        long interval = receiveData(data);
        if (interval < clientTimeOut) {
            System.out.println("Cleint " + clientId + ": Read test succeeded\n");
        } else {
            System.out.println("Client " + clientId + ": Time out, now sending readRequest " + dataId + " again\n");
            System.out.println();
            sendReadRequest(dataId);
        }
        ;
    }

    /**
     * This Method can send a write request to node(s) through TransportNode
     * and check the answer
     * The read request is implemented as Data with type "w"
     *
     * @param dataId The id of the data which we need to write, was writen as dataId in write request(Data Instance)
     * @throws InterruptedException
     */
    public void sendWriteRequest(int dataId) throws InterruptedException {
        Data data = new Data(dataId);
        System.out.println("Time " + TimeCounter.timeCounter + ": " + "Cleint " + clientId + " sent write request of data " + dataId);
        TimeCounter.timeCounter++;
        if (dataMap.get(dataId) != null) {
            data = dataMap.get(dataId);
            data.setTimestamp(new Date().getTime());
            data.setVersionstamp(data.getVersionstamp() + 1);
        }
        changeToWriteForm(data);
        int randomNetDelay = new Random().nextInt(transportNode.randomDelayBound);
        System.out.println("Time " + TimeCounter.timeCounter + ": Request package from Client " + clientId + " encountered a delay random delay of " + randomNetDelay + " ms");
        TimeCounter.timeCounter++;
        Data sdata = new Data(data);
        sdata.setTimestamp(data.getTimestamp() + randomNetDelay);
        transportNode.getBuffer().addData(sdata);
        long interval = receiveData(data);
        if (interval < clientTimeOut && interval >= 0) {
            System.out.println("Cleint " + clientId + ": write test succeeded\n");
        } else if (interval == -1) {
            System.out.println("Clinet " + clientId + "： Writing invalid dataversion.Please update the latest version of data from server first! ");
            System.exit(1);
        } else {
            System.out.println("Client " + clientId + ": time out, now sending writeRequest " + dataId + " again\n");
            System.out.println();
            sendWriteRequest(dataId);
        }
    }

    /**
     * This method checks the buffer to get the answer of node after the client sends the request
     * Periodic check : 20ms
     * @param sdata the request we sent
     * @return
     * @throws InterruptedException
     */
    public long receiveData(Data sdata) throws InterruptedException {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!buffer.isBufferEmpty()) {
                    for (Iterator<Data> it = buffer.getBufferList().iterator(); it.hasNext(); ) {
                        Data data = it.next();
                        if (data.getType() == 'd' && data.getDataId() == Integer.parseInt(sdata.getContent())) {
                            interval = data.getTimestamp() - sdata.getTimestamp();
                            System.out.println("Time " + TimeCounter.timeCounter + ": Client " + clientId + " received Data " + data.getDataId() + " in " + interval + " ms");
                            TimeCounter.timeCounter++;
                            if (interval < clientTimeOut) {
                                dataMap.put(data.getDataId(), new Data(data));
                            }
                            timer.cancel();
                            timer = null;
                            it.remove();
                        }
                        if (data.getType() == 'k' && sdata.getDataId() == Integer.parseInt(data.getContent())) {
                            interval = data.getTimestamp() - sdata.getTimestamp();
                            System.out.println("Time " + TimeCounter.timeCounter + ": Client " + clientId + " received ack for Data " + sdata.getDataId() + " in " + interval + " ms");
                            TimeCounter.timeCounter++;
                            timer.cancel();
                            timer = null;
                            it.remove();
                        }
                        if (data.getType() == 'n') {
                            long tempInterval = data.getTimestamp() - sdata.getTimestamp();
                            interval = -1;
                            System.out.println("Time " + TimeCounter.timeCounter + ": Client " + clientId + " received versionerror for Data " + sdata.getDataId() + " in " + tempInterval + " ms");
                            TimeCounter.timeCounter++;
                            timer.cancel();
                            timer = null;
                            it.remove();
                        }
                    }
                }

            }

        }, 0, 20);
        while (timer != null) {
            Thread.sleep(20);
        }
        return interval;
    }

    /**
     * This method changes the Data instance in a write request Form
     * @param data
     */
    private void changeToWriteForm(Data data) {
        data.setType('w');
        data.setContent("Content");
        data.setClinetId(clientId);
    }


    public Buffer getBuffer() {
        return buffer;
    }
}
