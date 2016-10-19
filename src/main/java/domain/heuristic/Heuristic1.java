package domain.heuristic;

import aima.search.framework.HeuristicFunction;
import domain.State;


/**
 * @author David
 */
public class Heuristic1 implements HeuristicFunction {

    @Override
    public double getHeuristicValue(Object state) {
        return ((State) state).cost();
    }

}
