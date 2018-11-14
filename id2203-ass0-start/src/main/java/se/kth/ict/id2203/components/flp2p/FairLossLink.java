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
package se.kth.ict.id2203.components.flp2p;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.ports.flp2p.Flp2pDeliver;
import se.kth.ict.id2203.ports.flp2p.Flp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.NoLinkException;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public final class FairLossLink extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(FairLossLink.class);

	private Negative<FairLossPointToPointLink> flp2p = provides(FairLossPointToPointLink.class);
	private Positive<Network> network = requires(Network.class);
	private Positive<Timer> timer = requires(Timer.class);

	private Address self;
	private Topology topology;
	private Random random;

	public FairLossLink(FairLossLinkInit event) {
		subscribe(handleFlp2pSend, flp2p);
		subscribe(handleMessage, network);
		subscribe(handleDelayedMessage, timer);

		topology = event.getTopology();
		self = topology.getSelfAddress();

		random = new Random(event.getRandomSeed());
	}

	private Handler<Flp2pSend> handleFlp2pSend = new Handler<Flp2pSend>() {
		@Override
		public void handle(Flp2pSend event) {
			Address destination = event.getDestination();

            logger.info("FairLossLink::handleFlp2pSend destination {}", destination.toString());

			if (destination.equals(self)) {
				// deliver locally
//                logger.info("FairLossLink::handleFlp2pSend deliver locally");
				Flp2pDeliver deliverEvent = event.getDeliverEvent();
				trigger(deliverEvent, flp2p);
				return;
			}

			double lossRate;
			long latency;
			try {
				lossRate = topology.getLossRate(self, destination);
				latency = topology.getLatencyMs(self, destination);
			} catch (NoLinkException e) {
				// there is no link to the destination, we drop the message
                logger.info("FairLossLink::handleFlp2pSend NoLinkException");
				return;
			}
			
			if (random.nextDouble() < lossRate) {
				// drop the message according to the loss rate
                logger.info("FairLossLink::handleFlp2pSend drop the message");
				return;
			}

			// make a FairLossLinkMessage to be delivered at the destination
			FairLossLinkMessage message = new FairLossLinkMessage(self,
					destination, event.getDeliverEvent());

			if (latency > 0) {
				// delay the sending according to the latency
//                logger.info("FairLossLink::handleFlp2pSend delay the sending {}", latency);
				ScheduleTimeout st = new ScheduleTimeout(latency);
				st.setTimeoutEvent(new DelayedMessage(st, message));
				trigger(st, timer);
			} else {
				// send immediately
//                logger.info("FairLossLink::handleFlp2pSend send immediately");
				trigger(message, network);
			}
		}
	};

	private Handler<DelayedMessage> handleDelayedMessage = new Handler<DelayedMessage>() {
		@Override
		public void handle(DelayedMessage event) {
//            logger.info("FairLossLink::handleDelayedMessage");
			trigger(event.getMessage(), network);
		}
	};

	private Handler<FairLossLinkMessage> handleMessage = new Handler<FairLossLinkMessage>() {
		@Override
		public void handle(FairLossLinkMessage event) {
//            logger.info("FairLossLink::handleMessage");
			trigger(event.getDeliverEvent(), flp2p);
		}
	};
}
