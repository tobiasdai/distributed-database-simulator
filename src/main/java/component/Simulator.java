package component;

import component.SimulatorEvent;

import java.util.*;

/**
 * Created by dais on 2017-4-10.
 */

public class Simulator {
    private static List<SimulatorEvent> simulatorEvents = new ArrayList<SimulatorEvent>();
    public static int currentTime = 0;

    public static void go() {
        while (!simulatorEvents.isEmpty()) {
            currentTime = simulatorEvents.get(0).getTime();
            simulatorEvents.get(0).go();
            simulatorEvents.remove(0);
        }
    }

    public static void addEvent(SimulatorEvent simulatorEvent) {
        simulatorEvents.add(simulatorEvent);
        Collections.sort(simulatorEvents, new Comparator<SimulatorEvent>() {
            @Override
            public int compare(SimulatorEvent o1, SimulatorEvent o2) {
                return new Integer(o1.getTime()).compareTo(new Integer(o2.getTime()));
            }
        });
    }

    public static void deleteEvent(int time){
        SimulatorEvent simulator = new SimulatorEvent() {
            @Override
            public void go() {

            }
        };
        for(SimulatorEvent simulator1:simulatorEvents){
            if(simulator1.getTime() == time){
                simulator = simulator1;
                break;
            }
        }
        simulatorEvents.remove(simulator);
    }

}



