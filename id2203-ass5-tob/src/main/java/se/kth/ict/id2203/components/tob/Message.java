package se.kth.ict.id2203.components.tob;

import java.io.Serializable;

import se.kth.ict.id2203.ports.tob.TobDeliver;

public class Message implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = -4884571528260682L;

	private final int seqNum;
	private final int pid;
	private final TobDeliver deliverEvent;

	public Message(int seqNum, int pid, TobDeliver deliverEvent) {
		this.seqNum = seqNum;
		this.pid = pid;
		this.deliverEvent = deliverEvent;
	}
	
	public int getSeqNum() {
		return seqNum;
	}
	
	public int getPid() {
		return pid;
	}
	
	public TobDeliver getDeliverEvent() {
		return deliverEvent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Message) {
			Message m = (Message) obj;
			return seqNum == m.seqNum && pid == m.pid;
		}
		return false;
	}

	@Override
	public int compareTo(Message o) {
		int c = Integer.compare(seqNum, o.seqNum);
		if (c == 0) {
			c = Integer.compare(pid, o.pid);
		}
		return c;
	}

	@Override
	public String toString() {
		return String.format("Message(%d, %d)", seqNum, pid);
	}
}
