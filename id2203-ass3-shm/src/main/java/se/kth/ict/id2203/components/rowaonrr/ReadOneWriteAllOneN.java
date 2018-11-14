package se.kth.ict.id2203.components.rowaonrr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.rowaonrr.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

public class ReadOneWriteAllOneN extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadOneWriteAllOneN.class);

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private Negative<ReadOneWriteAllOneNRegularRegister> rowaonrr = provides(ReadOneWriteAllOneNRegularRegister.class);

    private Address self;
    private Set<Address> all;

    private Object val = null;
    private Set<Address> correct;
    private Set<Address> writeset;



	public ReadOneWriteAllOneN(ReadOneWriteAllOneNInit event) {
        this.self = event.getSelfAddress();
        this.all = event.getAllAddresses();

        val = null;
        correct = new HashSet<>(all);
        writeset = new HashSet<>();

        subscribe(handleReadRequest, rowaonrr);
        subscribe(handleWriteRequest, rowaonrr);

        subscribe(handleWriteRowaonrrMessage, beb);
        subscribe(handleAckRowaonrrMessage, pp2p);

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
            logger.debug("ReadOneWriteAllOneN, ReadRequest " + event);

            trigger(new ReadResponse(val), rowaonrr);
        }
    };

    private Handler<WriteRequest> handleWriteRequest = new Handler<WriteRequest>() {
        @Override
        public void handle(WriteRequest event) {
            logger.debug("ReadOneWriteAllOneN, handleWriteRequest " + event + " val: " + event.getValue());

            trigger(new BebBroadcast(new WriteRowaonrrMessage(self, event.getValue())), beb);
        }
    };

    private Handler<WriteRowaonrrMessage> handleWriteRowaonrrMessage = new Handler<WriteRowaonrrMessage>() {
        @Override
        public void handle(WriteRowaonrrMessage event) {
            logger.debug("ReadOneWriteAllOneN, handleWriteRowaonrrMessage" +
                    " source " + event.getSource() +
                    " val " + val + " e.val " + event.getVal()
            );

            val = event.getVal();

            trigger(new Pp2pSend(event.getSource(), new AckRowaonrrMessage(self)), pp2p);
        }
    };

    private Handler<AckRowaonrrMessage> handleAckRowaonrrMessage = new Handler<AckRowaonrrMessage>() {
        @Override
        public void handle(AckRowaonrrMessage event) {
            logger.debug("ReadOneWriteAllOneN, handleAckRowaonrrMessage " + event.getSource());

            writeset.add(event.getSource());
//            logger.debug("ReadOneWriteAllOneN, handleAckRowaonrrMessage if(writeset.containsAll(correct)) " +
//                    "\n    " + writeset + "\n    " + correct);
            if (writeset.containsAll(correct)) {
                writeset.clear();
                trigger(new WriteResponse(), rowaonrr);
            }
        }
    };
}
