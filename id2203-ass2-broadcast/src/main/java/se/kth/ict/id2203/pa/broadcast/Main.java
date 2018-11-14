package se.kth.ict.id2203.pa.broadcast;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.components.aurb.AllAckURb;
import se.kth.ict.id2203.components.aurb.AllAckURbInit;
import se.kth.ict.id2203.components.epb.EagerPb;
import se.kth.ict.id2203.components.epb.EagerPbInit;
import se.kth.ict.id2203.components.flp2p.FairLossLink;
import se.kth.ict.id2203.components.flp2p.FairLossLinkInit;
import se.kth.ict.id2203.components.frb.FIFORb;
import se.kth.ict.id2203.components.frb.FIFORbInit;
import se.kth.ict.id2203.components.lrb.LazyRb;
import se.kth.ict.id2203.components.lrb.LazyRbInit;
import se.kth.ict.id2203.components.lurb.MajorityAckLURb;
import se.kth.ict.id2203.components.lurb.MajorityAckLURbInit;
import se.kth.ict.id2203.components.nwcrb.NoWaitingCrb;
import se.kth.ict.id2203.components.nwcrb.NoWaitingCrbInit;
import se.kth.ict.id2203.components.pfd.Pfd;
import se.kth.ict.id2203.components.pfd.PfdInit;
import se.kth.ict.id2203.ports.aurb.AllAckUniformReliableBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.crb.CausalOrderReliableBroadcast;
import se.kth.ict.id2203.components.pp2p.DelayLink;
import se.kth.ict.id2203.components.pp2p.DelayLinkInit;
import se.kth.ict.id2203.components.erb.EagerRb;
import se.kth.ict.id2203.components.erb.EagerRbInit;
import se.kth.ict.id2203.ports.epb.EagerProbabilisticBroadcast;
import se.kth.ict.id2203.ports.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.ports.frb.FIFOReliableBroadcast;
import se.kth.ict.id2203.ports.lrb.LazyReliableBroadcast;
import se.kth.ict.id2203.ports.lurb.MajorityAckLoggedUniformReliableBroadcast;
import se.kth.ict.id2203.ports.nwcrb.NoWaitingCausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
import se.kth.ict.id2203.components.beb.BasicBroadcast;
import se.kth.ict.id2203.components.beb.BasicBroadcastInit;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.crb.WaitingCrb;
import se.kth.ict.id2203.components.crb.WaitingCrbInit;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Fault;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class Main extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

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
        logger.debug("Main, Main");

		Address self = topology.getSelfAddress();
		Set<Address> pi = topology.getAllAddresses();

		Component timer = create(JavaTimer.class, Init.NONE);
		Component network = create(NettyNetwork.class, new NettyNetworkInit(self, 5, 0));
		Component console = create(JavaConsole.class, Init.NONE);
		Component pp2p = create(DelayLink.class, new DelayLinkInit(topology, 1000));
		Component beb = create(BasicBroadcast.class, new BasicBroadcastInit(self, pi));
		Component flp2p = create(FairLossLink.class, new FairLossLinkInit(topology, 0));

		Component erb = create(EagerRb.class, new EagerRbInit(self, pi));

        Component pfd = create(Pfd.class, new PfdInit(self, pi, 20000, 500));
		Component lrb = create(LazyRb.class, new LazyRbInit(self, pi));

		Component allackurb = create(AllAckURb.class, new AllAckURbInit(self, pi));

		Component lurb = create(MajorityAckLURb.class, new MajorityAckLURbInit(self, pi));

		Component epb = create(EagerPb.class, new EagerPbInit(self, pi));

		Component crb = create(WaitingCrb.class, new WaitingCrbInit(self, pi));

		Component nwcrb = create(NoWaitingCrb.class, new NoWaitingCrbInit(self, pi));

		Component frb = create(FIFORb.class, new FIFORbInit(self, pi));

		Component app = create(Application.class, new ApplicationInit(self, pi, commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, console.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, beb.control());
		subscribe(handleFault, flp2p.control());

		subscribe(handleFault, erb.control());
		subscribe(handleFault, lrb.control());
        subscribe(handleFault, pfd.control());

        subscribe(handleFault, allackurb.control());
		subscribe(handleFault, lurb.control());
		subscribe(handleFault, epb.control());

		subscribe(handleFault, frb.control());

		subscribe(handleFault, crb.control());
		subscribe(handleFault, nwcrb.control());


		subscribe(handleFault, app.control());

		logger.debug("Main, subscribe");

		connect(app.required(CausalOrderReliableBroadcast.class), crb.provided(CausalOrderReliableBroadcast.class));
		connect(app.required(NoWaitingCausalOrderReliableBroadcast.class), nwcrb.provided(NoWaitingCausalOrderReliableBroadcast.class));
		connect(app.required(EagerReliableBroadcast.class), erb.provided(EagerReliableBroadcast.class));
		connect(app.required(LazyReliableBroadcast.class), lrb.provided(LazyReliableBroadcast.class));
        connect(app.required(AllAckUniformReliableBroadcast.class), allackurb.provided(AllAckUniformReliableBroadcast.class));
		connect(app.required(MajorityAckLoggedUniformReliableBroadcast.class), lurb.provided(MajorityAckLoggedUniformReliableBroadcast.class));
		connect(app.required(EagerProbabilisticBroadcast.class), epb.provided(EagerProbabilisticBroadcast.class));
		connect(app.required(FIFOReliableBroadcast.class), frb.provided(FIFOReliableBroadcast.class));

		connect(app.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));
		connect(app.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(Console.class), console.provided(Console.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

		connect(crb.required(EagerReliableBroadcast.class), erb.provided(EagerReliableBroadcast.class));
		connect(nwcrb.required(EagerReliableBroadcast.class), erb.provided(EagerReliableBroadcast.class));

		connect(erb.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));

        connect(pfd.required(Timer.class), timer.provided(Timer.class));
        connect(pfd.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(lrb.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));
        connect(lrb.required(PerfectFailureDetector.class), pfd.provided(PerfectFailureDetector.class));

        connect(allackurb.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));
        connect(allackurb.required(PerfectFailureDetector.class), pfd.provided(PerfectFailureDetector.class));

		connect(lurb.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));

		connect(epb.required(FairLossPointToPointLink.class), flp2p.provided(FairLossPointToPointLink.class));

		connect(frb.required(EagerReliableBroadcast.class), erb.provided(EagerReliableBroadcast.class));

		connect(beb.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(pp2p.required(Timer.class), timer.provided(Timer.class));
		connect(pp2p.required(Network.class), network.provided(Network.class));

		connect(flp2p.required(Timer.class), timer.provided(Timer.class));
		connect(flp2p.required(Network.class), network.provided(Network.class));

		logger.debug("Main, connect");
	}

	private Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
