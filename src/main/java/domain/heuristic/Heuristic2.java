package domain.heuristic;

import aima.search.framework.HeuristicFunction;
import domain.State;

/**
 * @author David
 */
public class Heuristic2 implements HeuristicFunction {

    final double alpha;

    public Heuristic2(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double getHeuristicValue(Object state) {
        State current = (State) state;
        return (1-alpha)*current.cost() - alpha*current.felicitat();
    }

}
