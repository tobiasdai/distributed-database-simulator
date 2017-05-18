package manager;

import component.Client;
import component.Data;
import component.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dais on 2017-4-16.
 */
//in this porject i use just a certain number of clients,
//that means it will not delete any client from clientList
//if you want to delete some of clients,change the following structure "List" to "Map"
public class ClientManager {
    public static List<Client> clientList = new ArrayList<Client>();

    public static void add(Client client) {
        clientList.add(client);
    }

    public static void addAllClient(List<Client> clientList2){
        clientList.addAll(clientList2);
    }

    public static Client getClientWithClientId(int id) {
        return clientList.get(id-1);
    }

    public static void clientAddNode(){
        for(Client client:clientList){
            for(Node node:NodeManager.nodeList){
                client.addNode(node);
            }
        }
    }

    public static void clientAddDataMap(Map<Integer, Data> datamap){
        for(Client client:clientList){
            client.setDataMap(datamap);
        }
    }
}
