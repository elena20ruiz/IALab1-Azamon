package test;

import aima.search.framework.HeuristicFunction;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import domain.State;
import domain.successor.hc.HCSuccessorFunction;
import domain.successor.sa.SASuccessorFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author David
 */
public class StartSearch {

    private StartSearch() {}

    public static Result hillClimbingSearch(State state, HCSuccessorFunction function, HeuristicFunction heuristic) {
        return run(
                new Problem(
                        state,
                        function,
                        ((end)->false),
                        heuristic
                ),
                new HillClimbingSearch(),
                state.getGenTime()
        );
    }

    public static Result simulatedAnnealingSearch(State state, SASuccessorFunction function, HeuristicFunction heuristic, SAParams par) {
        Search search;
        if (par != null)
            search = new SimulatedAnnealingSearch(par.n, par.limit, par.k, par.lambda);
        else search = new SimulatedAnnealingSearch();
        return run(
                new Problem(
                        state,
                        function,
                        ((end)->false),
                        heuristic
                ),
                search,
                state.getGenTime()
        );
    }

    private static Result run(Problem problem, Search search, long genTime) {
        try {
            long ini = System.nanoTime();
            SearchAgent agent = new SearchAgent(problem, search);
            long end = System.nanoTime();
            return new Result(
                    (State) search.getGoalState(),
                    (end - ini) + genTime,
                    getInstrumentation(agent.getInstrumentation()),
                    problem.getHeuristicFunction().getHeuristicValue(search.getGoalState())
            );
        } catch (Exception e) {
            System.err.println("ERROR :" + e.getClass().getSimpleName() + " - " + e.getMessage());
            return new Result(
                    (State) problem.getInitialState(),
                    0,
                    new LinkedList<>(),
                    problem.getHeuristicFunction().getHeuristicValue(problem.getInitialState())
            );
        }
    }

    private static List<String> getInstrumentation(Properties properties) {
        List<String> l = new LinkedList<>();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String property = properties.getProperty(key);
            l.add(key + " : " + property);
        }
        return l;
    }

    public static class SAParams {
        private final int n;
        private final int limit;
        private final int k;
        private final double lambda;

        public SAParams(int n, int limit, int k, double lambda) {
            this.n = n;
            this.limit = limit;
            this.k = k;
            this.lambda = lambda;
        }

    }

}
