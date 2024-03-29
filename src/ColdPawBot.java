import jdk.internal.cmm.SystemResourcePressureImpl;
import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class ColdPawBot {
    public static final CyclicBarrier barrier = new CyclicBarrier(3);
    public static HashMap<Integer, Integer> graphNodeIntegerHashMap;
    public static void main(String[] args) {
        NetworkClient client = new NetworkClient(null, "Die kalte Pfote", "Die kalte Pfote hat wieder zugeschlagen!");

        // Ziel des Spiels ist es, möglichst große Bereiche der Spielwelt durch "Überfahren" mit eigenen Bots mit der eigenen Farbe zu färben.
        // Die kugelförmige Spielwelt wird von Gräben durchzogen, die normalerweise die Bewegung begrenzen.

        GraphNode[] nodes = client.getGraph();
        graphNodeIntegerHashMap = new HashMap<>();
        for(int i = 0; i < nodes.length; i++) {
            graphNodeIntegerHashMap.put(nodes[i].hashCode(), i);
        }

        /*
        Jeder Spieler hat Kontrolle über drei Bots (nummeriert 0, 1, 2).
        Bot 0 (einfarbig) hat Geschwindigkeit 100%
        Bot 1 (gepunktet) hat Geschwindigkeit 67% und kann sich über Gräben bewegen
        Bot 2 (gestreift) hat Geschwindigkeit 42% und färbt auch seine unmittelbare Nachbarschaft
        Können nicht gebremst werden!
        if(x, y, oder z > .94) -> Energie wird geladen
         */

        // Create interesting-point algorithm (it is only needed once, it is shared by multiple bots)
        InterestingPoint p = new InterestingPoint(client);
        new Thread(p).start();

        // Create the bots
        for(int i = 0; i < 3; i++) {
            // Building three different converted maps
            Bot c = new Bot(client, i);
            // Register the bot to interesting point
            p.registerBot(c);
            new Thread(c).start();
        }
    }
}
