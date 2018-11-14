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

import se.kth.ict.id2203.ports.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Transport;

public final class FairLossLinkMessage extends Message {

	private static final long serialVersionUID = -3397276101112877392L;

	private final Flp2pDeliver deliverEvent;

	public FairLossLinkMessage(Address source, Address destination,
							   Flp2pDeliver deliverEvent) {
		super(source, destination, Transport.TCP);
		this.deliverEvent = deliverEvent;
	}

	public final Flp2pDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
