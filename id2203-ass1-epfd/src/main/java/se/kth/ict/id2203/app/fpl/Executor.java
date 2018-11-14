package se.kth.ict.id2203.app.fpl;

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
				defaultLinks(500, 0);
			}
		};

		Scenario scenario = new Scenario(Main.class) {
			{
				command(1, "S10:P1:P2:P3");
				command(2, "S10");
				command(3, "S10");
			}
		};

//		Scenario scenario = new Scenario(Main.class) {
//			{
//				command(1, "S10:P1:P2:P3");
//				command(2, "S10:PA:PB:PC");
//				command(3, "S10:PGG:PHH:PJJ");
//			}
//		};

		scenario.executeOn(topology);

		System.exit(0);
	}
}
