package se.kth.ict.id2203.app.reconfigurable.rble;

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
				node(4, "127.0.0.1", 22034);
				defaultLinks(800, 0);
			}
		};

		Scenario scenario = new Scenario(Main.class) {
			{
				command(1, "S10");
				command(2, "S10");
				command(3, "S10");
			}
		};

		Scenario scenario2 = new Scenario(Main.class) {
			{
				command(1, "S10");
				command(2, "S10");
				command(3, "S10");
			}
		};

		// using command new@pid@command in the Executor to create new process
		// using command C1@1@2@3 to reconfigure the topology
		// C: config
		// 1: config id
		// @1@2@3: process ids being configured in this config
		scenario.executeOn(topology);

		System.exit(0);
	}
}
