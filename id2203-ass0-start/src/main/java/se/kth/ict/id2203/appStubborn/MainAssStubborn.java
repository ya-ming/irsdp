package se.kth.ict.id2203.appStubborn;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.flp2p.FairLossLink;
import se.kth.ict.id2203.components.flp2p.FairLossLinkInit;
import se.kth.ict.id2203.components.sp2p.StubbornLink;
import se.kth.ict.id2203.components.sp2p.StubbornLinkInit;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.ports.sp2p.StubbornPointToPointLink;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class MainAssStubborn extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;
	private Topology topology = Topology.load(System.getProperty("topology"), selfId);

	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);
		commandScript = args[1];
		Kompics.createAndStart(MainAssStubborn.class);
	}

	public MainAssStubborn() {
		Address self = topology.getSelfAddress();

		Component timer = create(JavaTimer.class, Init.NONE);
		Component network = create(NettyNetwork.class, new NettyNetworkInit(self, 5));
		Component console = create(JavaConsole.class, Init.NONE);
		Component sp2p = create(StubbornLink.class, new StubbornLinkInit(topology, 0));
		Component flp2p = create(FairLossLink.class, new FairLossLinkInit(topology, 0));
		Component app = create(ApplicationAssStubborn.class, new ApplicationAssStubbornInit(self, topology.getAllAddresses(), commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, console.control());
		subscribe(handleFault, sp2p.control());
        subscribe(handleFault, flp2p.control());
		subscribe(handleFault, app.control());
//
		connect(app.required(Console.class), console.provided(Console.class));
		connect(app.required(StubbornPointToPointLink.class), sp2p.provided(StubbornPointToPointLink.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

        connect(sp2p.required(FairLossPointToPointLink.class), flp2p.provided(FairLossPointToPointLink.class));
		connect(sp2p.required(Timer.class), timer.provided(Timer.class));

        connect(flp2p.required(Timer.class), timer.provided(Timer.class));
        connect(flp2p.required(Network.class), network.provided(Network.class));
	}

	private Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
