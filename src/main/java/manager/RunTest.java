package manager;

import component.Data;
import controller.Simulator;
import controller.SimulatorEvent;
import generator.ClientFactory;
import generator.DataFactory;
import generator.NodeFactory;

import java.util.Map;

public class RunTest {
    //All test start time can not be the same, it needs at least 1ms as interval
    public static void readTest(int clientId,int dataId,int starttime) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendReadRequest(dataId,0);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    //specified method can choose default node which client send packet to
    public static void specifiedReadTest(int clientId,int dataId,int starttime,int nodeId){
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendReadRequest(dataId,nodeId);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    public static void writeTest(int clientId,int dataId,int starttime,String content) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendWriteRequest(dataId,0,content);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    public static void specifiedwriteTest(int clientId,int dataId,int starttime,int nodeId,String content) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendWriteRequest(dataId,nodeId,content);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    public static void Init(){
        ClientManager.addAllClient(ClientFactory.clientGenerator(Integer.parseInt(PropertiesConfig.readData("numberOfClient"))));
        NodeManager.addAllNode(NodeFactory.nodeGenerator(Integer.parseInt(PropertiesConfig.readData("numberOfNode"))));
        Map<Integer, Data> datamap = DataFactory.dataMapGenerator(Integer.parseInt(PropertiesConfig.readData("numberOfInitialData")));
        ClientManager.clientAddDataMap(datamap);
        NodeManager.nodeAddDataMap(datamap);
        ClientManager.clientAddNode();
    }

    public static void changeNodeStatus(int nodeId, int starttime){
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                NodeManager.changeNodeStatus(nodeId);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    public static void changeNodeDelay(int nodeId, int newDelay, int starttime){
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                NodeManager.changeNodeDelay(nodeId,newDelay);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }
}


