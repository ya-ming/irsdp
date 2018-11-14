package se.kth.ict.id2203.pa.broadcast;

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
				link(1, 2, 2000, 0).bidirectional();
				link(1, 3, 6000, 0).bidirectional();
				link(2, 3, 3000, 0).bidirectional();
			}
		};

		Scenario scenario = new Scenario(Main.class) {
			{
//				command(1, "S10:B1:S10:B2:S10:B3:S30000:X");
//                command(1, "S10:B1:S30000:X");
//				command(1, "S10:R1:S10:R2:S10:R3:S30000:X");
//				command(1, "S10:R1:S30000:X");

//				command(1, "S10:L1:S10:L2:S10:L3:S30000:X");

				// Testing for AllAck Uniform Reliable Broadcast
				// Basic Testing
//				command(1, "S10:A1");
//				command(1, "S10:A1:S10:A2:S10:A3:S30000:X");
				// Complex Testing, including Crash
//				command(1, "S10:A1:S10:A2:S10:A3:S20000:X");
//				command(2, "S10:A4:S20000:A5:S20000:A6");

				// Testing for Majority ack logged uniform reliable broadcast
				command(1, "S10:M1");
//				command(1, "S10:M1:S10:M2:S10:M3:S30000");
//				command(2, "S10:M4:S20000:M5:S20000:M6");

				// Testing for FIFO reliable broadcast
//				command(1, "S10:F1:S10:F2:S10:F3");
//				command(2, "S10:F4:S10:F5:S10:F6");

				// Testing for no-waiting causal order reliable broadcast
//				command(1, "S10:N1:S10:N2:S10:N3");
//				command(2, "S10:N4:S10:N5:S10:N6");

//				command(1, "S10:C1:S10:C2:S10:C3:S30000:X");
//                command(1, "S10:C1:S30000:X");
				command(2, "S10");
				command(3, "S10");
			}
		};

		scenario.executeOn(topology);
		System.exit(0);
	}
}
