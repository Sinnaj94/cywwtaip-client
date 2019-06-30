import lenz.htw.cywwtaip.world.GraphNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

/**
 * Custom Math class for basic math operations
 */
public class CustomMath {
    /**
     * Get the euclidian distance between point a and b
     * @param a point a as float[]
     * @param b point b as float[]
     * @return the euclidian distance between point a and b
     */
    public static float dist(float[] a, float[] b) {
        return (float) FastMath.sqrt(FastMath.pow(a[0] - b[0], 2) + FastMath.pow(a[1] - b[1], 2) + FastMath.pow(a[2] - b[2], 2));
    }

    /**
     * Get the euclidian distance between point a and b
     * @param a point a as GraphNode
     * @param b point b as GraphNode
     * @return the euclidian distance between point a and b
     */
    public static float dist(GraphNode a, GraphNode b) {
        return (float) FastMath.sqrt(FastMath.pow(a.x - b.x, 2) + FastMath.pow(a.x - b.x, 2) + FastMath.pow(a.x - b.x, 2));
    }

    /**
     * Get the euclidian distance between point a and b
     * @param a point a as float[]
     * @param b point b as GraphNode
     * @return the euclidian distance between point a and b
     */
    public static float dist(float[] a, GraphNode b) {
        return (float) FastMath.sqrt(FastMath.pow(a[0] - b.x, 2) + FastMath.pow(a[1] - b.y, 2) + FastMath.pow(a[2] - b.z, 2));
    }

    /**
     * Fast distance (squared distance with) !ONLY FOR COMPARISION!
     * @param a point a as float[]
     * @param b point b as GraphNode
     * @return the squared distance between point a and b
     */
    public static float fastDist(float[] a, GraphNode b) {
        return (float)(FastMath.pow(a[0] - b.x, 2) + FastMath.pow(a[1] - b.y, 2) + FastMath.pow(a[2] - b.z, 2));
    }

    /**
     * Convert float vector to Vector3D
     * @param vec given float vector
     * @return Converted float vector to Vector3D
     */
    public static Vector3D convert(float[] vec) {
        return new Vector3D(vec[0], vec[1], vec[2]);
    }

}
