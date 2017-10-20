package generator;

import component.Client;
import component.GeneralNode;
import component.Node;
import component.QuorumNode;
import manager.NodeManager;
import manager.PropertiesConfig;
import manager.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Created by dais on 2017-5-13.
 */
public class NodeFactory {
    private static int count = 1;
    private static int maximumRandomNodeDelay = Integer.parseInt(PropertiesConfig.readData("maximumRandomNodeDelay"));
    private static String strategy = PropertiesConfig.readData("strategy");

    public static List<Node> nodeGenerator(int num) {
        try {
            Strategy st = Strategy.valueOf(strategy.toUpperCase());
        } catch (Exception e) {
            System.out.println(ansi().eraseScreen().fg(RED).a("Strategy not found, please check config file again").reset());
            System.exit(1);
        }
        int numberOfOnlineNode = num;
        List<Node> list = new ArrayList<Node>();
        for (int i = 0; i < num; i++) {
            int delay = 0, load = 0;
            boolean status = false;
            String nodeDelay = PropertiesConfig.readData("node" + count + "_delay");
            String nodeLoad = PropertiesConfig.readData("node" + count + "_load");
            String nodeStatus = PropertiesConfig.readData("node" + count + "_status");
            if (nodeDelay == null) {
                delay = new Random().nextInt(maximumRandomNodeDelay);
            } else if (Integer.parseInt(nodeDelay) > 0) {
                delay = Integer.parseInt(nodeDelay);
            } else {
                System.out.println(ansi().eraseScreen().fg(RED).a("Node delay initialization error, please check the configuration file").reset());
                System.exit(1);
            }
            if (nodeLoad == null) {
                load = new Random().nextInt(101);
            } else if ((load = Integer.parseInt(nodeLoad)) >= 0 && load <= 100) {
                load = Integer.parseInt(nodeLoad);
            } else {
                System.out.println(ansi().eraseScreen().fg(RED).a("Node Load initialization error, please check the configuration file").reset());
                System.exit(1);
            }
            if (nodeStatus == null || nodeStatus.equals("true")) {
                status = true;
            } else {
                status = false;
                numberOfOnlineNode--;
            }
            switch (strategy) {
                case "general":
                    list.add(new GeneralNode(count, delay, load, status));
                    break;
                case "quorum":
                    list.add(new QuorumNode(count, delay, load, status));
                    break;
            }
            count++;
        }
        NodeManager.numberOfOnlineNode = numberOfOnlineNode;
        System.out.println("Node initialization is successful, number of nodes: " + num +" , number of online nodes: "+numberOfOnlineNode);
        return list;
    }
}
