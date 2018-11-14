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
package se.kth.ict.id2203.app.fuc;

import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class Executor {
	public static final void main(String[] args) {
		Topology topology1 = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
				node(3, "127.0.0.1", 22033);
				defaultLinks(1000, 0);
			}
		};

		Topology topology2 = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
				node(3, "127.0.0.1", 22033);
				node(4, "127.0.0.1", 22034);
				link(1, 2, 500, 0).bidirectional();
				link(1, 3, 10000, 0).bidirectional();
				link(1, 4, 10000, 0).bidirectional();
				link(2, 3, 1000, 0).bidirectional();
				link(2, 4, 1000, 0).bidirectional();
				link(3, 4, 1000, 0).bidirectional();
			}
		};
		Scenario scenario1 = new Scenario(Main.class) {
			{
				command(1, "S100:C1");
				command(2, "S100:C2");
				command(3, "S100:C3");
			}
		};

		Scenario scenario2 = new Scenario(Main.class) {
			{
				command(1, "S100:C1:S700:X");
				command(2, "S100:C2");
				command(3, "S100:C3");
				command(4, "S100:C4");
			}
		};
		scenario1.executeOn(topology1);
//		scenario2.executeOn(topology2);

        System.exit(0);
	}
}
