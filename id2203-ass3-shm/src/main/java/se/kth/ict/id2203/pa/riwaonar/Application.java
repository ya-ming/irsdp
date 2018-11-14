package se.kth.ict.id2203.pa.riwaonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.pa.riwcmnnar.Pp2pMessage;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.console.ConsoleLine;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.riwaonar.ReadImposeWriteAllOneNAtomicRegister;
import se.kth.ict.id2203.ports.rowaonrr.*;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.*;

public final class Application extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private Positive<Timer> timer = requires(Timer.class);
	private Positive<Console> console = requires(Console.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<ReadImposeWriteAllOneNAtomicRegister> riwaonar = requires(ReadImposeWriteAllOneNAtomicRegister.class);

	private Set<Address> neighborSet;
	private Address self;

	private List<String> commands;
	private boolean blocking;

	public Application(ApplicationInit event) {
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, console);
		subscribe(handlePp2pMessage, pp2p);
		subscribe(handleReadResponse, riwaonar);
		subscribe(handleWriteResponse, riwaonar);

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

	private Handler<ReadResponse> handleReadResponse = new Handler<ReadResponse>() {
		@Override
		public void handle(ReadResponse event) {
			logger.info("Read regular value {} done.", event.getValue());
			blocking = false;
			doNextCommand();
		}
	};

	private Handler<WriteResponse> handleWriteResponse = new Handler<WriteResponse>() {
		@Override
		public void handle(WriteResponse event) {
			logger.info("Write regular value done.");
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
		logger.info("R: read from regular register");
		logger.info("Wn: write integer 'n' to regular register");
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
		logger.info("Read regular value.");
		trigger(new ReadRequest(), riwaonar);
		blocking = true;
	}

	private final void doWrite(Object v) {
		logger.info("Write regular value {}.", v);
		trigger(new WriteRequest(v), riwaonar);
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
