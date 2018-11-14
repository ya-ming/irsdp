package se.kth.ict.id2203.ports.ac;

public class TupleTsVal {
	private final int timestamp;
	private final Integer value;

	public TupleTsVal(int timestamp, Integer value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public Integer getValue() {
		return value;
	}

	public boolean gt(TupleTsVal other) {
		if (timestamp > other.timestamp) {
			return true;
		} else {
			return false;
		}
	}

}
