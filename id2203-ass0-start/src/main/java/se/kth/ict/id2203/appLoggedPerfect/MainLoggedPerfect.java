/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009-2014 KTH Royal Institute of Technology
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
package se.kth.ict.id2203.appLoggedPerfect;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.components.console.JavaConsole;
import se.kth.ict.id2203.components.flp2p.FairLossLink;
import se.kth.ict.id2203.components.flp2p.FairLossLinkInit;
import se.kth.ict.id2203.components.lpp2p.LoggedPerfectLink;
import se.kth.ict.id2203.components.lpp2p.LoggedPerfectLinkInit;
import se.kth.ict.id2203.components.pp2p.PerfectLink;
import se.kth.ict.id2203.components.pp2p.PerfectLinkInit;
import se.kth.ict.id2203.components.sp2p.StubbornLink;
import se.kth.ict.id2203.components.sp2p.StubbornLinkInit;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.sp2p.StubbornPointToPointLink;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class MainLoggedPerfect extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;
	private Topology topology = Topology.load(System.getProperty("topology"), selfId);

	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);
		commandScript = args[1];
		Kompics.createAndStart(MainLoggedPerfect.class);
	}

	public MainLoggedPerfect() {
		Address self = topology.getSelfAddress();

		Component timer = create(JavaTimer.class, Init.NONE);
		Component network = create(NettyNetwork.class, new NettyNetworkInit(self, 5));
		Component console = create(JavaConsole.class, Init.NONE);
		Component lpp2p = create(LoggedPerfectLink.class, new LoggedPerfectLinkInit(topology));
		Component flp2p = create(FairLossLink.class, new FairLossLinkInit(topology, 0));
		Component sp2p = create(StubbornLink.class, new StubbornLinkInit(topology, 0));
		Component app = create(ApplicationLoggedPefect.class, new ApplicationLoggedPerfectInit(self, topology.getAllAddresses(), commandScript));

		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, console.control());
		subscribe(handleFault, lpp2p.control());
        subscribe(handleFault, flp2p.control());
		subscribe(handleFault, sp2p.control());
		subscribe(handleFault, app.control());

		connect(app.required(Console.class), console.provided(Console.class));
		connect(app.required(PerfectPointToPointLink.class), lpp2p.provided(PerfectPointToPointLink.class));
//		connect(app.required(FairLossPointToPointLink.class), flp2p.provided(FairLossPointToPointLink.class));
		connect(app.required(Timer.class), timer.provided(Timer.class));

        connect(lpp2p.required(StubbornPointToPointLink.class), sp2p.provided(StubbornPointToPointLink.class));

		connect(sp2p.required(Timer.class), timer.provided(Timer.class));
        connect(sp2p.required(FairLossPointToPointLink.class), flp2p.provided(FairLossPointToPointLink.class));

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
