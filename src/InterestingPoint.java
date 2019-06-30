import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.*;

public class InterestingPoint implements Runnable {
    private NetworkClient client;
    private int playerNr;
    private KMeans kMeans;
    private Set<Bot> bots;
    public volatile boolean running;
    private GraphNode[] graphNodes;
    private boolean needsRefill;
    private List<float[]> gasStations;
    // Refill after 5 seconds without refill is good
    private final float NEWREFILL = 3000;
    public InterestingPoint(NetworkClient client, int playerNr) {
        this.client = client;
        this.playerNr = playerNr;
        bots = new HashSet<>();
        running = true;
        gasStations = new ArrayList<>();
        gasStations.add(new float[]{1,0,0});
        gasStations.add(new float[]{0,1,0});
        gasStations.add(new float[]{0,0,1});
        gasStations.add(new float[]{-1,0,0});
        gasStations.add(new float[]{0,-1,0});
        gasStations.add(new float[]{0,0,-1});

        kMeans = new KMeans(client.getGraph(), playerNr);
    }

    private boolean checkRefill() {

        for(Bot b:bots) {
            double delta = System.currentTimeMillis() - b.getLastRefill();
            if(delta < NEWREFILL) {
                System.out.println("Bot " + b.getBotID() + " successful refill: " + delta +  "ms ago.");
                needsRefill = false;
                return false;
            }
        }
        System.out.println("The bot needs a new refill.");
        needsRefill = true;
        return true;
    }

    public void registerBot(Bot b) {
        bots.add(b);
    }

    @Override
    public void run() {
        while(running) {
            try {
                checkRefill();
                kMeans.setGraph(client.getGraph());
                List<KMeans.Cluster> c = kMeans.refresh();
                // Go through the 3 vip points
                //System.out.println("New goal. Go, go, go!");
                try {
                    for(Bot b:bots) {
                        // The bot who can go over obstacles always goes to catch the energy
                        if(needsRefill && b.getBotID() == 1) {
                            b.setGoal(getNearestGasStation(b.getBotPosition()));
                            continue;
                        }
                        b.setGoal(c.get(b.getBotID()).randomNode());
                    }
                }catch (ConcurrentModificationException e) {

                }


                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private float[] getNearestGasStation(float[] start) {
        float dist = Float.POSITIVE_INFINITY;
        float[] goal = null;
        for(float[] station:gasStations) {
            float curDis = CustomMath.dist(start,station);
            if(curDis < dist) {
                goal = station;
                dist = curDis;
            }
        }
        return goal;
    }
}
