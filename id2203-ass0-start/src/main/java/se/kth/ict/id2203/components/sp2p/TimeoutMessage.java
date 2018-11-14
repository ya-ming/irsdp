package se.kth.ict.id2203.components.sp2p;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public final class TimeoutMessage extends Timeout {
	public TimeoutMessage(ScheduleTimeout request) {
		super(request);
	}
}
