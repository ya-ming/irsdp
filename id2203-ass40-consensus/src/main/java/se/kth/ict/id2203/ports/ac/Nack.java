package se.kth.ict.id2203.ports.ac;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class Nack extends Pp2pDeliver {
	/**
	 *
	 */
	private static final long serialVersionUID = -6261789079595882498L;

	private final int consensusId;

	public Nack(Address source, int consensusId) {
		super(source);
		this.consensusId = consensusId;
	}

	public int getConsensusId() {
		return consensusId;
	}

}
