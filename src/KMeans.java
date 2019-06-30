import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.util.FastMath;
import sun.nio.ch.Net;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Class for getting the most interesting areas
 */
public class KMeans {
    // The number of clusters
    private final int NUM_CLUSTERS = 32;
    private final int SEED = 0;
    private Cluster[] clusters;
    private GraphNode[] nodes;
    private int playerNR;
    Random r;


    /**
     * KMeans constructor
     * @param nodes The given Graph
     * @param playerNR The player Number
     */
    public KMeans(GraphNode[] nodes, int playerNR) {
        // random class
        double t = System.currentTimeMillis();
        this.nodes = nodes;
        this.playerNR = playerNR;
        r = new Random(0);

        // initializing the cluster map
        clusters = new Cluster[NUM_CLUSTERS];

        // Generating random clusters
        for(int i = 0; i < NUM_CLUSTERS; i++) {
            float[] p = randomPosition();
            clusters[i] = new Cluster(p);
        }
    }

    /**
     * Update the graph
     * @param graph The given Graph
     */
    public void setGraph(GraphNode[] graph) {
        nodes = graph;
    }

    /**
     * Execute the kMeans algorithm
     * @return The Clusters as a list
     */
    public List<Cluster> refresh() {
        // Reset all cluster scores
        for(int i = 0; i < clusters.length; i++) {
            clusters[i].resetScore();
        }

        // Going through each node and assigning it to cluster with min distance
        for(int i = 0; i < nodes.length; i++) {
            Cluster current = null;
            float dist = Float.POSITIVE_INFINITY;
            // Go through each cluster to check the distance
            for(int j = 0; j < clusters.length; j++) {
                // if the current is smaller than the previous distance, set new node
                float curDist = clusters[j].dist(i);
                if(curDist < dist) {
                    current = clusters[j];
                    dist = curDist;
                }
            }

            if(current != null)
                current.addGraphNode(i);
        }

        for(Cluster c:clusters) {
            c.updateMedian();
        }

        return getInterestingPoint();
    }

    /**
     * Get the points ordered descending by score
     * @return the points ordered descending by score
     */
    public List<Cluster> getInterestingPoint() {
        List<Cluster> ret =  Arrays.asList(clusters);
        ret.sort(Comparator.comparingDouble(Cluster::getScore));
        Collections.reverse(ret);
        return ret;
    }

    /**
     * Get a random position on a sphere
     * @return random position on a sphere
     */
    private float[] randomPosition() {
        float u = r.nextFloat();
        float v = r.nextFloat();
        float theta = (float)(2 * FastMath.PI * u);
        float phi = (float)FastMath.acos(2 * v - 1);
        float x = (float)(FastMath.sin(phi) * FastMath.cos(theta));
        float y = (float)(FastMath.sin(phi) * FastMath.sin(theta));
        float z = (float) FastMath.cos(phi);
        return new float[]{x,y,z};
    }

    /**
     * Class for a cluster
     */
    class Cluster {
        private List<Integer> nodeIDList;
        private float[] position;
        private double score;

        /**
         * Get a random node
         * @return random node, that is not blocked
         */
        public int randomNode() {
            GraphNode c;
            int i;
            do {
                i = nodeIDList.get(r.nextInt(nodeIDList.size()));
                c = nodes[i];
            }while (c.blocked);
            return i;
        }

        /**
         * Reset the score
         */
        public void resetScore() {
            score = 0;
        }

        /**
         * Get the calculated score
         * @return the calculated score
         */
        public double getScore() {
            return score;
        }

        /**
         * Constructor for cluster
         * @param position initial position
         */
        public Cluster(float[] position) {
            nodeIDList = new ArrayList<>();
            this.position = position;
        }

        /**
         * Add a graph node by index and update score
         * @param i index of node
         */
        public void addGraphNode(int i) {
            nodeIDList.add(i);
            // Scoring
            if(nodes[i].owner != 0) {
                // We want to eliminate other players, but not ourself
                if(nodes[i].owner != playerNR + 1) {
                    score+=1;
                } else if(nodes[i].owner == playerNR + 1) {
                    score-=1;
                }
            }
        }

        /**
         * Get the distance to other Node by index
         * @param i node by index
         * @return the distance to other Node by index
         */
        public float dist(int i) {
            return CustomMath.fastDist(position, nodes[i]);
        }

        public void setPosition(float[] position) {
            this.position = position;
        }

        /**
         * Update the median of the current point
         * @return the median of the current point
         */
        public float[] updateMedian() {
            double[] total = new double[3];

            // go through each node and add the score
            for(int i:nodeIDList) {
                total[0] += nodes[i].x;
                total[1] += nodes[i].y;
                total[2] += nodes[i].z;
            }

            // median value
            float[] median = new float[3];
            for(int i = 0; i < total.length; i++) {
                // median value is all values / the size
                median[i] = (float)(total[i] / nodeIDList.size());
            }

            setPosition(median);

            return median;
        }
    }
}
