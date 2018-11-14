/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203.app.sc;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.components.ac.Paxos;
import se.kth.ict.id2203.components.ac.PaxosInit;
import se.kth.ict.id2203.components.beb.BasicBroadcast;
import se.kth.ict.id2203.components.beb.BasicBroadcastInit;
import se.kth.ict.id2203.components.ble.Ble;
import se.kth.ict.id2203.components.ble.BleInit;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.fpl.FPp2pLink;
import se.kth.ict.id2203.components.fpl.FPp2pLinkInit;
import se.kth.ict.id2203.components.pp2p.DelayLink;
import se.kth.ict.id2203.components.pp2p.DelayLinkInit;
import se.kth.ict.id2203.components.sc.MultiPaxos;
import se.kth.ict.id2203.components.sc.MultiPaxosInit;
import se.kth.ict.id2203.ports.ac.AbortableConsensus;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.ble.BallotLeaderElection;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.sc.SequenceConsensus;
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

		FileAppender appender = (FileAppender) Logger.getRootLogger().getAppender("F");
		appender.setFile("logs/" + selfId + ".txt");
		appender.activateOptions();

		commandScript = args[1];
		Kompics.createAndStart(Main.class);
	}

	public Main() {
		Address self = topology.getSelfAddress();
		Set<Address> pi = topology.getAllAddresses();

		Component timer = create(JavaTimer.class, Init.NONE);
		Component network = create(NettyNetwork.class, new NettyNetworkInit(topology.getSelfAddress(), 5));
		Component console = create(JavaConsole.class, Init.NONE);
		Component pp2p = create(DelayLink.class, new DelayLinkInit(topology));
		Component fpl = create(FPp2pLink.class, new FPp2pLinkInit(topology));
		Component sc = create(MultiPaxos.class, new MultiPaxosInit(self, pi));
		Component ble = create(Ble.class, new BleInit(self, pi, 2000, 500));
		Component app = create(Application.class, new ApplicationInit(self, pi, commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, console.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, fpl.control());
		subscribe(handleFault, sc.control());
		subscribe(handleFault, ble.control());
		subscribe(handleFault, app.control());

		connect(app.required(SequenceConsensus.class), sc.provided(SequenceConsensus.class));
		connect(app.required(Console.class), console.provided(Console.class));
		connect(app.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

		connect(sc.required(FIFOPerfectPointToPointLink.class), fpl.provided(FIFOPerfectPointToPointLink.class));
		connect(sc.required(BallotLeaderElection.class), ble.provided(BallotLeaderElection.class));

		connect(ble.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(ble.required(Timer.class), timer.provided(Timer.class));

		connect(fpl.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(fpl.required(NetworkControl.class), network.provided(NetworkControl.class));

		connect(pp2p.required(Network.class), network.provided(Network.class));
		connect(pp2p.required(Timer.class), timer.provided(Timer.class));
	}

	private Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
