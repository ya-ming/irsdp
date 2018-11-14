package se.kth.ict.id2203.ports.ac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class AcComponent extends ComponentDefinition {

	private Positive<PerfectPointToPointLink> pp2pPort = requires(PerfectPointToPointLink.class);
	private Positive<BestEffortBroadcast> bebPort = requires(BestEffortBroadcast.class);
	private Negative<AcPort> acPort = provides(AcPort.class);

	private static final Logger logger = LoggerFactory.getLogger(AcComponent.class);

	private Set<Integer> seenIds = new HashSet<Integer>();
	private Map<Integer, Integer> tempValue = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> value = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> writeAck = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> rts = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> wts = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> tstamp = new HashMap<Integer, Integer>();
	private Map<Integer, Set<TupleTsVal>> readSet = new HashMap<Integer, Set<TupleTsVal>>();

	private int majority;
	private int rank;
	private int numberOfNodes;
	private Address self;

	public AcComponent(AcInit event) {
        majority = 1 + (event.getNumberOfNodes() / 2);
        rank = event.getNodeId();
        numberOfNodes = event.getNumberOfNodes();
        self = event.getSelf();
        logger.info("Started");

		subscribe(handlerAcPropose, acPort);
		subscribe(handlerRead, bebPort);
		subscribe(handlerNack, pp2pPort);
		subscribe(handlerReadAck, pp2pPort);
		subscribe(handlerWrite, bebPort);
		subscribe(handlerWriteAck, pp2pPort);
	}

	private void initInstance(int consensusId) {
		if (!seenIds.contains(consensusId)) {
			tempValue.put(consensusId, null);
			value.put(consensusId, null);
			writeAck.put(consensusId, 0);
			rts.put(consensusId, 0);
			wts.put(consensusId, 0);
			tstamp.put(consensusId, rank);
			readSet.put(consensusId, new HashSet<TupleTsVal>());
			seenIds.add(consensusId);
		}
	}
	
	Handler<AcPropose> handlerAcPropose = new Handler<AcPropose>() {
		public void handle(AcPropose event) {
			int id = event.getConsensusId();
			initInstance(id);
			int oldTstamp = tstamp.get(id);
			tstamp.put(id,oldTstamp+numberOfNodes);
			tempValue.put(id, event.getValue());
			logger.info("Proposer: sending READ messages");
			trigger(new BebBroadcast(new Read(self, id, tstamp.get(id))), bebPort);
		}
	};

	Handler<Read> handlerRead = new Handler<Read>() {
		public void handle(Read event) {
			int id = event.getConsensusId();
			int ts = event.getTstamp();
			initInstance(id);
			logger.info("Acceptor: rcv READ message");
			if (rts.get(id) >= ts || wts.get(id) >= ts) {
				trigger(new Pp2pSend(event.getSource(), new Nack(self, id)), pp2pPort);
			} else {
				rts.put(id, ts);
				trigger(new Pp2pSend(event.getSource(), new ReadAck(self, id, wts.get(id),
						value.get(id),ts)),pp2pPort);
			}
		}
	};

	Handler<Nack> handlerNack = new Handler<Nack>() {
		public void handle(Nack event) {
			int id = event.getConsensusId();
			readSet.put(id, new HashSet<TupleTsVal>());
			writeAck.put(id, 0);
			logger.info("Proposer: rcv NACK message ");
			trigger(new AcDecide(id, null), acPort);
		}
	};

	Handler<ReadAck> handlerReadAck = new Handler<ReadAck>() {
		public void handle(ReadAck event) {
			int id = event.getConsensusId();
			int ts = event.getTimestamp();
			int sentts = event.getSentTimestamp();
			Integer v = event.getValue();
			logger.info("Proposer: rcv ReadACK message");

			if (sentts == tstamp.get(id)) {
				readSet.get(id).add(new TupleTsVal(ts, v));
				if (readSet.get(id).size() == majority) {
					// get max
					TupleTsVal max = null;
					for (TupleTsVal tuple : readSet.get(id)) {
						if (max == null || tuple.gt(max)) {
							max = tuple;
						}
					}
					if (max.getValue() != null) {
						tempValue.put(id, max.getValue());
					}
					logger.info("Proposer: sending WRITE message");
					trigger(new BebBroadcast(new Write(self, id, tstamp.get(id), tempValue.get(id))), bebPort);
				}
			}
		}
	};

	Handler<Write> handlerWrite = new Handler<Write>() {
		public void handle(Write event) {
			int id = event.getConsensusId();
			int ts = event.getTs();
			Integer v = event.getValue();
			logger.info("Acceptor: rcv WRITE message");

			initInstance(id);
			if (rts.get(id) > ts || wts.get(id) > ts) {
				trigger(new Pp2pSend(event.getSource(), new Nack(self, id)), pp2pPort);
			} else {
				value.put(id, v);
				wts.put(id, ts);
				trigger(new Pp2pSend(event.getSource(), new WriteAck(self, id, ts)), pp2pPort);
			}
		}
	};

	Handler<WriteAck> handlerWriteAck = new Handler<WriteAck>() {
		public void handle(WriteAck event) {
			int id = event.getConsensusId();
			int sentts = event.getSentTimestamp();
			logger.info("Proposer: rcv WriteACK message");

			if (sentts == tstamp.get(id)) {
				writeAck.put(id, writeAck.get(id) + 1);
				if (writeAck.get(id) == majority) {
					readSet.put(id, new HashSet<TupleTsVal>());
					writeAck.put(id, 0);
					trigger(new AcDecide(id, tempValue.get(id)), acPort);
				}
			}
		}
	};

}
