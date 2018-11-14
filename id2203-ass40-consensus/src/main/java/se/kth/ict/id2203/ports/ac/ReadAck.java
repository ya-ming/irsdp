package se.kth.ict.id2203.ports.ac;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class ReadAck extends Pp2pDeliver {

	/**
	 *
	 */
	private static final long serialVersionUID = 3820816010867427802L;

	private final int consensusId;
	private final int timestamp;
	private final Integer value;
	private final int sentTimestamp;

	public ReadAck(Address source, int consensusId, int timestamp, Integer value, int sentTimestamp) {
		super(source);
		this.consensusId = consensusId;
		this.timestamp = timestamp;
		this.value = value;
		this.sentTimestamp = sentTimestamp;
	}

	public int getConsensusId() {
		return consensusId;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public Integer getValue() {
		return value;
	}

	public int getSentTimestamp() {
		return sentTimestamp;
	}

}
