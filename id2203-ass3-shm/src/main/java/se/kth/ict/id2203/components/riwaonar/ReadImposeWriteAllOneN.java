package se.kth.ict.id2203.components.riwaonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.riwaonar.ReadImposeWriteAllOneNAtomicRegister;
import se.kth.ict.id2203.ports.rowaonrr.ReadRequest;
import se.kth.ict.id2203.ports.rowaonrr.ReadResponse;
import se.kth.ict.id2203.ports.rowaonrr.WriteRequest;
import se.kth.ict.id2203.ports.rowaonrr.WriteResponse;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Set;

public class ReadImposeWriteAllOneN extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteAllOneN.class);

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private Negative<ReadImposeWriteAllOneNAtomicRegister> riwaonar = provides(ReadImposeWriteAllOneNAtomicRegister.class);

    private Address self;
    private Set<Address> all;

    private Object val;
    private Integer ts;

    private Set<Address> correct;
    private Set<Address> writeset;

    private Object readval;
    private boolean reading;



	public ReadImposeWriteAllOneN(ReadImposeWriteAllOneNInit event) {
        this.self = event.getSelfAddress();
        this.all = event.getAllAddresses();

        val = null;
        ts = 0;
        correct = new HashSet<>(all);
        writeset = new HashSet<>();
        readval = null;
        reading = false;

        subscribe(handleReadRequest, riwaonar);
        subscribe(handleWriteRequest, riwaonar);

        subscribe(handleWriteRiwaonarMessage, beb);
        subscribe(handleAckRiwaonarMessage, pp2p);

        subscribe(handleCrash, pfd);
	}

	private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            correct.remove(event.getSource());
        }
    };

    private Handler<ReadRequest> handleReadRequest = new Handler<ReadRequest>() {
        @Override
        public void handle(ReadRequest event) {
            logger.debug("ReadImposeWriteAllOneN, ReadRequest " + event +
            " ts " + ts + " val " + val
            );

            reading = true;
            readval = val;
            trigger(new BebBroadcast(new WriteRiwaonarMessage(self, ts, val)), beb);
        }
    };

    private Handler<WriteRequest> handleWriteRequest = new Handler<WriteRequest>() {
        @Override
        public void handle(WriteRequest event) {
            logger.debug("ReadImposeWriteAllOneN, handleWriteRequest " + event +
                " ts " + ts + " v " + event.getValue()
            );

            trigger(new BebBroadcast(new WriteRiwaonarMessage(self, ts + 1, event.getValue())), beb);
        }
    };

    private Handler<WriteRiwaonarMessage> handleWriteRiwaonarMessage = new Handler<WriteRiwaonarMessage>() {
        @Override
        public void handle(WriteRiwaonarMessage event) {
            logger.debug("ReadImposeWriteAllOneN, handleWriteRiwaonarMessage" +
                    " source " + event.getSource() +
                    " val " + val + " v' " + event.getVal() +
                    " ts " + ts + " ts' " + event.getTs()
            );

            if (event.getTs() > ts) {
                ts = event.getTs();
                val = event.getVal();
            }

            trigger(new Pp2pSend(event.getSource(), new AckRiwaonarMessage(self)), pp2p);
        }
    };

    private Handler<AckRiwaonarMessage> handleAckRiwaonarMessage = new Handler<AckRiwaonarMessage>() {
        @Override
        public void handle(AckRiwaonarMessage event) {
            logger.debug("ReadImposeWriteAllOneN, handleAckRiwaonarMessage " + event.getSource());

            writeset.add(event.getSource());
//            logger.debug("ReadImposeWriteAllOneN, handleAckRiwaonarMessage if(writeset.containsAll(correct)) " +
//                    "\n    " + writeset + "\n    " + correct);
            if (writeset.containsAll(correct)) {
                writeset.clear();

                if (reading) {
                    reading = false;
                    trigger(new ReadResponse(val), riwaonar);
                }
                else {
                    trigger(new WriteResponse(), riwaonar);

                }
            }
        }
    };
}
