import domain.State;

import java.util.List;

/**
 * @author David
 */
public class Result {

    private final State finalState;
    private final long time;
    private final List<String> instrumentation;
    private final double h;

    public Result(State finalState, long time, List<String> instrumentation, double h) {
        this.finalState = finalState;
        this.time = time;
        this.instrumentation = instrumentation;
        this.h = h;
    }

    public State getFinalState() {
        return finalState;
    }

    public double getFinalValue() {
        return h;
    }

    public long getTime() {
        return time/1000000;
    }

    public double getCost() {
        return finalState.cost();
    }

    public long getFelicitat() {
        return finalState.felicitat();
    }

    @Override
    public String toString() {
        return finalState + System.lineSeparator() +
                time + System.lineSeparator() +
                instrumentation.toString();
    }

}
