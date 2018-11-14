package se.kth.ict.id2203.components.epb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.pa.broadcast.EagerPbMessage;
import se.kth.ict.id2203.ports.epb.EPbBroadcast;
import se.kth.ict.id2203.ports.epb.EPbDeliver;
import se.kth.ict.id2203.ports.epb.EagerProbabilisticBroadcast;
import se.kth.ict.id2203.ports.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.ports.flp2p.Flp2pSend;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;

import java.util.*;

//Algorithm 3.9: Eager Probabilistic Broadcast
//        Implements:
//            ProbabilisticBroadcast, instance pb.
//
//        Uses:
//            FairLossPointToPointLinks, instance fll.
//
//        upon event < pb, Init > do
//            delivered := ∅;
//
//        procedure gossip(msg) is
//            forall t ∈ picktargets(k) do trigger < fll, Send | t, msg >;
//
//        upon event < pb, Broadcast | m > do
//            delivered := delivered ∪ {m};
//            trigger < pb, Deliver | self, m >;
//            gossip([GOSSIP, self,m,Round]);
//
//        upon event < fll, Deliver | p, [GOSSIP, s,m, r] > do
//            if m ∈ delivered then
//                delivered := delivered ∪ {m};
//                trigger < pb, Deliver | s, m >;
//            if r > 1 then gossip([GOSSIP, s,m, r − 1]);
//
//        function picktargets (k) returns set of processes is
//            targets := ∅;
//            while #(targets) < k do
//                candidate := random(Π \ {self});
//                if candidate ∈ targets then
//                    targets := targets ∪ {candidate};
//            return targets;

public class EagerPb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(EagerPb.class);

    private Negative<EagerProbabilisticBroadcast> epb = provides(EagerProbabilisticBroadcast.class);
    private Positive<FairLossPointToPointLink> fl = requires(FairLossPointToPointLink.class);

    private final Address selfAddress;
    private final Set<Address> allAddresses;
    private final HashMap<Integer, Address> PI;
    private int seqnum = 0;
    private int Round = 3;
    private int k = 2;
    private HashSet<EPbDeliver> delivered;    // using EPbDeliver because that's the original message we want to broadcast

    public EagerPb(EagerPbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();

        PI = new HashMap<>();
        for (Address address: allAddresses
             ) {
            PI.put(address.getId(), address);
        }

        logger.debug("EagerPb, PI: " + PI);

        delivered = new HashSet<>();

        subscribe(handleEPbBroadcast, epb);
        subscribe(handleEagerPbMessage, fl);
        subscribe(handleStart, control);
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("EagerPb, handleStart");
        }
    };

    HashSet<Address> picktargets() {
        HashSet<Address> targets = new HashSet<>();
        Random rand = new Random();

        logger.debug("EagerPb, picktargets");

        while (targets.size() < k) {
            int r = rand.nextInt(allAddresses.size()) + 1;
            logger.debug("EagerPb, picktargets r = " + r);
            Address candidate = PI.get(r);
            if (candidate != selfAddress && targets.contains(candidate) == false) {
                targets.add(candidate);
            }
        }
        return targets;
    }

    private void gossip(EPbDataMessage message) {
        for (Address address: picktargets()
             ) {
            trigger(new Flp2pSend(address, message), fl);
        }
    }

    private Handler<EPbBroadcast> handleEPbBroadcast = new Handler<EPbBroadcast>() {
        @Override
        public void handle(EPbBroadcast event) {
            // Only turn on below log if test RB. Because EagerPbMessage is not common for CRB test.
            logger.debug("EagerPb, handleEPbBroadcast " + ((EagerPbMessage) (event.getDeliverEvent())).getMessage() + " " + seqnum);
            seqnum++;
            delivered.add(event.getDeliverEvent());
            trigger(event.getDeliverEvent(), epb);  // EagerPbMessage is the deliverEvent()


            EPbDataMessage message = new EPbDataMessage(selfAddress, event.getDeliverEvent(), Round, seqnum);
            gossip(message);
        }
    };

    private Handler<EPbDataMessage> handleEagerPbMessage = new Handler<EPbDataMessage>() {
        @Override
        public void handle(EPbDataMessage event) {
            EPbDeliver deliverMsg = event.getDeliverEvent();
            // Only turn on below log if test RB. Because EagerPbMessage is not common for CRB test.
            logger.debug("EagerPb, handleMajorityAckLRbDataMessage " + event.getSource() + " " +
                    ((EagerPbMessage) (event.getDeliverEvent())).getMessage() + " " +
                    "Round = " + event.getRound() + " " +
                    seqnum);

            if (delivered.contains(deliverMsg) == false) {
                delivered.add(deliverMsg);

                trigger(event.getDeliverEvent(), epb);
            }

            if (event.getRound() > 1) {
                gossip(new EPbDataMessage(selfAddress, event.getDeliverEvent(), event.getRound() - 1, event.getSeqnum()));
            }
        }
    };
}
