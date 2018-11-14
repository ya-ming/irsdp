package se.kth.ict.id2203.app.fpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.console.ConsoleLine;
import se.kth.ict.id2203.ports.epfd.Restore;
import se.kth.ict.id2203.ports.epfd.Suspect;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.fpl.FPp2pSend;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public final class Application extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private Positive<Timer> timer = requires(Timer.class);
	private Positive<Console> console = requires(Console.class);
	private Positive<FIFOPerfectPointToPointLink> fpl = requires(FIFOPerfectPointToPointLink.class);

	private Address self;
	private Set<Address> neighbors;

	private ArrayList<String> commands;
	private boolean blocking;

	public Application(ApplicationInit event) {
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, console);
		subscribe(handleFPp2pMessage, fpl);

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
			if (event.getLine().equals("XX")) {
				doShutdown();
			} else {
				commands.addAll(Arrays.asList(event.getLine().trim().split(":")));
				doNextCommand();
			}
		}
	};

	private Handler<FPp2pMessage> handleFPp2pMessage = new Handler<FPp2pMessage>() {
		@Override
		public void handle(FPp2pMessage event) {
			logger.info("Received fifo perfect message {}", event.getMessage());
		}
	};

	private Handler<Suspect> handleSuspect = new Handler<Suspect>() {
		@Override
		public void handle(Suspect event) {
			logger.info("Process {} suspected", event.getSource());
		}
	};

	private Handler<Restore> handleRestore = new Handler<Restore>() {
		@Override
		public void handle(Restore event) {
			logger.info("Process {} restored", event.getSource());
		}
	};

	private final void doNextCommand() {
		while (!blocking && !commands.isEmpty()) {
            doCommand(commands.remove(0));
		}
	}

	private void doCommand(String cmd) {
		if (cmd.startsWith("P")) {
			doFIFOPerfect(cmd.substring(1));
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
		logger.info("Available commands: P<m>, S<n>, help, X");
		logger.info("Pm: sends fifo perfect message 'm' to all neighbors");
		logger.info("Sn: sleeps 'n' milliseconds before the next command");
		logger.info("help: shows this help message");
		logger.info("X: terminates this process");
	}

    private final void doFIFOPerfect(String message) {
		for (Address neighbor : neighbors) {
			logger.info("Sending fifo perfect message {} to {}", message, neighbor);
			trigger(new FPp2pSend(neighbor, new FPp2pMessage(self, message)), fpl);
		}
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
