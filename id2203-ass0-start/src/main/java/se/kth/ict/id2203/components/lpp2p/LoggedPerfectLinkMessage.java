package se.kth.ict.id2203.components.lpp2p;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Transport;

public final class LoggedPerfectLinkMessage extends Message {

	private static final long serialVersionUID = -8044668011408046391L;
	
	private final Pp2pDeliver deliverEvent;

	public LoggedPerfectLinkMessage(Address source, Address destination,
									Pp2pDeliver deliverEvent) {
		super(source, destination, Transport.TCP);
		this.deliverEvent = deliverEvent;
	}

	public final Pp2pDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
