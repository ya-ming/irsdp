package se.kth.ict.id2203.components.fpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.fpl.FplSend;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class SequenceNumberFIFOLink extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(SequenceNumberFIFOLink.class);

	private Negative<FIFOPerfectPointToPointLink> fpl = provides(FIFOPerfectPointToPointLink.class);
	private Positive<PerfectPointToPointLink> pl = requires(PerfectPointToPointLink.class);
	
	private final Address self;
	private final Set<Address> pi;
	private final HashMap<Address, Integer> lsn = new HashMap<Address, Integer>();
	private final HashMap<Address, Integer> next = new HashMap<Address, Integer>();
	private final ArrayList<DataMessage> pending = new ArrayList<DataMessage>();

	public SequenceNumberFIFOLink(SequenceNumberFIFOLinkInit event) {
		logger.info("Constructing SequenceNumberFIFOLinkInit component.");

		subscribe(handleSend, fpl);
		subscribe(handleDataMessage, pl);

		self = event.getSelf();
		pi = event.getAllAddresses();

		for (Address p : pi) {
			lsn.put(p, 0);
			next.put(p, 1);
		}
	}

	private Handler<FplSend> handleSend = new Handler<FplSend>() {
		@Override
		public void handle(FplSend event) {
			Address q = event.getDestination();
			lsn.put(q, lsn.get(q) + 1);
//            logger.info("   handleSend {} {}", q, lsn.get(q));
			trigger(new Pp2pSend(q, new DataMessage(self, event.getDeliverEvent(), lsn.get(q))), pl);
		}
	};

	private Handler<DataMessage> handleDataMessage = new Handler<DataMessage>() {
		@Override
		public void handle(DataMessage event) {
			pending.add(event);
			boolean anyDelivered = true;
//            logger.info("   handleDataMessage {} {}", event.getSource(), event.getLsn());

//            for (DataMessage dm: pending
//                 ) {
//                logger.info("        {} {}", dm.getSource(), dm.getLsn());
//            }

			while (anyDelivered) {
				anyDelivered = false;
				for (int i = 0; i < pending.size(); i++) {
					DataMessage m = pending.get(i);
					Address q = m.getSource();
					if (m.getLsn() == next.get(q)) {
						next.put(q, next.get(q) + 1);
						pending.remove(i);
						trigger(m.getDeliverEvent(), fpl);
						anyDelivered = true;
					}
				}
			}
		}
	};
}
