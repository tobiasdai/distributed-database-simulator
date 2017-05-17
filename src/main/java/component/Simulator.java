package component;

import component.SimulatorEvent;

import java.util.*;

/**
 * Created by dais on 2017-4-10.
 */

public class Simulator {
    private static List<SimulatorEvent> simulatorEvents = new ArrayList<SimulatorEvent>();

    public static void go(){
        while(!simulatorEvents.isEmpty()){
        SimulatorTimer.currentTime = simulatorEvents.get(0).getTime();
        simulatorEvents.get(0).go();
        simulatorEvents.remove(0);
        Collections.sort(simulatorEvents, new Comparator<SimulatorEvent>() {
            @Override
            public int compare(SimulatorEvent o1, SimulatorEvent o2) {
                if(o1.getTime()>=o2.getTime()){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
    }
    }

    public static void addEvent(SimulatorEvent simulatorEvent){
        simulatorEvents.add(simulatorEvent);
    }


}



