import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        NetworkClient client = new NetworkClient(null, "Davidoff", "Davidoff cool water!");
        // Ziel des Spiels ist es, möglichst große Bereiche der Spielwelt durch "Überfahren" mit eigenen Bots mit der eigenen Farbe zu färben.
        // Die kugelförmige Spielwelt wird von Gräben durchzogen, die normalerweise die Bewegung begrenzen.
        Map<Integer, Node> converted = convertGraph(client.getGraph());
        // Create the bots
        for(int i = 0; i < 3; i++) {
            Bot c = new Bot(client, i, converted);
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
            float[] pos = new float[]{n.x, n.y, n.z};
            Node added = new Node(pos, n.hashCode());
            nodeList.put(n.hashCode(), added);
        }

        // Adding neighbours after list is built
        for(int i = 0; i < nodes.length; i++) {
            // Go through neighbours
            for(int j = 0; j < nodes[i].neighbors.length; j++) {
                // Get the Main Node via hash and add Neighbour via hash
                nodeList.get(nodes[i].hashCode()).addNeighbour(nodeList.get(nodes[j].hashCode()));
            }
        }
        long dt = System.currentTimeMillis() - t;
        System.out.println(String.format("Conversion took %d milliseconds.", dt));
        System.out.println(nodeList.values().toArray()[0].toString());
        return nodeList;

    }
}
