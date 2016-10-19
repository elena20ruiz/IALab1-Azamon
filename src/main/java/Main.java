import aima.search.framework.HeuristicFunction;
import domain.State;
import domain.heuristic.Heuristic1;
import domain.heuristic.Heuristic2;
import domain.successor.hc.HCSuccessorFunction;
import domain.successor.hc.HCSuccessorFunction1;
import domain.successor.hc.HCSuccessorFunction2;
import domain.successor.hc.HCSuccessorFunction3;
import domain.successor.sa.SASuccessorFunction;
import domain.successor.sa.SASuccessorFunction1;
import domain.successor.sa.SASuccessorFunction2;
import domain.successor.sa.SASuccessorFunction3;
import test.Result;
import test.StartSearch.SAParams;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static domain.State.GEN_ALG.SEQUENTIAL;
import static domain.State.GEN_ALG.SORTED;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;
import static java.lang.System.lineSeparator;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.regex.Pattern.compile;

/**
 * @author David
 */
public class Main {

    private static final String num = "[0-9]+";
    private static final String dec = "[0-9]+(\\.[0-9]+)?";
    private static final String alpha = "(0(\\.[0-9]+)?)|1";

    private static final Pattern testPattern =
            compile("[1-467] " + num + " " + num);
    private static final Pattern espPattern =
            compile("esp");
    private static final Pattern HCPattern =
            compile("HC " + num + " " + dec + " (SEQ|SORT) (OP1|OP2|OP3) (H1|H2 " + alpha + ") " + num);
    private static final Pattern SAPattern =
            compile("SA " + num + " " + dec + " (SEQ|SORT) (OP1|OP2|OP3) (H1|H2 " + alpha + ") " +
                    num + " " + num + " " + num + " " + dec + " " + num);

    private static final String test1 = "Test1_";
    private static final String test2 = "Test2_";
    private static final String test3 = "Test3_";
    private static final String test4 = "Test4_";
    private static final String test6 = "Test6_";
    private static final String test7 = "Test7_";
    private static final String HC = "HC_";
    private static final String SA = "SA_";
    private static final String testEsp = "TestEsp.txt";
    private static final String txt = ".txt";

    private static final int ESP_SEED = 1234;

    private static BufferedWriter bw;

    private static final MathContext mc = new MathContext(6, HALF_EVEN);


    private Main() {}

