package se.kth.ict.id2203.components.fpl;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class DataMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 8697210125965432728L;

	private final FplDeliver deliverEvent;
	private int lsn;

	protected DataMessage(Address source, FplDeliver deliverEvent, int lsn) {
		super(source);
		this.deliverEvent = deliverEvent;
		this.lsn = lsn;
	}
	
	public FplDeliver getDeliverEvent() {
		return deliverEvent;
	}
	
	public int getLsn() {
		return lsn;
	}
}
