package component;

/**
 * Created by dais on 2017-5-17.
 */
public class Network {
    public static void transferPacket(Packet pac, Client client, Node node) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                node.receivePacket(pac);
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime + node.getDelay()/2);
        Simulator.addEvent(simulatorEvent);
    }

    public static void transferPackage(Packet pac, Node sNode, Node rNode) {
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                rNode.receivePacket(pac);
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime + Math.abs(sNode.getDelay()-rNode.getDelay())/2);
        Simulator.addEvent(simulatorEvent);
    }

    public static void transferPacket(Packet pac, Node node, Client client){
        SimulatorEvent simulatorEvent = new SimulatorEvent() {
            @Override
            public void go() {
                client.receivePacket(pac);
            }
        };
        simulatorEvent.setTime(SimulatorTimer.currentTime + node.getDelay()/2);
        Simulator.addEvent(simulatorEvent);
    }
}
