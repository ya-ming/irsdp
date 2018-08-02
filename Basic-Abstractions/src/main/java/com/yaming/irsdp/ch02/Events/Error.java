package com.yaming.irsdp.ch02.Events;

import se.sics.kompics.Event;

public class Error extends Event {
    private long id;

    public Error(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
