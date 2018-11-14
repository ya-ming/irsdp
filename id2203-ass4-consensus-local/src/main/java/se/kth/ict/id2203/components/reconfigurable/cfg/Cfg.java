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
package se.kth.ict.id2203.components.reconfigurable.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.reconfigurable.cfg.Config;
import se.kth.ict.id2203.ports.reconfigurable.cfg.ConfigPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Start;

public class Cfg extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Cfg.class);

	private Negative<ConfigPort> configPort = provides(ConfigPort.class);

	public Cfg() {
		subscribe(handleConfiguration, configPort);
	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
		}
	};

	private Handler<Config> handleConfiguration = new Handler<Config>() {
		@Override
		public void handle(Config event) {
			logger.debug(" handleConfiguration" + event.getConfiguration());
			trigger(event.getConfiguration() , configPort);
		}
	};
}
