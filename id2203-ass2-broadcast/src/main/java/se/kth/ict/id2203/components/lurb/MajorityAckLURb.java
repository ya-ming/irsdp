package se.kth.ict.id2203.components.lurb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.pa.broadcast.MajorityAckLURbMessage;
import se.kth.ict.id2203.ports.lurb.MajorityAckLURbBroadcast;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.lurb.MajorityAckLURbDeliver;
import se.kth.ict.id2203.ports.lurb.MajorityAckLoggedUniformReliableBroadcast;
import se.kth.ict.id2203.tools.ObjectToFile;
import se.kth.ict.id2203.tools.PrettyPrintingMap;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//Algorithm 3.8: Logged Majority-Ack Uniform Reliable Broadcast
//        Implements:
//            LoggedUniformReliableBroadcast, instance lurb.
//        Uses:
//            StubbornBestEffortBroadcast, instance sbeb.
//
//        upon event < lurb, Init > do
//            delivered := ∅;
//            pending := ∅;
//            forall m do ack[m] := ∅;
//            store(pending, delivered);
//
//        upon event < lurb, Recovery > do
//            retrieve(pending, delivered);
//            trigger < lurb, Deliver | delivered >;
//            forall (s,m) ∈ pending do
//                trigger < sbeb, Broadcast | [DATA, s,m] >;
//
//        upon event < lurb, Broadcast | m > do
//            pending := pending ∪ {(self,m)};
//            store(pending);
//            trigger < sbeb, Broadcast | [DATA, self,m] >;
//
//        upon event < sbeb, Deliver | p, [DATA, s,m] > do
//            if (s,m) ∈ pending then
//                pending := pending ∪ {(s,m)};
//                store(pending);
//                trigger < sbeb, Broadcast | [DATA, s,m] >;
//            if p ∈ ack[m] then
//                ack[m] := ack[m] ∪ {p};
//                if #(ack[m]) > N/2 ∧ (s,m) ∈ delivered then
//                    delivered := delivered ∪ {(s,m)};
//                    store(delivered);
//                    trigger < lurb, Deliver | delivered >;


public class MajorityAckLURb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(MajorityAckLURb.class);

    private Negative<MajorityAckLoggedUniformReliableBroadcast> lurb = provides(MajorityAckLoggedUniformReliableBroadcast.class);
    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);

    private final Address selfAddress;
    private final Set<Address> allAddresses;
    private int seqnum = 0;
    private HashSet<MajorityAckLURbDeliver> delivered;    // using MajorityAckLURbDeliver because that's the original message we want to broadcast
    private HashSet<MajorityAckLURbDeliver> pending;
    // Using HashMap to store all the acks for each message
    private HashMap<MajorityAckLURbDeliver, HashSet<Address>> ack;

    ObjectToFile objectToFile = new ObjectToFile();

    public MajorityAckLURb(MajorityAckLURbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();

        delivered = new HashSet<>();
        pending = new HashSet<>();

        ack = new HashMap<>();

        subscribe(handleMajorityAckLURbDataMessage, lurb);
        subscribe(handleMajorityAckLURbMessage, beb);
        subscribe(handleStart, control);
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("lurb, handleStart");

            Object temp = objectToFile.readObject(String.valueOf(selfAddress.getId()) + "_delivered");
            if (temp != null)
                delivered = (HashSet) temp;

            temp = objectToFile.readObject(String.valueOf(selfAddress.getId()) + "_pending");
            if (temp != null)
                pending = (HashSet) temp;

            if (delivered.isEmpty() == false) {
                logger.debug("MajorityAckLURb, init, delivered not empty");
                for (MajorityAckLURbDeliver deliver : delivered
                        ) {
                    logger.debug("MajorityAckLURb, init, delivered: " + deliver);
                    trigger(deliver, lurb);
                }
            }

            if (pending.isEmpty() == false) {
                logger.debug("MajorityAckLURb, init, pending not empty");
                for (MajorityAckLURbDeliver pending : pending
                        ) {
                    logger.debug("MajorityAckLURb, init, pending: " + pending);
                    MajorityAckLURbDataMessage message = new MajorityAckLURbDataMessage(selfAddress, pending, 0);
                    trigger(new BebBroadcast(message), beb);
                }
            }
        }
    };

    private Handler<MajorityAckLURbBroadcast> handleMajorityAckLURbDataMessage = new Handler<MajorityAckLURbBroadcast>() {
        @Override
        public void handle(MajorityAckLURbBroadcast event) {
            // Only turn on below log if test RB. Because AllAckURbMessage is not common for CRB test.
            logger.debug("MajorityAckLURb, handleMajorityAckLRbBroadcast " + ((MajorityAckLURbMessage) (event.getDeliverEvent())).getMessage() + " " + seqnum);
            seqnum++;
            MajorityAckLURbDataMessage message = new MajorityAckLURbDataMessage(selfAddress, event.getDeliverEvent(), seqnum);

            logger.debug("MajorityAckLURb, handleMajorityAckLRbBroadcast, pending: " + pending);
            pending.add(event.getDeliverEvent());
            objectToFile.writeObject(String.valueOf(selfAddress.getId()) + "_pending", pending);
            trigger(new BebBroadcast(message), beb);
        }
    };

    private Handler<MajorityAckLURbDataMessage> handleMajorityAckLURbMessage = new Handler<MajorityAckLURbDataMessage>() {
        @Override
        public void handle(MajorityAckLURbDataMessage event) {
            MajorityAckLURbDeliver deliverMsg = event.getDeliverEvent();
            // Only turn on below log if test RB. Because AllAckURbMessage is not common for CRB test.
            logger.debug("MajorityAckLURb, handleMajorityAckLURbDataMessage " + event.getSource() + " " + ((MajorityAckLURbMessage) (event.getDeliverEvent())).getMessage() + " " + seqnum);
            if (ack.containsKey(deliverMsg) == false) {
                ack.put(deliverMsg, new HashSet<>());
            }
            logger.debug("MajorityAckLURb, handleMajorityAckLURbDataMessage, ack: ");
            logger.debug((new PrettyPrintingMap<MajorityAckLURbDeliver, HashSet<Address>>(ack)).toString());


            ack.get(deliverMsg).add(event.getSource());

            // pending doesn't contain this event means this event comes from the remote site
            logger.debug("MajorityAckLURb, handleMajorityAckLURbDataMessage, pending: " + pending);
            if (pending.contains(deliverMsg) == false) {
                MajorityAckLURbDataMessage message = new MajorityAckLURbDataMessage(selfAddress, event.getDeliverEvent(), seqnum);
                pending.add(deliverMsg);
                objectToFile.writeObject(String.valueOf(selfAddress.getId()) + "_pending", pending);
                trigger(new BebBroadcast(message), beb);
            }

            logger.debug("MajorityAckLURb, handleMajorityAckLURbDataMessage, delivered: " + delivered);
            if (pending.contains(deliverMsg) &&
                    // Majority-Ack
                    (ack.get(deliverMsg).size() > (allAddresses.size() / 2)) &&
                    delivered.contains(deliverMsg) == false) {
                logger.debug("MajorityAckLURb, handleMajorityAckLURbDataMessage, deliver");
                delivered.add(deliverMsg);
                objectToFile.writeObject(String.valueOf(selfAddress.getId()) + "_delivered", delivered);
                trigger(event.getDeliverEvent(), lurb);  // AllAckURbMessage is the deliverEvent()
            }
        }
    };
}
