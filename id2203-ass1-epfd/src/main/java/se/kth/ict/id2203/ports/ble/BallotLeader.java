package se.kth.ict.id2203.ports.ble;

import se.kth.ict.id2203.components.ble.Ballot;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class BallotLeader extends Event {

	private final Address leader;
	private final Ballot ballot;

	public BallotLeader(Address leader, Ballot ballot) {
		this.leader = leader;
		this.ballot = ballot;
	}

	public Address getLeader() {
		return leader;
	}

	public Ballot getBallot() {
		return ballot;
	}

	@Override
	public String toString() {
		return "BallotLeader{" +
				"leader=" + leader +
				", ballot=" + ballot +
				'}';
	}
}
