import com.sun.corba.se.impl.orbutil.graph.Graph;
import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;

import java.sql.Time;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterestingPoint implements Runnable {
    private NetworkClient client;
    private int playerNr;
    private KMeans kMeans;
    private Set<Bot> bots;
    public volatile boolean running;
    private GraphNode[] graphNodes;

    public InterestingPoint(NetworkClient client, int playerNr) {
        this.client = client;
        this.playerNr = playerNr;
        bots = new HashSet<>();
        running = true;
        kMeans = new KMeans(client.getGraph(), playerNr);
    }

    public void registerBot(Bot b) {
        bots.add(b);
    }

    @Override
    public void run() {
        while(running) {
            try {
                kMeans.setGraph(client.getGraph());
                List<KMeans.Cluster> c = kMeans.refresh();
                // Go through the 3 vip points
                System.out.println("New goal. Go, go, go!");
                for(Bot b:bots) {
                    b.setGoal(c.get(b.getBotID()).randomNode());
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
