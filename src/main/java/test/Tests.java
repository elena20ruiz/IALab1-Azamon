package test;

import aima.search.framework.HeuristicFunction;
import domain.Parameters;
import domain.State;
import domain.State.GEN_ALG;
import domain.heuristic.Heuristic1;
import domain.heuristic.Heuristic2;
import domain.successor.hc.HCSuccessorFunction;
import domain.successor.hc.HCSuccessorFunction1;
import domain.successor.hc.HCSuccessorFunction2;
import domain.successor.hc.HCSuccessorFunction3;
import domain.successor.sa.SASuccessorFunction;
import domain.successor.sa.SASuccessorFunction1;
import test.StartSearch.SAParams;

import static domain.State.GEN_ALG.SEQUENTIAL;
import static domain.State.GEN_ALG.SORTED;

/**
 * @author David
 */
public class Tests {

    public static HCSuccessorFunction HC_BEST = new HCSuccessorFunction1();
    public static SASuccessorFunction SA_BEST = new SASuccessorFunction1();
    public static GEN_ALG GEN_BEST = SORTED;
    public static SAParams SA_PARAM_BEST = null;

    private static final String HC_BEST_NOT_DEFINED = "HC_BEST indefinido.";
    private static final String SA_BEST_NOT_DEFINED = "SA_BEST indefinido.";
    private static final String GEN_BEST_NOT_DEFINED = "GEN_BEST indefinido.";
    private static final String SA_PARAM_BEST_NOT_DEFINED = "SA_PARAM_BEST indefinido.";

    private Tests() {}

    public static Result test_HC(int npaq, double proporcion, GEN_ALG GEN, HCSuccessorFunction sf, HeuristicFunction h, int seed) {
        Parameters.ini(npaq, proporcion, seed);
        return StartSearch.hillClimbingSearch(
                new State(GEN),
                sf,
                h
        );
    }

    public static Result test_SA(int npaq, double proporcion, GEN_ALG GEN, SASuccessorFunction sf, HeuristicFunction h, SAParams sap, int seed) {
        Parameters.ini(npaq, proporcion, seed);
        return StartSearch.simulatedAnnealingSearch(
                new State(GEN),
                sf,
                h,
                sap
        );
    }


    public static Result test_HC(int seed) {
        check(TYPE.GEN, TYPE.HC_SUCC);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(GEN_BEST),
                HC_BEST,
                new Heuristic1()
        );
    }

    public static Result test_SA(int seed) {
        check(TYPE.GEN, TYPE.SA_PARAM, TYPE.SA_SUCC);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.simulatedAnnealingSearch(
                new State(GEN_BEST),
                SA_BEST,
                new Heuristic1(),
                SA_PARAM_BEST
        );
    }


    public static Result test_OP1(int seed) {
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(SEQUENTIAL),
                new HCSuccessorFunction1(),
                new Heuristic1()
        );
    }

    public static Result test_OP2(int seed) {
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(SEQUENTIAL),
                new HCSuccessorFunction2(),
                new Heuristic1()
        );
    }

    public static Result test_OP3(int seed) {
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(SEQUENTIAL),
                new HCSuccessorFunction3(),
                new Heuristic1()
        );
    }

    public static Result test_SEQ(int seed) {
        check(TYPE.HC_SUCC);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(SEQUENTIAL),
                HC_BEST,
                new Heuristic1()
        );
    }

    public static Result test_SORTED(int seed) {
        check(TYPE.HC_SUCC);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(SORTED),
                HC_BEST,
                new Heuristic1()
        );
    }

    public static Result test_SA(int seed, int n, int limit, int k, double lambda) {
        check(TYPE.GEN, TYPE.SA_SUCC);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.simulatedAnnealingSearch(
                new State(GEN_BEST),
                SA_BEST,
                new Heuristic1(),
                new SAParams(n, limit, k, lambda)
        );
    }

    public static Result test_INI(int seed, int paquets, double proporcio) {
        check(TYPE.GEN, TYPE.HC_SUCC);
        Parameters.ini(paquets, proporcio, seed);
        return StartSearch.hillClimbingSearch(
                new State(GEN_BEST),
                HC_BEST,
                new Heuristic1()
        );
    }

    public static Result test_HC_ALPHA(int seed, double alpha) {
        check(TYPE.GEN, TYPE.HC_SUCC);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.hillClimbingSearch(
                new State(GEN_BEST),
                HC_BEST,
                new Heuristic2(alpha)
        );
    }

    public static Result test_SA_ALPHA(int seed, double alpha) {
        check(TYPE.GEN, TYPE.SA_SUCC, TYPE.SA_PARAM);
        Parameters.ini(100, 1.2, seed);
        return StartSearch.simulatedAnnealingSearch(
                new State(GEN_BEST),
                SA_BEST,
                new Heuristic2(alpha),
                SA_PARAM_BEST
        );
    }

    private static void check(TYPE... types) {
        for (TYPE type : types) {
            switch (type) {
                case HC_SUCC:
                    if (HC_BEST == null) throw new NullPointerException(HC_BEST_NOT_DEFINED);
                case SA_SUCC:
                    if (SA_BEST == null) throw new NullPointerException(SA_BEST_NOT_DEFINED);
                case GEN:
                    if (GEN_BEST == null) throw new NullPointerException(GEN_BEST_NOT_DEFINED);
                case SA_PARAM:
                    if (SA_PARAM_BEST == null) throw new NullPointerException(SA_PARAM_BEST_NOT_DEFINED);
            }
        }
    }

    private enum TYPE {
        HC_SUCC,
        SA_SUCC,
        GEN,
        SA_PARAM
    }

}
