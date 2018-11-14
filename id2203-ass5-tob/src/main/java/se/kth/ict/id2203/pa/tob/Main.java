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
package se.kth.ict.id2203.pa.tob;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import se.kth.ict.id2203.components.beb.BasicBroadcast;
import se.kth.ict.id2203.components.beb.BasicBroadcastInit;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.eld.MonarchicalEld;
import se.kth.ict.id2203.components.eld.MonarchicalEldInit;
import se.kth.ict.id2203.components.fpl.SequenceNumberFIFOLink;
import se.kth.ict.id2203.components.fpl.SequenceNumberFIFOLinkInit;
import se.kth.ict.id2203.components.pp2p.DelayLink;
import se.kth.ict.id2203.components.pp2p.DelayLinkInit;
import se.kth.ict.id2203.components.tob.Tob;
import se.kth.ict.id2203.components.tob.TobInit;
import se.kth.ict.id2203.components.multipaxos.MultiPaxos;
import se.kth.ict.id2203.components.multipaxos.MultiPaxosInit;
import se.kth.ict.id2203.ports.asc.AbortableSequenceConsensus;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.eld.EventualLeaderDetector;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.tob.TotalOrderBroadcast;
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
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;
	Topology topology = Topology.load(System.getProperty("topology"), selfId);

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
		Component console = create(JavaConsole.class, Init.NONE);
		Component pp2p = create(DelayLink.class, new DelayLinkInit(topology));
		Component beb = create(BasicBroadcast.class, new BasicBroadcastInit(self, pi));
		Component fpl = create(SequenceNumberFIFOLink.class, new SequenceNumberFIFOLinkInit(self, pi));
		Component asc = create(MultiPaxos.class, new MultiPaxosInit(self, pi));
		Component eld = create(MonarchicalEld.class, new MonarchicalEldInit(self, pi, 2500, 500));
		Component tob = create(Tob.class, new TobInit(self, pi));
		Component app = create(Application.class, new ApplicationInit(self, pi, commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, console.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, beb.control());
		subscribe(handleFault, fpl.control());
		subscribe(handleFault, asc.control());
		subscribe(handleFault, eld.control());
		subscribe(handleFault, tob.control());
		subscribe(handleFault, app.control());

		connect(app.required(TotalOrderBroadcast.class), tob.provided(TotalOrderBroadcast.class));
		connect(app.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(Console.class), console.provided(Console.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

		connect(tob.required(EventualLeaderDetector.class), eld.provided(EventualLeaderDetector.class));
		connect(tob.required(AbortableSequenceConsensus.class), asc.provided(AbortableSequenceConsensus.class));
		connect(tob.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));

		connect(eld.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(eld.required(Timer.class), timer.provided(Timer.class));

		connect(asc.required(FIFOPerfectPointToPointLink.class), fpl.provided(FIFOPerfectPointToPointLink.class));

		connect(fpl.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(beb.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(pp2p.required(Network.class), network.provided(Network.class));
		connect(pp2p.required(Timer.class), timer.provided(Timer.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
