package se.kth.ict.id2203.app.pfd;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.le.Le;
import se.kth.ict.id2203.components.le.LeInit;
import se.kth.ict.id2203.components.pfd.Pfd;
import se.kth.ict.id2203.components.pfd.PfdInit;
import se.kth.ict.id2203.components.pp2p.DelayLink;
import se.kth.ict.id2203.components.pp2p.DelayLinkInit;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

import java.util.Set;

public class Main extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;
	private Topology topology = Topology.load(System.getProperty("topology"), selfId);

	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);
		commandScript = args[1];
		Kompics.createAndStart(Main.class);
	}

	public Main() {
		Address self = topology.getSelfAddress();
		Set<Address> pi = topology.getAllAddresses();

		Component timer = create(JavaTimer.class, Init.NONE);
		Component network = create(NettyNetwork.class, new NettyNetworkInit(self, 5));
		Component con = create(JavaConsole.class, Init.NONE);
		Component pp2p = create(DelayLink.class, new DelayLinkInit(topology));
		Component pfd = create(Pfd.class, new PfdInit(self, pi, 2000, 500));
		Component app = create(Application.class, new ApplicationInit(self, pi, commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, pfd.control());
		subscribe(handleFault, app.control());

		connect(app.required(PerfectFailureDetector.class), pfd.provided(PerfectFailureDetector.class));
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

		connect(pfd.required(Timer.class), timer.provided(Timer.class));
		connect(pfd.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(pp2p.required(Timer.class), timer.provided(Timer.class));
		connect(pp2p.required(Network.class), network.provided(Network.class));
	}

	private Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
