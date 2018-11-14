/**
 * This file is part of the ID2203 course assignments kit.
 * <p>
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203.components.pp2p;

import java.util.ArrayList;
import java.util.Random;

import se.kth.ict.id2203.appPerfect.Pp2pMessage;
import se.kth.ict.id2203.appStubborn.Sp2pMessage;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.sp2p.Sp2pSend;
import se.kth.ict.id2203.ports.sp2p.StubbornPointToPointLink;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

//Algorithm 2.2: Eliminate Duplicates
//        Implements:
//          PerfectPointToPointLinks, instance pl.
//
//        Uses:
//          StubbornPointToPointLinks, instance sl.
//
//        upon event < pl, Init > do
//          delivered := ∅;
//
//        upon event < pl, Send | q, m > do
//          trigger < sl, Send | q, m >;
//
//        upon event < sl, Deliver | p, m > do
//          if m ∈ delivered then
//              delivered := delivered ∪ {m};
//              trigger < pl, Deliver | p, m >;

public final class PerfectLink extends ComponentDefinition {

    private Negative<PerfectPointToPointLink> pp2p = provides(PerfectPointToPointLink.class);
    private Positive<StubbornPointToPointLink> sp2p = requires(StubbornPointToPointLink.class);

    private Address self;
    private Topology topology;
    private long sigma;

    private Random random = new Random(0);

    ArrayList<String> delivered_list = new ArrayList<String>();

    public PerfectLink(PerfectLinkInit event) {
        subscribe(handlePp2pSend, pp2p);
        subscribe(handleSp2pMessage, sp2p);

        topology = event.getTopology();
        sigma = event.getSigma();
        self = topology.getSelfAddress();
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

            trigger(new Sp2pSend(destination, new Sp2pMessage(self, ((Pp2pMessage) event.getDeliverEvent()).getMessage())), sp2p);
        }
    };

    private Handler<Sp2pMessage> handleSp2pMessage = new Handler<Sp2pMessage>() {
        @Override
        public void handle(Sp2pMessage event) {
            if (delivered_list.contains(event.getMessage()) == false){
                delivered_list.add(event.getMessage());
                trigger(new Pp2pMessage(self, event.getMessage()), pp2p);
            }

        }
    };
}
