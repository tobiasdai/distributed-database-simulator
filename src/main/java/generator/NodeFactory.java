package generator;

import component.Client;
import component.GeneralNode;
import component.Node;
import component.QuorumNode;
import manager.PropertiesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dais on 2017-5-13.
 */
public class NodeFactory {
    private static int count = 1;
    private static int maximumRandomNodeDelay = Integer.parseInt(PropertiesConfig.readData("maximumRandomNodeDelay"));

        public static List<Node> nodeGenerator(int num,String strategy) {
            List<Node> list = new ArrayList<Node>();
            for (int i = 0; i < num; i++) {
                int delay,load = 0;
                String nodeDelay = PropertiesConfig.readData("node"+count+"_delay");
                String nodeLoad = PropertiesConfig.readData("node"+count+"_load");
                if(nodeDelay==null){
                    delay = new Random().nextInt(maximumRandomNodeDelay);
                }else if((delay = Integer.parseInt(nodeDelay))<0){
                    System.out.println("Node dealy can not be less than 0, please check the config da");
                    System.exit(1);
                }
                if(nodeLoad==null){
                    load = new Random().nextInt(101);
                }else if((load = Integer.parseInt(nodeLoad))<0&&load>100){
                    System.out.println("Node Load can not be less than 0 or greater than 100, olease check the config file");
                    System.exit(1);
                }
                switch (strategy){
                    case "general":
                        list.add(new GeneralNode(count,delay,load));
                        break;
                    case "quorum":
                        list.add(new QuorumNode(count,delay,load));
                        break;
                }
                count++;
            }
            return list;
    }
}
