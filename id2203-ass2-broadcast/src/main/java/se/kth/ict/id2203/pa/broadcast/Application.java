package se.kth.ict.id2203.pa.broadcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.aurb.AllAckURbBroadcast;
import se.kth.ict.id2203.ports.aurb.AllAckUniformReliableBroadcast;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.console.Console;
import se.kth.ict.id2203.ports.console.ConsoleLine;
import se.kth.ict.id2203.ports.crb.CausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.crb.CrbBroadcast;
import se.kth.ict.id2203.ports.epb.EPbBroadcast;
import se.kth.ict.id2203.ports.epb.EagerProbabilisticBroadcast;
import se.kth.ict.id2203.ports.frb.FIFOReliableBroadcast;
import se.kth.ict.id2203.ports.frb.FRbBroadcast;
import se.kth.ict.id2203.ports.lrb.LRbBroadcast;
import se.kth.ict.id2203.ports.lrb.LazyReliableBroadcast;
import se.kth.ict.id2203.ports.lurb.MajorityAckLURbBroadcast;
import se.kth.ict.id2203.ports.lurb.MajorityAckLoggedUniformReliableBroadcast;
import se.kth.ict.id2203.ports.nwcrb.NWCrbBroadcast;
import se.kth.ict.id2203.ports.nwcrb.NoWaitingCausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.erb.RbBroadcast;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
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
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<EagerReliableBroadcast> rb = requires(EagerReliableBroadcast.class);
    private Positive<LazyReliableBroadcast> lrb = requires(LazyReliableBroadcast.class);
	private Positive<AllAckUniformReliableBroadcast> alackurb = requires(AllAckUniformReliableBroadcast.class);
	private Positive<MajorityAckLoggedUniformReliableBroadcast> lurb = requires(MajorityAckLoggedUniformReliableBroadcast.class);
	private Positive<EagerProbabilisticBroadcast> epb = requires(EagerProbabilisticBroadcast.class);
	private Positive<FIFOReliableBroadcast> frb = requires(FIFOReliableBroadcast.class);
	private Positive<CausalOrderReliableBroadcast> crb = requires(CausalOrderReliableBroadcast.class);
	private Positive<NoWaitingCausalOrderReliableBroadcast> nwcrb = requires(NoWaitingCausalOrderReliableBroadcast.class);

	private Set<Address> neighborSet;
	private Address self;

	private List<String> commands;
	private boolean blocking;

	public Application(ApplicationInit event) {
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, console);
		subscribe(handlePp2pMessage, pp2p);
		subscribe(handleBebMessage, beb);
		subscribe(handleRbMessage, rb);
        subscribe(handleLRbMessage, lrb);
        subscribe(handleAllAckURbMessage, alackurb);
		subscribe(handleMajorityAckLURbMessage, lurb);
		subscribe(handleEagerPbMessage, epb);
		subscribe(handleFRbMessage, frb);
		subscribe(handleCrbMessage, crb);
		subscribe(handleNWCrbMessage, nwcrb);

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
			logger.info("Delievered perfect message {}", event.getMessage());
		}
	};

	private Handler<BebMessage> handleBebMessage = new Handler<BebMessage>() {
		@Override
		public void handle(BebMessage event) {
			logger.info("Delivered best effort broadcast message {} from {}", event.getMessage(), event.getSource());
			if (self.getId() == 2 && event.getSource().getId() == 1) {
				logger.info("Best effort broadcast message {}-res", event.getMessage());
				trigger(new BebBroadcast(new BebMessage(self, event.getMessage() + "-res")), beb);
			}
		}
	};

	private Handler<RbMessage> handleRbMessage = new Handler<RbMessage>() {
		@Override
		public void handle(RbMessage event) {
			logger.info("Delivered reliable broadcast message {} from {}", event.getMessage(), event.getSource());
			if (self.getId() == 2 && event.getSource().getId() == 1) {
				logger.info("Reliable broadcast message {}-res", event.getMessage());
				trigger(new RbBroadcast(new RbMessage(self, event.getMessage() + "-res")), rb);
			}
		}
	};

    private Handler<LRbMessage> handleLRbMessage = new Handler<LRbMessage>() {
        @Override
        public void handle(LRbMessage event) {
            logger.info("Delivered lazy reliable broadcast message {} from {}", event.getMessage(), event.getSource());
            if (self.getId() == 2 && event.getSource().getId() == 1) {
//                logger.info("Lazy reliable broadcast message {}-res", event.getMessage());
//                trigger(new LRbBroadcast(new LRbMessage(self, event.getMessage() + "-res")), lrb);
            }
        }
    };

	private Handler<AllAckURbMessage> handleAllAckURbMessage = new Handler<AllAckURbMessage>() {
		@Override
		public void handle(AllAckURbMessage event) {
			logger.info("Delivered all ack uniform reliable broadcast message {} from {}", event.getMessage(), event.getSource());
			if (self.getId() == 2 && event.getSource().getId() == 1) {
//                logger.info("Lazy reliable broadcast message {}-res", event.getMessage());
//                trigger(new LRbBroadcast(new LRbMessage(self, event.getMessage() + "-res")), lrb);
			}
		}
	};

	private Handler<MajorityAckLURbMessage> handleMajorityAckLURbMessage = new Handler<MajorityAckLURbMessage>() {
		@Override
		public void handle(MajorityAckLURbMessage event) {
			logger.info("Delivered majority ack logged uniform reliable broadcast message {} from {}", event.getMessage(), event.getSource());
			if (self.getId() == 2 && event.getSource().getId() == 1) {
//                logger.info("Lazy reliable broadcast message {}-res", event.getMessage());
//                trigger(new LRbBroadcast(new LRbMessage(self, event.getMessage() + "-res")), lrb);
			}
		}
	};

	private Handler<EagerPbMessage> handleEagerPbMessage = new Handler<EagerPbMessage>() {
		@Override
		public void handle(EagerPbMessage event) {
			logger.info("Delivered eager probabilistic broadcast message {} from {}", event.getMessage(), event.getSource());
		}
	};

	private Handler<FRbMessage> handleFRbMessage = new Handler<FRbMessage>() {
		@Override
		public void handle(FRbMessage event) {
			logger.info("Delivered FIFO reliable broadcast message {} from {}", event.getMessage(), event.getSource());
		}
	};

	private Handler<NWCrbMessage> handleNWCrbMessage = new Handler<NWCrbMessage>() {
		@Override
		public void handle(NWCrbMessage event) {
			logger.info("Delivered No Waiting causal reliable broadcast message {} from {}", event.getMessage(), event.getSource());
			if (self.getId() == 2 && event.getSource().getId() == 1) {
				logger.info("Causal broadcast message {}-res", event.getMessage());
				trigger(new NWCrbBroadcast(new NWCrbMessage(self, event.getMessage() + "-res")), nwcrb);
			}
		}
	};

	private Handler<CrbMessage> handleCrbMessage = new Handler<CrbMessage>() {
		@Override
		public void handle(CrbMessage event) {
			logger.info("Delivered causal broadcast message {} from {}", event.getMessage(), event.getSource());
			if (self.getId() == 2 && event.getSource().getId() == 1) {
				logger.info("Causal broadcast message {}-res", event.getMessage());
				trigger(new CrbBroadcast(new CrbMessage(self, event.getMessage() + "-res")), crb);
			}
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
		} else if (cmd.startsWith("B")) {
			doBestEffortBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("R")) {
			doReliableBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("L")) {
			doLazyReliableBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("A")) {
            doAllAckUniformReliableBroadcast(cmd.substring(1));
        } else if (cmd.startsWith("M")) {
			doMajorityAckLoggedUniformReliableBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("E")) {
			doEagerProbabilisticBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("F")) {
			doFIFOReliableBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("N")) {
			doNoWaitingCausalBroadcast(cmd.substring(1));
		} else if (cmd.startsWith("C")) {
			doCausalBroadcast(cmd.substring(1));
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
		logger.info("Available commands: P<m>, B<m>, R<m>, L<m>, A<m>, M<m>, E<m>, F<m>, N<m>, C<m>, S<n>, help, X");
		logger.info("Pm: sends perfect message 'm' to all neighbors");
		logger.info("Bm: best effort broadcast message 'm'");
		logger.info("Rm: reliable broadcast message 'm'");
        logger.info("Lm: lazy reliable broadcast message 'm'");
        logger.info("Am: all ack uniform reliable broadcast message 'm'");
		logger.info("Mm: logged uniform reliable broadcast message 'm'");
		logger.info("Em: eager probabilistic broadcast message 'm'");
		logger.info("Fm: fifo reliable broadcast message 'm'");
		logger.info("Nm: no waiting causal reliable broadcast message 'm'");
		logger.info("Cm: causal reliable broadcast message 'm'");
		logger.info("Sn: sleeps 'n' milliseconds before the next command");
		logger.info("help: shows this help message");
		logger.info("X: terminates this process");
	}

    private final void doPerfect(String message) {
		for (Address neighbor : neighborSet) {
			logger.info("Sending perfect message {} to {}", message, neighbor);
			trigger(new Pp2pSend(neighbor, new Pp2pMessage(self, message)), pp2p);
		}
	}

	private final void doBestEffortBroadcast(String message) {
		logger.info("Best effort broadcast message {}", message);
		trigger(new BebBroadcast(new BebMessage(self, message)), beb);
	}

	private final void doReliableBroadcast(String message) {
		logger.info("Reliable broadcast message {}", message);
		trigger(new RbBroadcast(new RbMessage(self, message)), rb);
	}

    private final void doLazyReliableBroadcast(String message) {
        logger.info("Lazy reliable broadcast message {}", message);
        trigger(new LRbBroadcast(new LRbMessage(self, message)), lrb);
    }

    private final void doAllAckUniformReliableBroadcast(String message) {
		logger.info("All ack uniform reliable broadcast message {}", message);
		trigger(new AllAckURbBroadcast(new AllAckURbMessage(self, message)), alackurb);
	}

	private final void doMajorityAckLoggedUniformReliableBroadcast(String message) {
		logger.info("Majority ack logged uniform reliable broadcast message {}", message);
		trigger(new MajorityAckLURbBroadcast(new MajorityAckLURbMessage(self, message)), lurb);
	}

	private final void doEagerProbabilisticBroadcast(String message) {
		logger.info("Eager probabilistic broadcast message {}", message);
		trigger(new EPbBroadcast(new EagerPbMessage(self, message)), epb);
	}

	private final void doFIFOReliableBroadcast(String message) {
		logger.info("FIFO reliable broadcast message {}", message);
		trigger(new FRbBroadcast(new FRbMessage(self, message)), frb);
	}

	private final void doCausalBroadcast(String message) {
		logger.info("Causal broadcast message {}", message);
		trigger(new CrbBroadcast(new CrbMessage(self, message)), crb);
	}

	private final void doNoWaitingCausalBroadcast(String message) {
		logger.info("No waiting causal broadcast message {}", message);
		trigger(new NWCrbBroadcast(new NWCrbMessage(self, message)), nwcrb);
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
