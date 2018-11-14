package se.kth.ict.id2203.ports.ac;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class Read extends BebDeliver {
	/**
	 *
	 */
	private static final long serialVersionUID = 6725846653895098903L;

	private final int consensusId;
	private final int tstamp;

	public Read(Address source, int consensusId, int tstamp) {
		super(source);
		this.consensusId = consensusId;
		this.tstamp = tstamp;
	}

	public int getConsensusId() {
		return consensusId;
	}

	public int getTstamp() {
		return tstamp;
	}


}
