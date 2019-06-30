import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.*;

/**
 * InterestingPoint gets interesting points via KMeans and sends the bots to different points
 * it also checks, if the bots need to be reloaded and sends the 2nd bot to an energy field, if it is necessary
 */
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

    /**
     * InterestingPoint constructor
     * @param client The Network Client
     */
    public InterestingPoint(NetworkClient client) {
        this.client = client;
        this.playerNr = client.getMyPlayerNumber();
        bots = new HashSet<>();
        running = true;

        // Add the "outer points" of the sphere as gas stations
        gasStations = new ArrayList<>();
        gasStations.add(new float[]{1,0,0});
        gasStations.add(new float[]{0,1,0});
        gasStations.add(new float[]{0,0,1});
        gasStations.add(new float[]{-1,0,0});
        gasStations.add(new float[]{0,-1,0});
        gasStations.add(new float[]{0,0,-1});

        // New kMeans algorithm for checking interesting areas
        kMeans = new KMeans(client.getGraph(), playerNr);
    }

    /**
     * Checks if the player needs a refill
     * @return if the player needs a refill
     */
    private boolean checkRefill() {
        // Iterate through all registered bots of the player
        for(Bot b:bots) {
            // Get the time of the last refill
            double delta = System.currentTimeMillis() - b.getLastRefill();
            if(delta < NEWREFILL) {
                // If any bot has refilled, everything is cool
                System.out.println("Bot " + b.getBotID() + " successful refill: " + delta +  "ms ago.");
                needsRefill = false;
                return false;
            }
        }
        // if no bot has refilled, the player needs a refill
        System.out.println("The bot needs a new refill.");
        needsRefill = true;
        return true;
    }

    /**
     * Register a bot to the list
     * @param b Added bot
     */
    public void registerBot(Bot b) {
        bots.add(b);
    }

    /**
     * Run method for threading
     */
    @Override
    public void run() {
        while(running) {
            try {
                checkRefill();

                // update the graph of kMeans and refresh
                kMeans.setGraph(client.getGraph());
                List<KMeans.Cluster> c = kMeans.refresh();

                // Go through the 3 vip points
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
                    continue;
                }

                // Do that operations every 3 seconds
                Thread.sleep(3000);
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
