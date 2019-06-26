import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;

public class Main {
    public final static  Object sync = new Object();
    public static void main(String[] args) {
        NetworkClient client = new NetworkClient(null, "Davidoff", "Davidoff cool water!");
        // Ziel des Spiels ist es, möglichst große Bereiche der Spielwelt durch "Überfahren" mit eigenen Bots mit der eigenen Farbe zu färben.
        // Die kugelförmige Spielwelt wird von Gräben durchzogen, die normalerweise die Bewegung begrenzen.
        // Create the bots
        //Map<Integer, Node> converted = convertGraph(client.getGraph());
        // TODO: dijkstra for everyone.
        for(int i = 0; i < 1; i++) {
            // Building three different converted maps
            Bot c = new Bot(client, i);
            new Thread(c).start();
        }
        /*
        Jeder Spieler hat Kontrolle über drei Bots (nummeriert 0, 1, 2).
        Bot 0 (einfarbig) hat Geschwindigkeit 100%
        Bot 1 (gepunktet) hat Geschwindigkeit 67% und kann sich über Gräben bewegen
        Bot 2 (gestreift) hat Geschwindigkeit 42% und färbt auch seine unmittelbare Nachbarschaft
        Können nicht gebremst werden!
        if(x, y, oder z > .94) -> Energie wird geladen
         */
        // Size of the array: 40962!
    }

    /**
     * Convert graph to List of Nodes
     * @param nodes
     * @return
     */
    private static Map<Integer, Node> convertGraph(GraphNode[] nodes) {
        System.out.println(String.format("Converting %d nodes.", nodes.length));
        long t = System.currentTimeMillis();
        Map<Integer, Node> nodeList = new HashMap<>();
        // Build List
        for(int i = 0; i < nodes.length; i++) {
            GraphNode n = nodes[i];
            Vector3D pos = new Vector3D(n.x, n.y, n.z);
            Node added = new Node(pos, n.hashCode(), n.blocked);
            nodeList.put(n.hashCode(), added);
        }

        // Adding neighbours after list is built
        for(int i = 0; i < nodes.length; i++) {
            // Go through neighbours
            Node current = nodeList.get(nodes[i].hashCode());
            for(int j = 0; j < nodes[i].neighbors.length; j++) {
                // Get the Main Node via hash and add Neighbour via hash
                current.addNeighbour(nodeList.get(nodes[j].hashCode()));
            }
        }
        // Abfrage von Node über hash: nodeList.get(HASHVALUE)
        long dt = System.currentTimeMillis() - t;
        System.out.println(String.format("Conversion took %d milliseconds.", dt));
        return nodeList;
    }

    private static Node[] graphToNode(GraphNode[] nodes) {
        System.out.println(String.format("Converting %d nodes to array.", nodes.length));
        Node[] ret = new Node[nodes.length];

        // convert graph to custom nodes
        for(int i = 0; i < nodes.length; i++) {
            GraphNode n = nodes[i];
            Vector3D pos = new Vector3D(n.x, n.y, n.z);
            Node add = new Node(pos, n.hashCode(), n.blocked);
            ret[i] = add;
        }

        for(int i = 0; i < nodes.length; i++) {
            Node current = ret[i];
            for(int j = 0; j < nodes[i].neighbors.length; j++) {
                //ret[i].addNeighbour(null);
                // find right node

            }
        }
        return ret;
    }
}
