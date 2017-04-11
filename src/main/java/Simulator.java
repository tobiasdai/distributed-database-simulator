import component.Client;
import component.TransportNode;
import generator.ClientFactory;

import java.util.List;

/**
 * Created by dais on 2017-4-10.
 */
//missing gui

public class Simulator {
    public Simulator() {
        TransportNode transportNode = new TransportNode();
        List<Client> list = ClientFactory.clientGenerator(3, transportNode);

    }
}



