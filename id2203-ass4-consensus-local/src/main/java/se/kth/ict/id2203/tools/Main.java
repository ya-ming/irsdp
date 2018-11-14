package se.kth.ict.id2203.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.trace("trace");
        logger.info("info");

        String s = " 1234";
        s = s.trim();
        Integer i = Integer.parseInt(s);

        String s2 = "102882587892676";
        Long i2 = Long.parseLong(s2);

        System.out.println(System.nanoTime());

        String msg = "Paxos2 -> Paxos2: PrepareAckMessage, ats = 0, av = null, ts = 5, t = 2";
        if (msg.indexOf("PrepareAckMessage") != -1) {
            msg = msg.replace("->", "-[#red]>");
        }
        System.out.println(msg);
    }
}