    public static void main(String... args) {
        String inLine = "";
        if (args.length > 0) {
            for (int i = 0; i < args.length - 1; ++i)
                inLine += args[i] + " ";
            inLine += args[args.length-1];
        }
        Matcher testMatcher = testPattern.matcher(inLine);
        Matcher HCMatcher = HCPattern.matcher(inLine);
        Matcher SAMatcher = SAPattern.matcher(inLine);
        Matcher espMatcher = espPattern.matcher(inLine);

        try {
            if (testMatcher.matches())
                parseTests(args);
            else if (HCMatcher.matches())
                parseHC(args);
            else if (SAMatcher.matches())
                parseSA(args);
            else if (espMatcher.matches()) {
                bw = new BufferedWriter(new FileWriter(testEsp));
                testEsp();
            }
            else {
                System.err.println("USAGE: test nTests seed");
                System.err.println("USAGE: esp");
                System.err.println("USAGE: HC paquets proporcio [SEQ|SORT] [OP1|OP2|OP3] [H1|(H2 alpha)] seed");
                System.err.println("USAGE: SA paquets proporcio [SEQ|SORT] [OP1|OP2|OP3] [H1|(H2 alpha)] n limit k lambda seed");
                System.err.println("NOTE: 0 <= alpha <= 1");
            }
            if (bw != null) {
                bw.flush();
                bw.close();
            }
        } catch (IOException e) {
            System.err.println("Error en la escritura de los resultados.");
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void parseTests(String[] args) throws IOException {
        int test = parseInt(args[0]);
        int nTests = parseInt(args[1]);
        int seed = parseInt(args[2]);
        switch (test) {
            case 1:
                bw = new BufferedWriter(new FileWriter(test1 + seed + txt));
                test1(nTests, seed);
                break;
            case 2:
                bw = new BufferedWriter(new FileWriter(test2 + seed + txt));
                test2(nTests, seed);
                break;
            case 3:
                bw = new BufferedWriter(new FileWriter(test3 + seed + txt));
                test3(nTests, seed);
                break;
            case 4:
                bw = new BufferedWriter(new FileWriter(test4 + seed + txt));
                test4(nTests, seed);
                break;
            case 6:
                bw = new BufferedWriter(new FileWriter(test6 + seed + txt));
                test6(nTests, seed);
                break;
            default:
                bw = new BufferedWriter(new FileWriter(test7 + seed + txt));
                test7(nTests, seed);
                break;
        }
    }

    private static void parseHC(String[] args) throws IOException {
        State.GEN_ALG alg;
        HCSuccessorFunction sf;
        HeuristicFunction hf;
        int seed;

        alg = args[3].equals("SEQ") ?  SEQUENTIAL : SORTED;
        switch (args[4]) {
            case "OP1":
                sf = new HCSuccessorFunction1();
                break;
            case "OP2":
                sf = new HCSuccessorFunction2();
                break;
            default:
                sf = new HCSuccessorFunction3();
                break;
        }
        if (args[5].equals("H1")) {
            hf = new Heuristic1();
            seed = parseInt(args[6]);
        }
        else {
            hf = new Heuristic2(parseDouble(args[6]));
            seed = parseInt(args[7]);
        }
        bw = new BufferedWriter(new FileWriter(HC + seed + txt));
        testHC(
                parseInt(args[1]),
                parseDouble(args[2]),
                alg,
                sf,
                hf,
                seed
        );
    }

    private static void parseSA(String[] args) throws IOException {
        State.GEN_ALG alg;
        SASuccessorFunction sf;
        HeuristicFunction hf;
        int n;
        int limit;
        int k;
        double lambda;
        int seed;

        alg = args[3].equals("SEQ") ?  SEQUENTIAL : SORTED;
        switch (args[4]) {
            case "OP1":
                sf = new SASuccessorFunction1();
                break;
            case "OP2":
                sf = new SASuccessorFunction2();
                break;
            default:
                sf = new SASuccessorFunction3();
                break;
        }
        if (args[5].equals("H1")) {
            hf = new Heuristic1();
            n = parseInt(args[6]);
            limit = parseInt(args[7]);
            k = parseInt(args[8]);
            lambda = parseDouble(args[9]);
            seed = parseInt(args[10]);
        }
        else {
            hf = new Heuristic2(parseDouble(args[6]));
            n = parseInt(args[7]);
            limit = parseInt(args[8]);
            k = parseInt(args[9]);
            lambda = parseDouble(args[10]);
            seed = parseInt(args[11]);
        }

        bw = new BufferedWriter(new FileWriter(SA + seed + txt));
        testSA(
                parseInt(args[1]),
                parseDouble(args[2]),
                alg,
                sf,
                hf,
                new SAParams(n, limit, k, lambda),
                seed
        );
    }

    private static void test1(int nTests, int seed) throws IOException {
        List<String> l = new ArrayList<>(4);
        BigDecimal[] out;

        out = makeTest(nTests, seed, "1.OP1", (s)->(test_OP1(s)));
        l.add("1.OP1: " + processTestOut(out));
        out = makeTest(nTests, seed, "1.OP2", (s)->(test_OP2(s)));
        l.add("1.OP2: " + processTestOut(out));
        out = makeTest(nTests, seed, "1.OP3", (s)->(test_OP3(s)));
        l.add("1.OP3: " + processTestOut(out));
        printL(l);
    }

    private static void test2(int nTests, int seed) throws IOException {
        List<String> l = new ArrayList<>(2);
        BigDecimal[] out;

        out = makeTest(nTests, seed, "2.SEQ", (s)->(test_SEQ(s)));
        l.add("2.SEQ: " + processTestOut(out));
        out = makeTest(nTests, seed, "2.SORTED", (s)->(test_SORTED(s)));
        l.add("2.SORTED: " + processTestOut(out));

        printL(l);
    }

    private static void test3(int nTests, int seed) throws IOException {
        int maxN = -1;
        int maxLimit = -1;
        int maxK = -1;
        int maxL = -1;
        BigDecimal bestCost = BigDecimal.ZERO;
        BigDecimal bestFeliciat = null;
        BigDecimal bestExeTime = null;
        boolean first;
        String pars;
        List<String> sMax = new ArrayList<>(5);


        for (int n = 100; n <= 100000; n *= 10) { //n = {10000, 100000, [...], 100000000}
            first = true;
            for (int limit = 100; limit <= 10000; limit*= 10) { //limit = {100, 1000, 10000}
                for (int k = 35; k <= 50; k += 5) { //k = {35, 40, 45, 50}
                    for (int l = 1; l <= 5; l++) { //lamb = {0.5, 0.05, [...], 0.00005}
                        final int nFinal = n;
                        final int limitFinal = limit;
                        final int kFinal = k;
                        final double lFinal = 5*pow(10, -l);
                        pars = String.format("(%d, %d, %d, %f)", nFinal, limitFinal, kFinal, lFinal);
                        BigDecimal[] out = makeTest(nTests, seed, "3." + pars, (s) -> (test_SA(s, nFinal, limitFinal, kFinal, lFinal)));

                        if (first || out[0].compareTo(bestCost) < 0) {
                            first = false;
                            maxN = n;
                            maxK = k;
                            maxLimit = limit;
                            maxL = l;
                            bestCost = out[0];
                            bestFeliciat = out[1];
                            bestExeTime = out[2];
                        }
                        bw.write(lineSeparator());
                        bw.write("SIM" + pars + ": " + out[0].round(mc) + "(" + out[1].round(mc) + ") - " +
                                getTime(out[2].longValue()) + lineSeparator());
                    }
                }
            }
            sMax.add(
                    String.format(
                            "BEST: (%d, %d, %d, %f) - %s %s (%s)",
                            maxN, maxLimit, maxK, 5*pow(10, -maxL),
                            bestCost.round(mc),
                            bestFeliciat.round(mc),
                            bestExeTime.round(mc)
                    )
            );
        }
        printL(sMax);
    }

    private static void test4(int nTests, int seed) throws IOException {
        List<String> PR = new ArrayList<>(19);
        List<String> PA = new ArrayList<>(18);
        BigDecimal[] out;

        final int paquetsFixats = 100;
        final double proporcioFixada = 1.2;

        for (double proporcio = 1.2; proporcio <= 5; proporcio += 0.2) {
            final double fProp = proporcio;
            out = makeTest(nTests, seed, "4.PROP." + proporcio, (s)->(test_INI(s, paquetsFixats, fProp)));
            PR.add("PROP(" + proporcio + "):" + processTestOut(out));
        }

        for (int paquets = 100; paquets <= 1000; paquets += 50) {
            final int fPaq = paquets;
            out = makeTest(nTests, seed, "4.PAQUETS." + paquets, (s)->(test_INI(s, fPaq, proporcioFixada)));
            PA.add("PAQUETS(" + paquets + "):" + processTestOut(out));
        }

        printL(PR);
        bw.write(lineSeparator());
        printL(PA);
    }

    private static void test6(int nTests, int seed) throws IOException {
        List<String> l = new ArrayList<>(19);
        BigDecimal[] out;

        for (double alpha = 0; alpha <= 1; alpha += 0.05) {
            final double fAlpha = alpha;
            out = makeTest(nTests, seed, "6." + alpha, (s)->(test_HC_ALPHA(s, fAlpha)));
            l.add("6." + alpha + ":" + processTestOut(out));
        }
        printL(l);
    }

    private static void test7(int nTests, int seed) throws IOException {
        List<String> l = new ArrayList<>(20);
        BigDecimal[] out;

        for (double alpha = 0; alpha <= 1; alpha += 0.05) {
            final double fAlpha = alpha;
            out = makeTest(nTests, seed, "7." + alpha, (s) -> (test_SA_ALPHA(s, fAlpha)));
            l.add("7." + alpha + ":" + processTestOut(out));
        }
        printL(l);
    }

    private static void testHC(int npaq, double proporcion, State.GEN_ALG GEN, HCSuccessorFunction sf, HeuristicFunction h, int seed) throws IOException {
        bw.write("HC TEST" + lineSeparator());
        Result r = test_HC(
                npaq,
                proporcion,
                GEN,
                sf,
                h,
                seed);
        BigDecimal cost = new BigDecimal(r.getFinalState().cost());
        BigDecimal felicitat = new BigDecimal(r.getFinalState().felicitat());
        BigDecimal exeTime = new BigDecimal(r.getTime());
        bw.write(cost + " " + felicitat + " " + exeTime + " - " + getTime(r.getTime()) + lineSeparator());
    }

    private static void testSA(int npaq, double proporcion, State.GEN_ALG GEN, SASuccessorFunction sf, HeuristicFunction h, SAParams sap, int seed) throws IOException {
        bw.write("SA TEST" + lineSeparator());
        Result r = test_SA(
                npaq,
                proporcion,
                GEN,
                sf,
                h,
                sap,
                seed);
        BigDecimal cost = new BigDecimal(r.getFinalState().cost());
        BigDecimal felicitat = new BigDecimal(r.getFinalState().felicitat());
        BigDecimal exeTime = new BigDecimal(r.getTime());
        bw.write(cost + " " + felicitat + " " + exeTime + " - " + getTime(r.getTime()) + lineSeparator());
    }

    private static void testEsp() throws IOException {
        Result r;

        System.out.println("Hill Climbing");
        r = test_HC(ESP_SEED);
        bw.write("Cost (HC - H1): " + r.getCost() + lineSeparator());
        bw.write("Execution time (HC - H1): " + r.getTime() + "ms." + lineSeparator());
        bw.write(lineSeparator());

        System.out.println("Simulated Annealing");
        r = test_SA(ESP_SEED);
        bw.write("Cost (SA - H1): " + r.getCost() + lineSeparator());
        bw.write("Execution time (SA - H1): " + r.getTime() + "ms." + lineSeparator());
        bw.write(lineSeparator());
    }


    private static String getTime(long time) {
        long hours = time / (3600*1000);
        long minutes = (time / (60*1000)) % 60;
        long seconds = (time / (1000)) % 60;
        String t = String.valueOf(time%1000);
        while (t.length() < 3)
            t = "0" + t;
        t = seconds + "." + t + "s";
        if (minutes > 0) t = minutes + "m " + t;
        if (hours > 0) t = hours + "h " + t;
        return t;
    }

    //out = {meanCost, meanFeliciat, meanExeTime, meanH};
    private static String processTestOut(BigDecimal[] out) {
        return out[0].round(mc) + " - " +
                out[1].round(mc) + " - " +
                out[3].round(mc) + " - " +
                out[2] + "ms. - " + getTime(out[2].longValue());
    }

    private static void printL(List<String> l) throws IOException {
        for (String s : l) {
            bw.write(lineSeparator());
            bw.write(s + lineSeparator());
        }
    }

    //Retorna {meanCost, meanFeliciat, meanExeTime, meanH}
    private static BigDecimal[] makeTest(int nTests, int seed, String testName, IntFunction<Result> test) throws IOException {
        BigDecimal n = new BigDecimal(nTests);
        BigDecimal h;
        BigDecimal cost;
        BigDecimal felicitat;
        BigDecimal exeTime;
        BigDecimal meanH = BigDecimal.ZERO;
        BigDecimal meanCost = BigDecimal.ZERO;
        BigDecimal meanFeliciat = BigDecimal.ZERO;
        BigDecimal meanExeTime = BigDecimal.ZERO;
        Random random = new Random();
        Result r;

        random.setSeed(seed);
        bw.write(testName + ":" + lineSeparator());
        for (int i = 0; i < nTests; ++i) {
            System.out.println(testName + "." + i);
            r = test.apply(random.nextInt());
            h = new BigDecimal(r.getFinalValue());
            cost = new BigDecimal(r.getCost());
            felicitat = new BigDecimal(r.getFelicitat());
            exeTime = new BigDecimal(r.getTime());
            bw.write(cost.round(mc) + " " + felicitat.round(mc) + " " + exeTime + "ms" + lineSeparator());
            meanH = meanH.add(h);
            meanCost = meanCost.add(cost);
            meanFeliciat = meanFeliciat.add(felicitat);
            meanExeTime = meanExeTime.add(exeTime);
            random.setSeed(random.nextInt());
        }
        bw.write(lineSeparator());
        meanH = meanH.divide(n, HALF_EVEN);
        meanCost = meanCost.divide(n, HALF_EVEN);
        meanFeliciat = meanFeliciat.divide(n, HALF_EVEN);
        meanExeTime = meanExeTime.divide(n, HALF_EVEN);
        return new BigDecimal[]{meanCost, meanFeliciat, meanExeTime, meanH};
    }

}
