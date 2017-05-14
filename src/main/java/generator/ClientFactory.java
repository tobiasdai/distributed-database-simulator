package generator;

import component.Client;
import component.TransportNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dais on 2017-4-10.
 */
public class ClientFactory {
    private static int count = 1;

    public static List<Client> clientGenerator(int num) {
        List<Client> list = new ArrayList<Client>();
        for (int i = 0; i < num; i++) {
            list.add(new Client(count));
            count++;
        }
        return list;
    }

    public static List<Client> clientGenerator(int num, TransportNode transportNode) {
        List<Client> list = new ArrayList<Client>();
        for (int i = 0; i < num; i++) {
            list.add(new Client(count, transportNode));
            count++;
        }
        return list;
    }

}
