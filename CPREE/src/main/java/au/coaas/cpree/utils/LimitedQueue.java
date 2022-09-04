package au.coaas.cpree.utils;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class LimitedQueue<E> extends LinkedList<E> {

    private int limit;
    private double mean;
    private double stdDev;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }

    public double average() {
        AtomicReference<Double> sum = new AtomicReference<>((double) 0);
        super.forEach(value -> {
            sum.updateAndGet(v -> new Double((v + (double) value)));
        });
        mean =  sum.get()/super.size();
        return mean;
    }

    public double reverse(double teta) {
        stdDev = Utilities.getStandardDeviation(super.toArray(), average());
        double zValue = Utilities.getZValueForProbability(teta);
        return mean + (zValue * stdDev);
    }
}
