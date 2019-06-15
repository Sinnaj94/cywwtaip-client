import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        NetworkClient client = new NetworkClient(null, "Davidoff", "Davidoff cool water!");

        // Ziel des Spiels ist es, möglichst große Bereiche der Spielwelt durch "Überfahren" mit eigenen Bots mit der eigenen Farbe zu färben.
        // Die kugelförmige Spielwelt wird von Gräben durchzogen, die normalerweise die Bewegung begrenzen.
        convertGraph(client.getGraph());
        // Create the bots
        for(int i = 0; i < 3; i++) {
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
    private static List<Node> convertGraph(GraphNode[] nodes) {
        List<Node> nodeList = new ArrayList<>();
        for(int i = 0; i < nodes.length; i++) {
            float[] pos = new float[]{nodes[i].x, nodes[i].y, nodes[i].z};
            Node n = new Node(pos);
            nodeList.add(n);
        }
        // Adding neighbours
        for(int i = 0; i < nodes.length; i++) {
            // Go through neighbours
            for(int j = 0; j < nodes[i].neighbors.length; j++) {
                // TODO
            }
        }
        return nodeList;

    }
}
