package se.kth.ict.id2203.components.sc;

import java.util.List;

public class AckInfo {
    private final Integer na;
    private final List<Object> sfxa;


    public AckInfo(Integer na, List<Object> sfxa) {
        this.na = na;
        this.sfxa = sfxa;
    }

    public Integer getNa() {
        return na;
    }


    public List<Object> getSfxa() {
        return sfxa;
    }

    @Override
    public String toString() {
        return "AckInfo{" +
                "na=" + na +
                ", sfxa=" + sfxa +
                '}';
    }
}

