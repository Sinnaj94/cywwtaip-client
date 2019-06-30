import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.util.FastMath;

public class CustomMath {
    public static float dist(float[] a, float[] b) {
        return (float) FastMath.sqrt(FastMath.pow(a[0] - b[0], 2) + FastMath.pow(a[1] - b[1], 2) + FastMath.pow(a[2] - b[2], 2));
    }

    public static float dist(GraphNode a, GraphNode b) {
        return (float) FastMath.sqrt(FastMath.pow(a.x - b.x, 2) + FastMath.pow(a.x - b.x, 2) + FastMath.pow(a.x - b.x, 2));
    }

    public static float dist(float[] a, GraphNode b) {
        return (float) FastMath.sqrt(FastMath.pow(a[0] - b.x, 2) + FastMath.pow(a[1] - b.y, 2) + FastMath.pow(a[2] - b.z, 2));
    }

    public static float fastDist(float[] a, GraphNode b) {
        return (float)(FastMath.pow(a[0] - b.x, 2) + FastMath.pow(a[1] - b.y, 2) + FastMath.pow(a[2] - b.z, 2));
    }
}
