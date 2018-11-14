package se.kth.ict.id2203.ports.reconfigurable.cfg;

import se.sics.kompics.Event;

public class Config extends Event {
    private final Configuration configuration;

    public Config(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
