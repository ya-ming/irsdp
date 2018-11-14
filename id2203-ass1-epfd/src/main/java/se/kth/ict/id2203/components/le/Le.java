package se.kth.ict.id2203.components.le;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.le.Leader;
import se.kth.ict.id2203.ports.le.LeaderElection;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Set;

public class Le extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(Le.class);

    private Negative<LeaderElection> le = provides(LeaderElection.class);
    private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private Address selfAddress;
    private Set<Address> allAddresses;
    private Set<Address> suspected;
    private Address leader;

    public Le(LeInit init) {
        logger.debug("Le, Construct, self: {}", init.getSelfAddress());
        logger.debug("Le, Construct, all: {}", init.getAllAddresses());

        this.selfAddress = init.getSelfAddress();
        this.allAddresses = init.getAllAddresses();

        leader = null;
        suspected = new HashSet<Address>();

        subscribe(handleStart, control);
        subscribe(handleCrash, pfd);
    }

    private void printSuspected() {
        logger.debug("Suspected:");
        for (Address a: suspected
                ) {
            logger.debug(a.toString());
        }
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("Le, handleStart");

            leader = maxRank();
            trigger(new Leader(leader), le);
        }
    };

    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            logger.debug("Le, handleCrash {}", event.getSource());
            suspected.add(event.getSource());

            Address temp_leader = maxRank();
            if (temp_leader != leader){
                leader = temp_leader;
                trigger(new Leader(leader), le);
            }
        }
    };

    private Address maxRank() {
        Address temp_leader = null;
        for (Address a: allAddresses
             ) {
//            System.out.println("processing " + a);
            if (suspected.contains(a)){
//                System.out.println("skip " + a);
                continue;
            }
            if (temp_leader == null) {
//                System.out.println("templeader == null, " + a);
                temp_leader = a;
            } else if (temp_leader.getId() > a.getId()) {
//                System.out.println("templeader != null, " + temp_leader + ", " + temp_leader.getId());
//                System.out.println("templeader != null, " + a + ", " + a.getId());
                temp_leader = a;
            }
        }
        return temp_leader;
    }
}