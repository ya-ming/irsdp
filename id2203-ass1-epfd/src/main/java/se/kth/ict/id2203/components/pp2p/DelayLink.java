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
package se.kth.ict.id2203.components.pp2p;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.NoLinkException;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkControl;
import se.sics.kompics.network.NetworkSessionOpened;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public final class DelayLink extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(DelayLink.class);

	private Negative<PerfectPointToPointLink> pp2p = provides(PerfectPointToPointLink.class);
	private Positive<Network> network = requires(Network.class);
//	private Positive<NetworkControl> networkControl = requires(NetworkControl.class);
	private Positive<Timer> timer = requires(Timer.class);

	private Address self;
	private Topology topology;
	private long sigma;

	private Random random = new Random(0);

	public DelayLink(DelayLinkInit event) {
		subscribe(handlePp2pSend, pp2p);
		subscribe(handleMessage, network);
		subscribe(handleDelayedMessage, timer);
//		subscribe(handleNetworkSessionOpened, networkControl);

		topology = event.getTopology();
		sigma = event.getSigma();
		self = topology.getSelfAddress();
	}

	private Handler<NetworkSessionOpened> handleNetworkSessionOpened = new Handler<NetworkSessionOpened>() {
		@Override
		public void handle(NetworkSessionOpened event) {
			logger.debug("PP2P handleNetworkSessionOpened " + event.getRemoteAddress());
		}
	};

	private Handler<Pp2pSend> handlePp2pSend = new Handler<Pp2pSend>() {
		@Override
		public void handle(Pp2pSend event) {
			Address destination = event.getDestination();

            logger.debug("PP2P handlePp2pSend " + destination);

			if (destination.equals(self)) {
				// deliver locally
				Pp2pDeliver deliverEvent = event.getDeliverEvent();
				trigger(deliverEvent, pp2p);
				return;
			}

			long latency;
			try {
				latency = Math.max(0, topology.getLatencyMs(self, destination) + (long) (random.nextGaussian() * sigma));
			} catch (NoLinkException e) {
				// there is no link to the destination, we drop the message
				return;
			}

			// make a DelayLinkMessage to be delivered at the destination
			DelayLinkMessage message = new DelayLinkMessage(self, destination,
					event.getDeliverEvent());

			if (latency > 0) {
				// delay the sending according to the latency
				ScheduleTimeout st = new ScheduleTimeout(latency);
				st.setTimeoutEvent(new DelayedMessage(st, message));
				trigger(st, timer);
			} else {
				// send immediately
				trigger(message, network);
			}
		}
	};

	private Handler<DelayedMessage> handleDelayedMessage = new Handler<DelayedMessage>() {
		@Override
		public void handle(DelayedMessage event) {
			logger.debug("PP2P handleDelayedMessage " + event.getMessage().getDestination());
			trigger(event.getMessage(), network);
		}
	};

	private Handler<DelayLinkMessage> handleMessage = new Handler<DelayLinkMessage>() {
		@Override
		public void handle(DelayLinkMessage event) {
            logger.debug("PP2P handleMessage " + event.getDestination());
			trigger(event.getDeliverEvent(), pp2p);
		}
	};
}
