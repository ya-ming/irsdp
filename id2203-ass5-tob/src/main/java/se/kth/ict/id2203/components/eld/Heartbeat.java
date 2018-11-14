package se.kth.ict.id2203.components.eld;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class Heartbeat extends Pp2pDeliver {

	private static final long serialVersionUID = 440683825752009502L;

	protected Heartbeat(Address source) {
		super(source);
	}
}
