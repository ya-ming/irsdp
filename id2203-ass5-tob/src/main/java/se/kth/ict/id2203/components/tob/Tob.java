package se.kth.ict.id2203.components.tob;

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.asc.AbortableSequenceConsensus;
import se.kth.ict.id2203.ports.asc.AscAbort;
import se.kth.ict.id2203.ports.asc.AscDecide;
import se.kth.ict.id2203.ports.asc.AscPropose;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.eld.EventualLeaderDetector;
import se.kth.ict.id2203.ports.eld.Trust;
import se.kth.ict.id2203.ports.tob.TobBroadcast;
import se.kth.ict.id2203.ports.tob.TotalOrderBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class Tob extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Tob.class);

	private Negative<TotalOrderBroadcast> tob = provides(TotalOrderBroadcast.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<EventualLeaderDetector> eld = requires(EventualLeaderDetector.class);
	private Positive<AbortableSequenceConsensus> asc = requires(AbortableSequenceConsensus.class);

	private final Address self;
	private Address trusted;
	private int nextSeqNum = 0;

	private TreeSet<Message> unordered = new TreeSet<Message>();
	private TreeSet<Message> delivered = new TreeSet<Message>();

	public Tob(TobInit init) {
		logger.info("Constructing Tob component.");

		subscribe(handleTobBroadcast, tob);
		subscribe(handleDisseminateMessage, beb);
		subscribe(handleTrust, eld);
		subscribe(handleAscDecide, asc);
		subscribe(handleAscAbort, asc);

		self = init.getSelfAddress();
	}

	private Handler<TobBroadcast> handleTobBroadcast = new Handler<TobBroadcast>() {
		@Override
		public void handle(TobBroadcast event) {
			nextSeqNum += 1;
			Message m = new Message(nextSeqNum, self.getId(), event.getDeliverEvent());
			logger.info("TobBroadcast {}", m);
			trigger(new BebBroadcast(new DisseminateMessage(self, m)), beb);
		}
	};

	private Handler<DisseminateMessage> handleDisseminateMessage = new Handler<DisseminateMessage>() {
		@Override
		public void handle(DisseminateMessage event) {
			logger.info("DisseminateMessage {}", event.getMessage());
			Message m = event.getMessage();
			if (!delivered.contains(m)) {
				unordered.add(event.getMessage());
				if (self.equals(trusted)) {
					trigger(new AscPropose(m), asc);
				}
			}
		}
	};

	private Handler<Trust> handleTrust = new Handler<Trust>() {
		@Override
		public void handle(Trust event) {
			logger.info("Trust {}", event.getLeader());
			trusted = event.getLeader();
			if (self.equals(trusted)) {
				for (Message m : unordered) {
					trigger(new AscPropose(m), asc);
				}
			}
		}
	};

	private Handler<AscAbort> handleAscAbort = new Handler<AscAbort>() {
		@Override
		public void handle(AscAbort event) {
			logger.info("AscAbort");
			if (self.equals(trusted)) {
				for (Message m : unordered) {
					trigger(new AscPropose(m), asc);
				}
			}
		}
	};

	private Handler<AscDecide> handleAscDecide = new Handler<AscDecide>() {
		@Override
		public void handle(AscDecide event) {
			logger.info("AscDecide {}", event.getValue());
			Message m = (Message) event.getValue();
			delivered.add(m);
			unordered.remove(m);
			trigger(m.getDeliverEvent(), tob);
		}
	};
}
