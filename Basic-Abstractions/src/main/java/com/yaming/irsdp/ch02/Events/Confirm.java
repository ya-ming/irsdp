package com.yaming.irsdp.ch02.Events;

import se.sics.kompics.Event;

public class Confirm extends Event {
    private long id;

    public Confirm(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
