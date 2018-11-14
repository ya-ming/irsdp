package se.kth.ict.id2203.appStubborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.console.ConsoleLine;
import se.kth.ict.id2203.ports.sp2p.Sp2pSend;
import se.kth.ict.id2203.ports.sp2p.StubbornPointToPointLink;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.*;

public final class ApplicationAssStubborn extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationAssStubborn.class);

	private Positive<Timer> timer = requires(Timer.class);
	private Positive<Console> console = requires(Console.class);
	private Positive<StubbornPointToPointLink> sp2p = requires(StubbornPointToPointLink.class);

	private Address self;
	private Set<Address> neighbors;

	private List<String> commands;
	private boolean blocking;

	public ApplicationAssStubborn(ApplicationAssStubbornInit event) {
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, console);
		subscribe(handleSp2pMessage, sp2p);

		self = event.getSelfAddress();
		neighbors = new TreeSet<Address>(event.getAllAddresses());
		neighbors.remove(self);

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

	private Handler<ApplicationAssStubbornContinue> handleContinue = new Handler<ApplicationAssStubbornContinue>() {
		@Override
		public void handle(ApplicationAssStubbornContinue event) {
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

	private Handler<Sp2pMessage> handleSp2pMessage = new Handler<Sp2pMessage>() {
		@Override
		public void handle(Sp2pMessage event) {
			logger.info("Received Stubborn message {}", event.getMessage());
		}
	};

	private final void doNextCommand() {
		while (!blocking && !commands.isEmpty()) {
            doCommand(commands.remove(0));
		}
	}

	private void doCommand(String cmd) {
		if (cmd.startsWith("L")) {
			doStubborn(cmd.substring(1));
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
		logger.info("Available commands: L<m>, S<n>, help, X");
		logger.info("Pm: sends perfect message 'm' to all neighbors");
		logger.info("Lm: sends lossy message 'm' to all neighbors");
		logger.info("Sn: sleeps 'n' milliseconds before the next command");
		logger.info("help: shows this help message");
		logger.info("X: terminates this process");
	}

	private final void doStubborn(String message) {
		for (Address neighbor : neighbors) {
			logger.info("Sending Stubborn message {} to {}", message, neighbor);
			trigger(new Sp2pSend(neighbor, new Sp2pMessage(self, message)), sp2p);
		}
	}

	private void doSleep(long delay) {
		logger.info("Sleeping {} milliseconds...", delay);

		ScheduleTimeout st = new ScheduleTimeout(delay);
		st.setTimeoutEvent(new ApplicationAssStubbornContinue(st));
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
