import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.util.FastMath;

import java.util.*;

public class KMeans {
    private final int NUM_CLUSTERS = 256;
    private Cluster[] clusters;
    private GraphNode[] nodes;
    Random r;


    public KMeans(GraphNode[] nodes) {
        // random class
        this.nodes = nodes;
        r = new Random(System.currentTimeMillis());

        // initializing the cluster map
        clusters = new Cluster[NUM_CLUSTERS];

        // Generating random clusters
        for(int i = 0; i < NUM_CLUSTERS; i++) {
            float[] p = randomPosition();
            clusters[i] = new Cluster(p);
        }

        // Going through each node and assigning it to cluster with min distance
        for(int i = 0; i < nodes.length; i++) {
            Cluster current = null;
            float dist = Float.POSITIVE_INFINITY;
            // Go through each cluster to check the distance
            for(int j = 0; j < clusters.length; j++) {
                // if the current is smaller than the previous distance, set new node
                float curDist = clusters[j].dist(nodes[i]);
                if(curDist < dist) {
                    current = clusters[j];
                    dist = curDist;
                }
            }
            if(current != null)
                current.addGraphNode(i);
        }
        int total = 0;
        for(Cluster c:clusters) {
            System.out.println(c.capacity());
            total+=c.capacity();
        }
        System.out.println("Capacity: " + total);
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
        private Set<Integer> nodeIDList;
        private float[] position;
        private float score;
        public Cluster(float[] position) {
            nodeIDList = new HashSet<>();
            this.position = position;
        }

        public void addGraphNode(int i) {
            nodeIDList.add(i);
        }


        public float dist(GraphNode a) {
            return CustomMath.dist(position, a);
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
