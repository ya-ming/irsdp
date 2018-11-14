/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203.components.beb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;

import java.util.Set;

public class BasicBroadcast extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(BasicBroadcast.class);

	private Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private final Address self;
	private final Set<Address> pi;

	public BasicBroadcast(BasicBroadcastInit init) {
		self = init.getSelfAddress();
		pi = init.getAllAddresses();

        subscribe(handleStart, control);
		subscribe(handleBebBoradcast, beb);
		subscribe(handleBebDataMessage, pp2p);

        printPi();
	}

    private void printPi() {
        logger.debug("Alive:");
        for (Address a:pi
                ) {
            logger.debug(a.toString());
        }
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("BasicBroadcast, handleStart");
        }
    };

    private Handler<BebBroadcast> handleBebBoradcast = new Handler<BebBroadcast>() {
        @Override
        public void handle(BebBroadcast event) {
//            logger.debug("BasicBroadcast, handleBebBoradcast");
            for(Address q: pi) {
//                logger.debug("BasicBroadcast, handleBebBoradcast, target {}", q);
                trigger(new Pp2pSend(q, new BebDataMessage(q, event.getDeliverEvent())), pp2p);
            }
        }
    };

    private Handler<BebDataMessage> handleBebDataMessage = new Handler<BebDataMessage>() {
        @Override
        public void handle(BebDataMessage event) {
//            logger.debug("BasicBroadcast, handleDelayLinkMessage");
            trigger(event.getDeliverEvent(), beb);  // BebMessage is the deliverEvent()
        }
    };
}
