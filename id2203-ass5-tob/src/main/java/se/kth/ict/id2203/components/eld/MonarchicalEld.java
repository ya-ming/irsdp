package se.kth.ict.id2203.components.eld;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.eld.EventualLeaderDetector;
import se.kth.ict.id2203.ports.eld.Trust;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class MonarchicalEld extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(MonarchicalEld.class);

	private Negative<EventualLeaderDetector> eld = provides(EventualLeaderDetector.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<Timer> timer = requires(Timer.class);

	private Address self;
	private Set<Address> addresses;

	private Address leader;
	private Set<Address> candidates;
	private int timeoutPeriod;
	private int timeoutPeriodDelta;

	public MonarchicalEld(MonarchicalEldInit event) {
		logger.info("Constructing MonarchicalEld component.");

		subscribe(handleStart, control);
		subscribe(handleTimeout, timer);
		subscribe(handleHeartbeat, pp2p);

		self = event.getSelfAddress();
		addresses = event.getAllAddresses(); 
		timeoutPeriod = event.getInitialTimeoutPeriod();
		timeoutPeriodDelta = event.getTimeoutPeriodDelta();
	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			leader = selectLowestId(addresses);

			trigger(new Trust(leader), eld);

			for (Address address : addresses) {
				trigger(new Pp2pSend(address, new Heartbeat(self)), pp2p);
			}

			candidates = new HashSet<Address>();

			ScheduleTimeout st = new ScheduleTimeout(timeoutPeriod);
			st.setTimeoutEvent(new EldTimeout(st));
			trigger(st, timer);
		}
	};

	private Address selectLowestId(Set<Address> from) {
		Address min = null;
		for (Address address : from) {
			if (min == null || min.getId() > address.getId()) {
				min = address;
			}
		}
		return min;
	}

	private Handler<EldTimeout> handleTimeout = new Handler<EldTimeout>() {
		@Override
		public void handle(EldTimeout event) {
			Address newLeader = selectLowestId(candidates);

			if (newLeader != null && !leader.equals(newLeader)) {
				timeoutPeriod += timeoutPeriodDelta;
				leader = newLeader;
				trigger(new Trust(leader), eld);
			}

			for (Address address : addresses) {
				trigger(new Pp2pSend(address, new Heartbeat(self)), pp2p);
			}

			candidates.clear();

			ScheduleTimeout st = new ScheduleTimeout(timeoutPeriod);
			st.setTimeoutEvent(new EldTimeout(st));
			trigger(st, timer);
		}
	};

	private Handler<Heartbeat> handleHeartbeat = new Handler<Heartbeat>() {
		@Override
		public void handle(Heartbeat event) {
			candidates.add(event.getSource());
		}
	};
}
