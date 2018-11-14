package se.kth.ict.id2203.app.pfd;

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

		scenario.executeOn(topology);

		System.exit(0);
	}
}
