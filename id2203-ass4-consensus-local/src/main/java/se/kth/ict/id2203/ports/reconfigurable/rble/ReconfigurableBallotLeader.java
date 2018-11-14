package se.kth.ict.id2203.ports.reconfigurable.rble;

import se.kth.ict.id2203.components.reconfigurable.rble.ReBallot;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class ReconfigurableBallotLeader extends Event {

	private final Address leader;
	private final ReBallot ballot;

	public ReconfigurableBallotLeader(Address leader, ReBallot ballot) {
		this.leader = leader;
		this.ballot = ballot;
	}

	public Address getLeader() {
		return leader;
	}

	public ReBallot getBallot() {
		return ballot;
	}

	@Override
	public String toString() {
		return "ReconfigurableBallotLeader{" +
				"leader=" + leader +
				", ballot=" + ballot +
				'}';
	}
}
