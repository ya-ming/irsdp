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
package se.kth.ict.id2203.pa.riwcmnnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.riwcmnnar.ArReadRequest;
import se.kth.ict.id2203.ports.riwcmnnar.ArReadResponse;
import se.kth.ict.id2203.ports.riwcmnnar.ArWriteRequest;
import se.kth.ict.id2203.ports.riwcmnnar.ArWriteResponse;
import se.kth.ict.id2203.ports.riwcmnnar.AtomicRegister;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.console.ConsoleLine;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public final class Application extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private Positive<Timer> timer = requires(Timer.class);
	private Positive<Console> console = requires(Console.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<AtomicRegister> ar = requires(AtomicRegister.class);

	private Set<Address> neighborSet;
	private Address self;

	private List<String> commands;
	private boolean blocking;

	public Application(ApplicationInit event) {
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, console);
		subscribe(handlePp2pMessage, pp2p);
		subscribe(handleArReadResponse, ar);
		subscribe(handleArWriteResponse, ar);

		self = event.getSelfAddress();
		neighborSet = new TreeSet<Address>(event.getAllAddresses());
		neighborSet.remove(self);

		commands = new ArrayList<String>(Arrays.asList(event.getCommandScript().split(":")));
        commands.add("$DONE");
        blocking = false;
	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			doNextCommand();
		}
	};

	private Handler<ApplicationContinue> handleContinue = new Handler<ApplicationContinue>() {
		@Override
		public void handle(ApplicationContinue event) {
			logger.info("Woke up from sleep");
			blocking = false;
			doNextCommand();
		}
	};

	private Handler<ConsoleLine> handleConsoleInput = new Handler<ConsoleLine>() {
		@Override
		public void handle(ConsoleLine event) {
			commands.addAll(Arrays.asList(event.getLine().trim().split(":")));
			doNextCommand();
		}
	};

	private Handler<Pp2pMessage> handlePp2pMessage = new Handler<Pp2pMessage>() {
		@Override
		public void handle(Pp2pMessage event) {
			logger.info("Received perfect message {}", event.getMessage());
		}
	};

	private Handler<ArReadResponse> handleArReadResponse = new Handler<ArReadResponse>() {
		@Override
		public void handle(ArReadResponse event) {
			logger.info("Read atomic value {} done.", event.getValue());
			blocking = false;
			doNextCommand();
		}
	};

	private Handler<ArWriteResponse> handleArWriteResponse = new Handler<ArWriteResponse>() {
		@Override
		public void handle(ArWriteResponse event) {
			logger.info("Write atomic value done.");
			blocking = false;
			doNextCommand();
		}
	};

	private final void doNextCommand() {
		while (!blocking && !commands.isEmpty()) {
            doCommand(commands.remove(0));
		}
	}

	private void doCommand(String cmd) {
		if (cmd.startsWith("P")) {
			doPerfect(cmd.substring(1));
		} else if (cmd.startsWith("R")) {
			doRead();
		} else if (cmd.startsWith("W")) {
			doWrite(cmd.substring(1));
		} else if (cmd.startsWith("S")) {
			doSleep(Integer.parseInt(cmd.substring(1)));
		} else if (cmd.startsWith("X")) {
			doShutdown();
		} else if (cmd.equals("help")) {
			doHelp();
		} else if (cmd.equals("$DONE")) {
			logger.info("DONE ALL OPERATIONS");
		} else {
			logger.info("Bad command: '{}'. Try 'help'", cmd);
		}
	}

	private final void doHelp() {
		logger.info("Available commands: P<m>, R, W<n>, S<n>, help, X");
		logger.info("Pm: sends perfect message 'm' to all neighbors");
		logger.info("R: read from atomic register");
		logger.info("Wn: write integer 'n' to atomic register");
		logger.info("Sn: sleeps 'n' milliseconds before the next command");
		logger.info("help: shows this help message");
		logger.info("X: terminates this process");
	}

    private final void doPerfect(String message) {
		for (Address neighbor : neighborSet) {
			logger.info("Sending perfect message {} to {}", message, neighbor);
			trigger(new Pp2pSend(neighbor, new Pp2pMessage(self, message)),
					pp2p);
		}
	}

	private final void doRead() {
		logger.info("Read atomic value.");
		trigger(new ArReadRequest(), ar);
		blocking = true;
	}

	private final void doWrite(Object v) {
		logger.info("Write atomic value {}.", v);
		trigger(new ArWriteRequest(v), ar);
		blocking = true;
	}

	private void doSleep(long delay) {
		logger.info("Sleeping {} milliseconds...", delay);

		ScheduleTimeout st = new ScheduleTimeout(delay);
		st.setTimeoutEvent(new ApplicationContinue(st));
		trigger(st, timer);
		
		blocking = true;
	}

	private void doShutdown() {
		System.out.println("2DIE");
		System.out.close();
		System.err.close();
		Kompics.shutdown();
		blocking = true;
	}
}
