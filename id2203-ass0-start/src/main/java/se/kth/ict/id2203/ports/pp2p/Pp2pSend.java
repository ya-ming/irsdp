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
package se.kth.ict.id2203.ports.pp2p;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public final class Pp2pSend extends Event {

	private final Pp2pDeliver deliverEvent;

	private final Address destination;

	public Pp2pSend(Address destination, Pp2pDeliver pp2pDeliver) {
		this.destination = destination;
		this.deliverEvent = pp2pDeliver;
	}

	public final Pp2pDeliver getDeliverEvent() {
		return deliverEvent;
	}

	public final Address getDestination() {
		return destination;
	}
}
