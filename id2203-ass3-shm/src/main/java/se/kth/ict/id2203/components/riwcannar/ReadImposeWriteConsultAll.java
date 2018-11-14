package se.kth.ict.id2203.components.riwcannar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.riwcannar.ReadImposeWriteConsultAllNNAtomicRegister;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.riwcmnnar.*;
import se.kth.ict.id2203.ports.riwmonar.ReadImposeWriteMajorityOneNAtomicRegister;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import javax.lang.model.element.ElementVisitor;
import java.util.*;

public class ReadImposeWriteConsultAll extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteConsultAll.class);

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Negative<ReadImposeWriteConsultAllNNAtomicRegister> ar = provides(ReadImposeWriteConsultAllNNAtomicRegister.class);

    private Address self;
    private Set<Address> all;
    private Set<Address> correct;
    private Set<Address> writeset;

    private Integer ts;
    private Integer wr;
    private Object val;
    private Object readval;
    private boolean reading;

	public ReadImposeWriteConsultAll(ReadImposeWriteConsultAllInit event) {
        this.self = event.getSelfAddress();
        this.all = event.getAllAddresses();


        this.ts = 0;    // timestamp
        this.wr = 0;    // writer identifier, if TSs are equal, use this to break the tie
        this.val = 0;   // value of the register
        this.readval = null;   // value read
        this.reading = false;   // in reading or not

        correct = new HashSet<>(all);
        writeset = new HashSet<>();

        subscribe(handleArReadRequest, ar);

        subscribe(handleArWriteRequest, ar);
        subscribe(handleWriteArMessage, beb);

        subscribe(handleAckMessage, pp2p);

        subscribe(handleCrash, pfd);
	}

    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            correct.remove(event.getSource());
        }
    };

    private Handler<ArReadRequest> handleArReadRequest = new Handler<ArReadRequest>() {
        @Override
        public void handle(ArReadRequest event) {
            logger.debug("ReadImposeWriteConsultAll, ArReadRequest ");

            reading = true;
            readval = val;
            trigger(new BebBroadcast(new WriteArMessage(self, ts, wr, val)), beb);
        }
    };

    private Handler<ArWriteRequest> handleArWriteRequest = new Handler<ArWriteRequest>() {
        @Override
        public void handle(ArWriteRequest event) {
            logger.debug("ReadImposeWriteConsultAll, handleArWriteRequest ");

            trigger(new BebBroadcast(new WriteArMessage(self, ts + 1, self.getId(), event.getValue())), beb);
        }
    };

    private Handler<WriteArMessage> handleWriteArMessage = new Handler<WriteArMessage>() {
        @Override
        public void handle(WriteArMessage event) {
            logger.debug("ReadImposeWriteConsultAll, handleWriteArMessage" +
                    " ts " + ts + " e.ts " + event.getTs() +
                    " wr " + wr + " e.wr " + event.getWr() +
                    " val " + val + " e.val " + event.getVal()
            );


            if ((event.getTs() > ts) || ((event.getTs() == ts) && event.getWr() > wr)) {
                ts = event.getTs();
                wr = event.getWr();
                val = event.getVal();
            }

            trigger(new Pp2pSend(event.getSource(), new AckMessage(self)), pp2p);
        }
    };

    private Handler<AckMessage> handleAckMessage = new Handler<AckMessage>() {
        @Override
        public void handle(AckMessage event) {
            logger.debug("ReadImposeWriteConsultAll, handleAckMessage ");

            writeset.add(event.getSource());
            if (writeset.containsAll(correct)){
                writeset.clear();
                if (reading) {
                    reading = false;
                    trigger(new ArReadResponse(readval), ar);
                } else {
                    trigger(new ArWriteResponse(), ar);
                }

            }
        }
    };
}
