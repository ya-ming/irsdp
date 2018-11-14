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
package se.kth.ict.id2203.pa.riwcannar;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.components.beb.BasicBroadcast;
import se.kth.ict.id2203.components.beb.BasicBroadcastInit;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.pfd.Pfd;
import se.kth.ict.id2203.components.pfd.PfdInit;
import se.kth.ict.id2203.components.riwcannar.ReadImposeWriteConsultAll;
import se.kth.ict.id2203.components.riwcannar.ReadImposeWriteConsultAllInit;
import se.kth.ict.id2203.components.pp2p.DelayLink;
import se.kth.ict.id2203.components.pp2p.DelayLinkInit;
import se.kth.ict.id2203.components.riwmonar.ReadImposeWriteMajority;
import se.kth.ict.id2203.components.riwmonar.ReadImposeWriteMajorityInit;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.riwcannar.ReadImposeWriteConsultAllNNAtomicRegister;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.riwmonar.ReadImposeWriteMajorityOneNAtomicRegister;
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
		Component network = create(NettyNetwork.class, new NettyNetworkInit(topology.getSelfAddress(), 5));
		Component console = create(JavaConsole.class, Init.NONE);
		Component pp2p = create(DelayLink.class, new DelayLinkInit(topology, 0));
		Component beb = create(BasicBroadcast.class, new BasicBroadcastInit(self, pi));
		Component pfd = create(Pfd.class, new PfdInit(self, pi, 4000, 500));
		Component riwmon = create(ReadImposeWriteConsultAll.class, new ReadImposeWriteConsultAllInit(self, pi));
		Component app = create(Application.class, new ApplicationInit(self, pi, commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, console.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, beb.control());
		subscribe(handleFault, riwmon.control());
		subscribe(handleFault, pfd.control());
		subscribe(handleFault, app.control());

		connect(app.required(ReadImposeWriteConsultAllNNAtomicRegister.class), riwmon.provided(ReadImposeWriteConsultAllNNAtomicRegister.class));
		connect(app.required(Console.class), console.provided(Console.class));
		connect(app.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

		connect(riwmon.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));
		connect(riwmon.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(riwmon.required(PerfectFailureDetector.class), pfd.provided(PerfectFailureDetector.class));

		connect(pfd.required(Timer.class), timer.provided(Timer.class));
		connect(pfd.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(beb.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

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
