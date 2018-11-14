package se.kth.ict.id2203.tools;

import org.slf4j.Logger;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.util.HashSet;

public class MessageLogger {
    public static void logMessageIn(Logger logger,
                                    String sourceDescriptor, Address source,
                                    String targetDescriptor, Address target,
                                    Event message) {

        String logString = String.format(
                "%d %s%d -> %s%d: %s",
                System.nanoTime(),
                sourceDescriptor, source.getId(),
                targetDescriptor, target.getId(),
                message
        );

        logger.trace(logString);
        logger.info(logString);
    }

    public static void logMessageOut(Logger logger,
                                    String sourceDescriptor, Address source,
                                    String targetDescriptor, Address target,
                                    Event message) {

        String logString = String.format(
                "%d %s%d --> %s%d: %s",
                System.nanoTime(),
                sourceDescriptor, source.getId(),
                targetDescriptor, target.getId(),
                message
        );

        logger.trace(logString);
        logger.info(logString);
    }

    public static void logMessageOut(Logger logger,
                                     String sourceDescriptor, Address source,
                                     String targetDescriptor, HashSet<Address> targets,
                                     Event message) {

        for (Address target : targets
                ) {
            String logString = String.format(
                    "%d %s%d --> %s%d: %s",
                    System.nanoTime(),
                    sourceDescriptor, source.getId(),
                    targetDescriptor, target.getId(),
                    message
            );

            logger.trace(logString);
            logger.info(logString);
        }
    }
}
