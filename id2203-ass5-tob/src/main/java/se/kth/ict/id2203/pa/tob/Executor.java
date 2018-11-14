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
				defaultLinks(1500, 0.0);
			}
		};

		Scenario scenario = new Scenario(Main.class) {
			{
				command(1, "S10:Bmsg1");
				command(2, "S10");
				command(3, "S10");
			}
		};

//		Scenario scenario = new Scenario(Main.class) {
//			{
//				command(1, "Bmsg1");
//				command(2, "Bmsg2");
//				command(3, "Bmsg3");
//			}
//		};

//		Scenario scenario = new Scenario(Main.class) {
//			{
//				command(1, "Bmsg11:S4000:X");
//				command(2, "Bmsg21:S2000:Bmsg22:S2000:Bmsg23:S2000:Bmsg24");
//				command(3, "Bmsg31:S1000:Bmsg32:S1000:Bmsg33:S1000:Bmsg34:S1000:Bmsg35");
//			}
//		};

		scenario.executeOn(topology);

		System.exit(0);
	}
}
