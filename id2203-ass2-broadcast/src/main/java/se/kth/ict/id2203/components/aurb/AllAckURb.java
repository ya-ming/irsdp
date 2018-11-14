package se.kth.ict.id2203.components.aurb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.pa.broadcast.AllAckURbMessage;
import se.kth.ict.id2203.ports.aurb.AllAckURbDeliver;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.aurb.AllAckURbBroadcast;
import se.kth.ict.id2203.ports.aurb.AllAckUniformReliableBroadcast;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.tools.PrettyPrintingMap;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

//Algorithm 3.4: All-Ack Uniform Reliable Broadcast
//        Implements:
//            UniformReliableBroadcast, instance urb.
//
//        Uses:
//            BestEffortBroadcast, instance beb.
//            PerfectFailureDetector, instance P.
//
//        upon event < urb, Init > do
//            delivered := ∅;
//            pending := ∅;
//            correct := Π;
//            forall m do ack[m] := ∅;
//
//        upon event < urb, Broadcast | m > do
//            pending := pending ∪ {(self,m)};
//            trigger < beb, Broadcast | [DATA, self,m] >;
//
//        upon event < beb, Deliver | p, [DATA, s,m] > do
//            ack[m] := ack[m] ∪ {p};
//            if (s,m) ∈ pending then
//                pending := pending ∪ {(s,m)};
//                trigger < beb, Broadcast | [DATA, s,m] >;
//
//        upon event < P, Crash | p > do
//            correct := correct \ {p};
//
//        function candeliver (m) returns Boolean is
//            return (correct ⊆ ack[m]);
//
//        upon exists (s,m) ∈ pending such that candeliver(m) ∧ m ∈ delivered do
//            delivered := delivered ∪ {m};
//            trigger < urb, Deliver | s, m >;


public class AllAckURb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(AllAckURb.class);

    private Negative<AllAckUniformReliableBroadcast> allackurb = provides(AllAckUniformReliableBroadcast.class);
    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private final Address selfAddress;
    private final Set<Address> allAddresses;
    private int seqnum = 0;
    private HashSet<AllAckURbDeliver> delivered;    // using AllAckURbDeliver because that's the original message we want to broadcast
    private HashSet<AllAckURbDeliver> pending;
    private HashSet<Address> correct;
    // Using HashMap to store all the acks for each message
    private HashMap<AllAckURbDeliver, HashSet<Address>> ack;

    public AllAckURb(AllAckURbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();

        delivered = new HashSet<>();
        pending = new HashSet<>();
        correct = new HashSet<>();
        correct.addAll(allAddresses);

        ack = new HashMap<>();

        subscribe(handleAllAckURbBroadcast, allackurb);
        subscribe(handleAllAckURbDataMessage, beb);
        subscribe(handleCrash, pfd);
    }

    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            logger.info("AllAckURb, handleCrash Process {} crashed", event.getSource());
            correct.remove(event.getSource());
        }
    };

    private Handler<AllAckURbBroadcast> handleAllAckURbBroadcast = new Handler<AllAckURbBroadcast>() {
        @Override
        public void handle(AllAckURbBroadcast event) {
            // Only turn on below log if test RB. Because AllAckURbMessage is not common for CRB test.
            logger.debug("AllAckURb, handleAllAckRbBroadcast " + ((AllAckURbMessage) (event.getDeliverEvent())).getMessage() + " " + seqnum);
            seqnum++;
            AllAckURbDataMessage message = new AllAckURbDataMessage(selfAddress, event.getDeliverEvent(), seqnum);

            logger.debug("AllAckURb, handleAllAckRbBroadcast, pending: " + pending);
            pending.add(event.getDeliverEvent());
            trigger(new BebBroadcast(message), beb);
        }
    };

    private Handler<AllAckURbDataMessage> handleAllAckURbDataMessage = new Handler<AllAckURbDataMessage>() {
        @Override
        public void handle(AllAckURbDataMessage event) {
            AllAckURbDeliver deliverMsg = event.getDeliverEvent();
            // Only turn on below log if test RB. Because AllAckURbMessage is not common for CRB test.
            logger.debug("AllAckURb, handleAllAckRbDataMessage " + event.getSource() + " " + ((AllAckURbMessage) (event.getDeliverEvent())).getMessage() + " " + seqnum);
            if (ack.containsKey(deliverMsg) == false) {
                ack.put(deliverMsg, new HashSet<>());
            }
            logger.debug("AllAckURb, handleAllAckRbDataMessage, ack: ");
            logger.debug((new PrettyPrintingMap<AllAckURbDeliver, HashSet<Address>>(ack)).toString());


            ack.get(deliverMsg).add(event.getSource());

            // pending doesn't contain this event means this event comes from the remote site
            logger.debug("AllAckURb, handleAllAckRbDataMessage, pending: " + pending);
            if (pending.contains(deliverMsg) == false) {
                AllAckURbDataMessage message = new AllAckURbDataMessage(selfAddress, event.getDeliverEvent(), seqnum);
                pending.add(deliverMsg);
                trigger(new BebBroadcast(message), beb);
            }

            logger.debug("AllAckURb, handleAllAckRbDataMessage, delivered: " + delivered);
            if (pending.contains(deliverMsg) &&
                    // All-Ack
                    ack.get(deliverMsg).containsAll(correct) &&
                    // Majority-Ack
//                    (ack.get(deliverMsg).size() > (allAddresses.size() / 2)) &&
                    delivered.contains(deliverMsg) == false) {
                logger.debug("AllAckURb, handleAllAckRbDataMessage, deliver");
                delivered.add(deliverMsg);
                trigger(event.getDeliverEvent(), allackurb);  // AllAckURbMessage is the deliverEvent()
            }
        }
    };

    static void printSet(Set set){
        for (Object o :set
             ) {
            logger.debug("AllAckUrb, printSet" + o);
        }

    }
}
