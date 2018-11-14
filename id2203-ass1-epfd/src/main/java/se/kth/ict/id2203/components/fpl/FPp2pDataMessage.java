package se.kth.ict.id2203.components.fpl;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class FPp2pDataMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private final FPp2pDeliver deliverEvent;

	private Integer lsn;

	public FPp2pDataMessage(Address source, FPp2pDeliver deliverEvent, Integer lsn) {
		super(source);
		this.deliverEvent = deliverEvent;
		this.lsn = lsn;
	}

	public final FPp2pDeliver getDeliverEvent() {
		return deliverEvent;
	}

	public Integer getLsn() {
		return lsn;
	}

	@Override
	public String toString() {
		return "FPp2pDataMessage{" +
				"deliverEvent=" + deliverEvent +
				", lsn=" + lsn +
				'}';
	}
}
