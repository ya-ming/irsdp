package com.yaming.irsdp.ch01.Events;

import se.sics.kompics.Event;

public final class Submit extends Event{
    private long id;

    public Submit(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
