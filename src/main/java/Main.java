import controller.Simulator;
import manager.*;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;


/**
 * Created by dais on 2017-4-8.
 */
public class Main {

    public static void main(String[] args) throws Exception {
//        generalTest();

//        quorumTest();

        timeoutTest();




    }
























    public static void generalTest(){
        PropertiesConfig.properties=PropertiesConfig.getPropertiesConfig("config1.properties");
        RunTest.Init();
        RunTest.writeTest(1,1,0,"");
        RunTest.readTest(2,1,5000);
        RunTest.writeTest(2,1,13000,"");
        RunTest.writeTest(1,1,20000,"");
        Simulator.go();
    }


    public static void quorumTest(){
        PropertiesConfig.properties=PropertiesConfig.getPropertiesConfig("config2.properties");
        RunTest.Init();
        RunTest.writeTest(1,1,0,"");
        RunTest.readTest(2,1,5000);
        RunTest.writeTest(2,1,13000,"");
        RunTest.writeTest(1,1,20000,"");
        Simulator.go();
    }


    public static void timeoutTest(){
        PropertiesConfig.properties=PropertiesConfig.getPropertiesConfig("config3.properties");
        RunTest.Init();
        RunTest.writeTest(1,1,0,"");
        Simulator.go();
    }


}
