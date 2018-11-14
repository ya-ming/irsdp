package se.kth.ict.id2203.components.lpp2p;

import se.kth.ict.id2203.appLoggedPerfect.Lpp2pMessage;
import se.kth.ict.id2203.appStubborn.Sp2pMessage;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.sp2p.Sp2pSend;
import se.kth.ict.id2203.ports.sp2p.StubbornPointToPointLink;
import se.kth.ict.id2203.tools.ObjectToFile;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

import java.util.ArrayList;
import java.util.Random;

//Algorithm 2.3: Log Delivered
//        Implements:
//          LoggedPerfectPointToPointLinks, instance lpl.
//
//        Uses:
//          StubbornPointToPointLinks, instance sl.
//
//        upon event < lpl, Init > do
//          delivered := ∅;
//          store(delivered);
//
//        upon event < lpl, Recovery > do
//          retrieve(delivered);
//          trigger < lpl, Deliver | delivered >;
//
//        upon event < lpl, Send | q, m > do
//          trigger < sl, Send | q, m >;
//
//        upon event < sl, Deliver | p, m > do
//          if not exists (p,m) ∈ delivered such that m = m then
//              delivered := delivered ∪ {(p,m)};
//              store(delivered);
//              trigger < lpl, Deliver | delivered >;

public final class LoggedPerfectLink extends ComponentDefinition {

    private Negative<PerfectPointToPointLink> pp2p = provides(PerfectPointToPointLink.class);
    private Positive<StubbornPointToPointLink> sp2p = requires(StubbornPointToPointLink.class);

    private Address self;
    private Topology topology;
    private long sigma;

    private Random random = new Random(0);
    ObjectToFile objectToFile = new ObjectToFile();

    ArrayList<String> delivered_list = new ArrayList<String>();

    public LoggedPerfectLink(LoggedPerfectLinkInit event) {
        subscribe(handlePp2pSend, pp2p);
        subscribe(handleSp2pMessage, sp2p);

        topology = event.getTopology();
        sigma = event.getSigma();
        self = topology.getSelfAddress();

        ArrayList<String> list = objectToFile.readObject(String.valueOf(self.getId()));
        if (list != null)
            delivered_list = list;
    }

    private Handler<Pp2pSend> handlePp2pSend = new Handler<Pp2pSend>() {
        @Override
        public void handle(Pp2pSend event) {
            Address destination = event.getDestination();

            if (destination.equals(self)) {
                // deliver locally
                Pp2pDeliver deliverEvent = event.getDeliverEvent();
                trigger(deliverEvent, pp2p);
                return;
            }

            trigger(new Sp2pSend(destination, new Sp2pMessage(self, ((Lpp2pMessage) event.getDeliverEvent()).getMessage())), sp2p);
        }
    };

    private Handler<Sp2pMessage> handleSp2pMessage = new Handler<Sp2pMessage>() {
        @Override
        public void handle(Sp2pMessage event) {
            if (delivered_list.contains(event.getMessage()) == false){
                delivered_list.add(event.getMessage());
                trigger(new Lpp2pMessage(self, event.getMessage()), pp2p);
                objectToFile.writeObject(String.valueOf(self.getId()), delivered_list);
            }

        }
    };
}
