package se.kth.ict.id2203.pa.riwmonar;

import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class Executor {
	public static final void main(String[] args) {
		Topology topology = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
				node(3, "127.0.0.1", 22033);

				defaultLinks(1000, 0);
			}
		};

		Scenario scenario = new Scenario(Main.class) {
			{
				command(1, "S10:W1:S1000:R:S10:WA:S10:R");
				command(2, "S1000");
				command(3, "S1000");
			}
		};

		// Non-overlapping (sequential) operations.
//		Scenario scenario = new Scenario(Main.class) {
//			{
//				command(1, "S10");
//				command(2, "S500:W4");
//				command(3, "S10000:R");
//			}
//		};

		// Concurrent operations.
//		Scenario scenario = new Scenario(Main.class) {
//			{
//				command(1, "W1:R:S1000:W12:R");
//				command(2, "W2:R:S3000:R");
//				command(3, "S5500:W3:R:S3000:R");
//			}
//		};

		// One process crashes and the other processes keep running.
//		Scenario scenario = new Scenario(Main.class) {
//			{
//				command(1, "X");
//				command(2, "W2");
//				command(3, "S5000:R");
//			}
//		};

		scenario.executeOn(topology);

		System.exit(0);
	}
}
