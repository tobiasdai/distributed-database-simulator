import component.*;
import generator.ClientFactory;
import generator.DataFactory;
import generator.NodeFactory;
import manager.ClientManager;
import manager.NodeManager;
import manager.PropertiesConfig;

import java.util.List;
import java.util.Map;


/**
 * Created by dais on 2017-4-8.
 */
public class Main {
    private static String strategy = PropertiesConfig.readData("strategy");

    public static void main(String[] args) throws Exception {
        try{
        Strategy st = Strategy.valueOf(strategy.toUpperCase());}
        catch (Exception e){
            System.out.println("Strategy not found, please check config file again");
            System.exit(1);
        }
        Init(strategy);
//        readTest(1,1,0);
//        readTest(2,500,15);
//        writeTest(2,1,0);
//        writeTest(1,300,35);
        Simulator.go();
    }


    public static void readTest(int clientId,int dataId,int starttime) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendReadRequest(dataId);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    public static void writeTest(int clientId,int dataId,int starttime) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                ClientManager.getClientWithClientId(clientId).sendWriteRequest(dataId);
            }
        };
        simulatorEvent.setTime(starttime);
        Simulator.addEvent(simulatorEvent);
    }

    private static void Init(String strategy){
        ClientManager.addAllClient(ClientFactory.clientGenerator(Integer.parseInt(PropertiesConfig.readData("numberOfClient"))));
        NodeManager.addAllNode(NodeFactory.nodeGenerator(Integer.parseInt(PropertiesConfig.readData("numberOfNode")),strategy));
        Map<Integer, Data> datamap = DataFactory.dataMapGenerator(Integer.parseInt(PropertiesConfig.readData("numberOfInitialData")));
        ClientManager.clientAddDataMap(datamap);
        NodeManager.nodeAddDataMap(datamap);
        ClientManager.clientAddNode();
    }


}
