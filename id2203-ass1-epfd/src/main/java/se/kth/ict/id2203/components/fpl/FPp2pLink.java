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
package se.kth.ict.id2203.components.fpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.app.fpl.FPp2pMessage;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.fpl.FPp2pSend;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.*;

import java.util.HashMap;
import java.util.HashSet;

//Algorithm 2.11: Sequence Number
//		Implements:
//			FIFOPerfectPointToPointLinks, instance fpl.
//
//		Uses:
//			PerfectPointToPointLinks, instance pl.
//
//		upon event < fpl, Init > do
//			forall p ∈ Π do
//				lsn[p] := 0;
//				next[p] := 1;
//
//		upon event < fpl, Send | q,m > do
//			lsn[q] := lsn[q] + 1;
//			trigger < pl, Send | q, (m, lsn[q]) >;
//
//		upon event < pl, Deliver | p, (m, sn) > do
//			pending := pending ∪ {(p,m, sn)};
//			while exists (q, n, sn) ∈ pending such that sn = next[q] do
//				next[q] := next[q] + 1;
//				pending := pending \ {(q, n, sn)};
//				trigger < fpl, Deliver | q, n >;

public final class FPp2pLink extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(FPp2pLink.class);

	private Negative<FIFOPerfectPointToPointLink> fpl = provides(FIFOPerfectPointToPointLink.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<NetworkControl> networkControl = requires(NetworkControl.class);

	private Address self;
	private HashSet<Address> all;
	private Topology topology;

	private HashSet<FPp2pDataMessage> pending;
	private HashMap<Address, Integer> lsn;
	private HashMap<Address, Integer> next;


	public FPp2pLink(FPp2pLinkInit event) {
		subscribe(handleFPp2pSend, fpl);
		subscribe(handleFPp2pDataMessage, pp2p);
		subscribe(handleNetworkException, networkControl);
		subscribe(handleNetworkConnectionRefused, networkControl);

		topology = event.getTopology();
		self = topology.getSelfAddress();
		all = new HashSet<>(event.getTopology().getAllAddresses());

		lsn = new HashMap<>();
		next = new HashMap<>();

		for (Address p:all
			 ) {
			lsn.put(p, 0);
			next.put(p, 1);
		}

		pending = new HashSet<>();
	}

	private void printMap(String name, HashMap map){
		logger.debug("FPP2P printMap " + name);
		for (Object key:map.keySet()
			 ) {
			logger.debug("        Key " + key + " Value " + map.get(key));
		}
	}

	private void printSet(HashSet set){
		logger.debug("FPP2P printSet ");
		for (Object item:set
				) {
			logger.debug("        Value " + item);
		}
	}

	private Handler<NetworkException> handleNetworkException = new Handler<NetworkException>() {
		@Override
		public void handle(NetworkException event) {
			logger.debug("FPP2P handleNetworkException " + event.getRemoteAddress());
			if (event.getRemoteAddress() != null) {
				int port = event.getRemoteAddress().getPort();
				for (Address p:all
						) {
					if (p.getPort() == port) {
						logger.debug("      handleNetworkException, reset lsn and next for " + p);
						lsn.put(p, 0);
						next.put(p, 1);
					}
				}
			}
		}
	};

	private Handler<NetworkConnectionRefused> handleNetworkConnectionRefused = new Handler<NetworkConnectionRefused>() {
		@Override
		public void handle(NetworkConnectionRefused event) {
			logger.debug("FPP2P handleNetworkConnectionRefused " + event.getRemoteAddress());
			if (event.getRemoteAddress() != null) {
				int port = event.getRemoteAddress().getPort();
				for (Address p:all
						) {
					if (p.getPort() == port) {
						logger.debug("      handleNetworkConnectionRefused, reset lsn and next for " + p);
						lsn.put(p, 0);
						next.put(p, 1);
					}
				}
			}
		}
	};

	private Handler<FPp2pSend> handleFPp2pSend = new Handler<FPp2pSend>() {
		@Override
		public void handle(FPp2pSend event) {
			Address destination = event.getDestination();

            logger.debug("FPP2P handleFPp2pSend " + destination);
			Integer n = lsn.get(event.getDestination());
			lsn.put(event.getDestination(), n + 1);

			trigger(new Pp2pSend(event.getDestination(),
					new FPp2pDataMessage(self, event.getDeliverEvent(), lsn.get(event.getDestination()))), pp2p);

		}
	};

	private Handler<FPp2pDataMessage> handleFPp2pDataMessage = new Handler<FPp2pDataMessage>() {
		@Override
		public void handle(FPp2pDataMessage event) {
			logger.info("FPP2P handleFPp2pDataMessage src: " +
					event.getSource());
//					+ " msg: " + ((FPp2pMessage)(event.getDeliverEvent())).getMessage());

			boolean retry;
			pending.add(event);

//			printMap("lsn", lsn);
//			printMap("next", next);
//			printSet(pending);


			do {
				retry = false;
				for (FPp2pDataMessage pendingMsg:pending
					 ) {
					if (pendingMsg.getLsn().equals(next.get(event.getSource()))) {
						next.put(event.getSource(), next.get(event.getSource()) + 1);
						pending.remove(pendingMsg);
						trigger(pendingMsg.getDeliverEvent(), fpl);
						retry = true;
						break;
					}
				}
//				printMap("lsn", lsn);
//				printMap("next", next);
//				printSet(pending);
			} while (retry);

		}
	};
}
