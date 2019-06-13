import lenz.htw.cywwtaip.net.NetworkClient;
import lenz.htw.cywwtaip.world.GraphNode;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        NetworkClient client = new NetworkClient(null, "ICH", "SUPER!");

        while (client.isAlive()) {
            client.getBotSpeed(0); // raw constant
            client.getScore(client.getMyPlayerNumber());
            client.changeMoveDirection(1, -0.08f);

            float[] position = client.getBotPosition(0, 0); // array with x,y,z
            float[] direction = client.getBotDirection(0); // array with x,y,z

            GraphNode[] graph = client.getGraph();

            for (GraphNode n : graph[0].neighbors) {
                System.out.println(n + ": " + n.owner + ", " + n.blocked);
            }
        }
    }
}
