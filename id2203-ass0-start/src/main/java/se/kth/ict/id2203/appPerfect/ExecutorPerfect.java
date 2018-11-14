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
package se.kth.ict.id2203.appPerfect;

import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class ExecutorPerfect {

	public static final void main(String[] args) {
		Topology topology1 = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
				link(1, 2, 1000, 0.5).bidirectional();
				// link(1, 2, 0, 0.99).bidirectional();
			}
		};

		Topology topology2 = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
				node(3, "127.0.0.1", 22033);
				node(4, "127.0.0.1", 22034);

				link(1, 2, 3000, 0.5).bidirectional();
//				 link(1, 2, 3000, 0.5);
//				 link(2, 1, 3000, 0.5);
//				 link(3, 2, 3000, 0.5);
//				 link(4, 2, 3000, 0.5);
				defaultLinks(1000, 0);
			}
		};

		Topology topology3 = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
                node(3, "127.0.0.1", 22033);
                defaultLinks(200, 0);
			}
		};

		Scenario scenario1 = new Scenario(MainPerfect.class) {
			{
				command(1, "S500:PAA:S300:PBB");
				command(2, "S500:PCC:S300:PDD");
			}
		};

		Scenario scenario2 = new Scenario(MainPerfect.class) {
			{
				command(1, "S2500:La1:S300:PA1:X").recover("S400:Pff", 1000);
				command(2, "S2500:Pb2:S300:LB2");
				command(3, "S2500:Lc3:S300:PC3");
				command(4, "S2500:Pd4:S300:LD4");
			}
		};

        Scenario scenario3 = new Scenario(MainPerfect.class) {
            {
                command(1, "S500:PA:PB:PC:PD:PE:PF");
                command(2, "S500");
                command(3, "S500");
            }
        };

		 scenario1.executeOn(topology1);
//		scenario1.executeOn(topology2);
//		 scenario2.executeOn(topology1);
//		 scenario2.executeOn(topology2);
//		 scenario1.executeOnFullyConnected(topology1);
//		 scenario1.executeOnFullyConnected(topology2);
		// scenario2.executeOnFullyConnected(topology1);
		// scenario2.executeOnFullyConnected(topology2);
//        scenario3.executeOn(topology3);
		System.exit(0);
		// move one of the below scenario executions above the exit for
		// execution

//		scenario1.executeOn(topology1);
//		scenario1.executeOn(topology2);
//		scenario2.executeOn(topology1);
//		scenario2.executeOn(topology2);
//		scenario1.executeOnFullyConnected(topology1);
//		scenario1.executeOnFullyConnected(topology2);
//		scenario2.executeOnFullyConnected(topology1);
//		scenario2.executeOnFullyConnected(topology2);
	}
}
