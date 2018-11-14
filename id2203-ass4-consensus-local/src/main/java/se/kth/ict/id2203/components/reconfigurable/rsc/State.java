package se.kth.ict.id2203.components.reconfigurable.rsc;



public class State {
    public Role role = Role.NONE;
    public Phase phase = Phase.NONE;

    public enum Role {
        FOLLOWER, LEADER, NONE
    }

    public enum Phase {
        PREPARE, ACCEPT, NONE
    }

    @Override
    public String toString() {
        return "State{" +
                "role=" + role +
                ", phase=" + phase +
                '}';
    }
}
