package se.kth.ict.id2203.app.ble;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.components.ble.Ble;
import se.kth.ict.id2203.components.ble.BleInit;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.pp2p.DelayLink;
import se.kth.ict.id2203.components.pp2p.DelayLinkInit;
import se.kth.ict.id2203.ports.ble.BallotLeaderElection;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkControl;
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

	static Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);

		FileAppender appender = (FileAppender) log.getRootLogger().getAppender("F");
		appender.setFile("logs/" + selfId + ".txt");
		appender.activateOptions();

		commandScript = args[1];
		Kompics.createAndStart(Main.class);
	}

	public Main() {
		Address self = topology.getSelfAddress();
		Set<Address> pi = topology.getAllAddresses();

//		System.out.println("###################" + pi.toString());

		Component time = create(JavaTimer.class, Init.NONE);
		Component network = create(NettyNetwork.class, new NettyNetworkInit(self, 5));
		Component con = create(JavaConsole.class, Init.NONE);
		Component pp2p = create(DelayLink.class, new DelayLinkInit(topology));
		Component ble = create(Ble.class, new BleInit(self, pi, 2000, 500));
		Component app = create(Application.class, new ApplicationInit(self, pi, commandScript));

		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, pp2p.control());
        subscribe(handleFault, ble.control());
		subscribe(handleFault, app.control());

		connect(app.required(BallotLeaderElection.class), ble.provided(BallotLeaderElection.class));
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(Timer.class), time.provided(Timer.class));

        connect(ble.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(ble.required(Timer.class), time.provided(Timer.class));

		connect(pp2p.required(Timer.class), time.provided(Timer.class));
		connect(pp2p.required(Network.class), network.provided(Network.class));
//		connect(pp2p.required(NetworkControl.class), network.provided(NetworkControl.class));
	}

	private Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
