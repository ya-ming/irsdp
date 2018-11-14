package se.kth.ict.id2203.ports.ac;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class Write extends BebDeliver {
	private final int consensusId;
	private final int ts;
	private final Integer value;

	public Write(Address source, int consensusId, int ts, Integer value) {
		super(source);
		this.consensusId = consensusId;
		this.ts = ts;
		this.value = value;
	}

	public int getConsensusId() {
		return consensusId;
	}

	public int getTs() {
		return ts;
	}

	public Integer getValue() {
		return value;
	}

}
