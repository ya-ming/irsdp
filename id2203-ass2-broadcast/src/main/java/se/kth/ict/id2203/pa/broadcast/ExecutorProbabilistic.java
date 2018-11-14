package se.kth.ict.id2203.pa.broadcast;

import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class ExecutorProbabilistic {

    public static final void main(String[] args) {
        Topology topology = new Topology() {
            {
                node(1, "127.0.0.1", 22031);
                node(2, "127.0.0.1", 22032);
                node(3, "127.0.0.1", 22033);
                node(4, "127.0.0.1", 22034);
                node(5, "127.0.0.1", 22035);
                node(6, "127.0.0.1", 22036);
                defaultLinks(1000, 0);
            }
        };

        Scenario scenario = new Scenario(Main.class) {
            {
                // Testing for Eager probabilistic broadcast
                command(1, "S10:E1");

//				command(1, "S10:C1:S10:C2:S10:C3:S30000:X");
//                command(1, "S10:C1:S30000:X");
                command(2, "S10");
                command(3, "S10");
                command(4, "S10");
                command(5, "S10");
                command(6, "S10");
            }
        };

        scenario.executeOn(topology);
        System.exit(0);
    }
}

