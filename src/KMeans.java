import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.util.FastMath;
import sun.nio.ch.Net;

import java.lang.reflect.Array;
import java.util.*;

public class KMeans {
    public static void main(String[] args) {
        NetworkClient client = new NetworkClient(null, "KMeans", "Test");
        KMeans k = new KMeans(client.getGraph(), client.getMyPlayerNumber());
    }

    private final int NUM_CLUSTERS = 10;
    private final int SEED = 0;
    private Cluster[] clusters;
    private GraphNode[] nodes;
    private int playerNR;
    Random r;


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

    public void setGraph(GraphNode[] graph) {
        nodes = graph;
    }

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

    public List<Cluster> getInterestingPoint() {
        List<Cluster> ret =  Arrays.asList(clusters);
        ret.sort(Comparator.comparingDouble(Cluster::getScore));
        Collections.reverse(ret);
        return ret;
    }

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

    class Cluster {
        private List<Integer> nodeIDList;

        public float[] getPosition() {
            return position;
        }

        public int randomNode() {
            GraphNode c;
            int i;
            do {
                i = nodeIDList.get(r.nextInt(nodeIDList.size()));
                c = nodes[i];
            }while (c.blocked);
            return i;
        }

        public void resetScore() {
            score = 0;
        }

        private float[] position;

        public double getScore() {
            return score;
        }

        private double score;
        public Cluster(float[] position) {
            nodeIDList = new ArrayList<>();
            this.position = position;
        }

        public void addGraphNode(int i) {
            nodeIDList.add(i);
            // Scoring
            // TODO: automated scoring
            /*if(nodes[i].blocked) {
                score -= .5f;
            }*/
            // We want to eliminate other players
            if(nodes[i].owner != 0) {
                if(nodes[i].owner != playerNR + 1) {
                    score+=1;
                } else if(nodes[i].owner == playerNR + 1) {
                    score-=1;
                }
            }
        }


        public float dist(int i) {
            return CustomMath.fastDist(position, nodes[i]);
        }

        public int capacity() {
            return nodeIDList.size();
        }

        public float[] updateMedian() {
            double[] total = new double[3];
            for(int i:nodeIDList) {
                total[0] += nodes[i].x;
                total[1] += nodes[i].y;
                total[2] += nodes[i].z;
            }

            // median value
            float[] median = new float[3];
            for(int i = 0; i < total.length; i++) {
                median[i] = (float)(total[i] / nodeIDList.size());
            }
            this.position = median;
            return median;
        }
    }
}
