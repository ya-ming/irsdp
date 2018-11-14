package se.kth.ict.id2203.ports.sc;

import se.sics.kompics.Event;

public class ScDecide extends Event {

    private final Object value;

    public ScDecide(Object value) {
        this.value = value;
    }

    public final Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ScDecide{" +
                "value=" + value +
                '}';
    }
}
