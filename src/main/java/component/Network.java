package component;

import controller.Simulator;
import controller.SimulatorEvent;
import manager.PropertiesConfig;

import java.util.Random;

/**
 * Created by dais on 2017-5-17.
 */
public class Network {
    public static int maximumRandomNetworkDelay = Integer.parseInt(PropertiesConfig.readData("maximumRandomNetworkDelay"));

    public static void transferPacket(Packet pac, Client client, Node node) {
        int randomDelay = new Random().nextInt(maximumRandomNetworkDelay);
        System.out.println("        Random delay from client "+client.getClientId()+" to node "+node.getNodeId()+":"+randomDelay+" ms, total delay = "+(node.getDelay()/2+randomDelay)+"ms");
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                node.receivePacket(pac);
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + node.getDelay()/2 + randomDelay);
        Simulator.addEvent(simulatorEvent);
    }

    public static void transferPacket(Packet pac, Node sNode, Node rNode) {
        int randomDelay = new Random().nextInt(maximumRandomNetworkDelay);
        System.out.println("        Random delay from node "+sNode.getNodeId()+" to node "+rNode.getNodeId()+":"+randomDelay+" ms, total delay = "+(Math.abs(sNode.getDelay()-rNode.getDelay())/2+randomDelay)+"ms");
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                rNode.receivePacket(pac);
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + Math.abs(sNode.getDelay()-rNode.getDelay())/2+randomDelay);
        Simulator.addEvent(simulatorEvent);
    }

    public static void transferPacket(Packet pac, Node node, Client client){
        int randomDelay = new Random().nextInt(maximumRandomNetworkDelay);
        System.out.println("        Random delay from node "+node.getNodeId()+" to client "+client.getClientId()+":"+randomDelay+" ms, total delay = "+(node.getDelay()/2+randomDelay)+"ms");
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                client.receivePacket(pac);
            }
        };
        simulatorEvent.setTime(Simulator.currentTime + node.getDelay()/2+ randomDelay);
        Simulator.addEvent(simulatorEvent);
    }
}
