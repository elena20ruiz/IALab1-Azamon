package domain;

import IA.Azamon.Oferta;
import IA.Azamon.Paquete;
import aima.search.framework.Successor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.compare;
import static java.lang.System.lineSeparator;

/**
 * @author David
 */
public class State {

    /**
     * A cada paquet <code>i</code> li assignem una oferta <code>ofertesAssignades[i]</code><br>
     * {O1, O1, O3, O2, ...}
     * */
    private final int[] ofertesAssignades;

    /**
     * Cada oferta <code>i</code> té acumulat un pes <code>pesOferta[i]</code><br>
     * Atribut derivat
     * */
    private final double[] pesOferta;

    private final long genTime;

    private final double cost;

    private final long felicitat;


    private State(int[] ofertes, double[] pesos) {
        this.ofertesAssignades = ofertes;
        pesOferta = pesos;
        genTime = 0;
        cost = getCost();
        felicitat = getFelicitat();
    }

    public State(GEN_ALG gen) {
        long ini = System.nanoTime();
        Parameters param = Parameters.getInstance();
        ofertesAssignades = new int[param.getNPaquetes()];
        pesOferta = new double[param.getNOfertas()];
        Arrays.fill(ofertesAssignades, -1);
        Arrays.fill(pesOferta, 0);
        switch (gen) {
            case SEQUENTIAL:
                generateSequential();
                break;
            case SORTED:
                generateSort();
                break;
            default:
                throw new RuntimeException("Algoritme de generació desconegut.");
        }
        cost = getCost();
        felicitat = getFelicitat();
        long end = System.nanoTime();
        genTime = end - ini;
    }

    private void generateSequential() {
        Parameters param = Parameters.getInstance();
        List<Integer> paqueteList = new ArrayList<>(param.getNPaquetes());
        List<Integer> ofertaList = new ArrayList<>(param.getNOfertas());

        for (int i = 0; i < param.getNPaquetes(); ++i)
            paqueteList.add(i);
        for (int i = 0; i < param.getNOfertas(); ++i)
            ofertaList.add(i);

        paqueteList.sort((o1, o2) ->
                compare(param.getPaquete(o1).getPrioridad(), param.getPaquete(o2).getPrioridad()));

        ofertaList.sort((o1, o2) ->
                compare(param.getOferta(o1).getDias(), param.getOferta(o2).getDias()));

        generateFromListBT(paqueteList, ofertaList);
    }

    private void generateSort() {
        Parameters param = Parameters.getInstance();
        List<Integer> paqueteList = new ArrayList<>(param.getNPaquetes());
        List<Integer> ofertaList = new ArrayList<>(param.getNOfertas());

        for (int i = 0; i < param.getNPaquetes(); ++i)
            paqueteList.add(i);
        for (int i = 0; i < param.getNOfertas(); ++i)
            ofertaList.add(i);

        paqueteList.sort((o1, o2) -> {
            int cmp = compare(param.getPaquete(o1).getPrioridad(), param.getPaquete(o2).getPrioridad());
            if (cmp != 0) return cmp;
            else return Double.compare(param.getPaquete(o1).getPeso(), param.getPaquete(o2).getPeso());
        });

        ofertaList.sort((o1, o2) -> {
            int cmp = compare(param.getPaquete(o1).getPrioridad(), param.getPaquete(o2).getPrioridad());
            if (cmp != 0) return cmp;
            else return Double.compare(param.getOferta(o2).getPrecio(), param.getOferta(o2).getPrecio());
        });

        generateFromListBT(paqueteList, ofertaList);
    }
    
    private void generateFromListBT(List<Integer> paqueteList, List<Integer> ofertaList) {
        if (!generateFromListBT(paqueteList, 0, ofertaList))
            throw new RuntimeException("No s'ha pogut realitzar l'assignació d'un paquet.");
    }

    private boolean generateFromListBT(List<Integer> paqueteList, int pos, List<Integer> ofertaList) {
        if (pos >= paqueteList.size()) return true;
        Parameters param = Parameters.getInstance();

        int paq = paqueteList.get(pos);
        double peso = param.getPaquete(paq).getPeso();

        for (int of : ofertaList) {
            if (canAdd(paq, of)) { //agafa oferta j-essima en ordre
                ofertesAssignades[paq] = of;
                pesOferta[of] += peso;
                if (generateFromListBT(paqueteList, pos+1, ofertaList))
                    return true;
                ofertesAssignades[paq] = -1;
                pesOferta[of] -= peso;
            }
        }
        return false;
    }


    private boolean canAdd(int paquet, int oferta) {
        Parameters param = Parameters.getInstance();
        Paquete paq = param.getPaquete(paquet);
        Oferta of = param.getOferta(oferta);
        return pesOferta[oferta] + paq.getPeso() <= of.getPesomax() &&
                of.getDias() <= param.getMaxDiasPaquete(paquet);
    }

    //////////////////Operadors//////////////////
    private State newMove(int paquet, int oferta) {
        int[] offAssig = new int[ofertesAssignades.length];
        System.arraycopy(ofertesAssignades, 0, offAssig, 0, ofertesAssignades.length);
        double[] pesOff = new double[pesOferta.length];
        System.arraycopy(pesOferta, 0, pesOff, 0, pesOferta.length);
        Paquete paq = Parameters.getInstance().getPaquete(paquet);
        offAssig[paquet] = oferta;
        pesOff[ofertesAssignades[paquet]] -= paq.getPeso();
        pesOff[oferta] += paq.getPeso();
        return new State(offAssig, pesOff);
    }

    private State newSwap(int paquet1, int paquet2) {
        int[] offAssig = new int[ofertesAssignades.length];
        System.arraycopy(ofertesAssignades, 0, offAssig, 0, ofertesAssignades.length);
        double[] pesOff = new double[pesOferta.length];
        System.arraycopy(pesOferta, 0, pesOff, 0, pesOferta.length);

        Parameters param = Parameters.getInstance();
        Paquete paq1 = param.getPaquete(paquet1);
        Paquete paq2 = param.getPaquete(paquet2);

        offAssig[paquet1] = ofertesAssignades[paquet2];
        offAssig[paquet2] = ofertesAssignades[paquet1];

        pesOff[ofertesAssignades[paquet1]] += (paq2.getPeso() - paq1.getPeso());
        pesOff[ofertesAssignades[paquet2]] += (paq1.getPeso() - paq2.getPeso());

        return new State(offAssig, pesOff);
    }

    public boolean canMove(int paquet, int oferta) {
        return paquet >= 0 &&
                paquet < ofertesAssignades.length &&
                oferta != ofertesAssignades[paquet] &&
                canAdd(paquet, oferta);
        //L'index del paquet es valid,
        //no re-assignem el paquet a l'oferta que ja té assignada,
        //l'oferta té capacitat per a acceptar el nou paquet i
        //l'oferta pot entregar el nou paquet a temps.
    }

    public boolean canSwap(int paquet1, int paquet2) {
        if (paquet1 == paquet2) return false;
        //No  intervanviem un paquet per sí mateix
        if (paquet1 < 0 || paquet1 > ofertesAssignades.length) return false;
        if (paquet2 < 0 || paquet2 > ofertesAssignades.length) return false;
        //Els index dels paquets són vàlids
        if (ofertesAssignades[paquet1] == ofertesAssignades[paquet2]) return false;
        //No intervanviem dos paquets de la mateixa oferta

        int oferta1 = ofertesAssignades[paquet1];
        int oferta2 = ofertesAssignades[paquet2];

        Parameters param = Parameters.getInstance();

        Paquete paq1 = param.getPaquete(paquet1);
        Paquete paq2 = param.getPaquete(paquet2);

        Oferta of1 = param.getOferta(oferta1);
        Oferta of2 = param.getOferta(oferta2);

        if (of1.getDias() > param.getMaxDiasPaquete(paquet2)) return false;
        if (pesOferta[oferta1] - paq1.getPeso() + paq2.getPeso() > of1.getPesomax()) return false;
        //El paquet 1 es pot assignar a l'oferta 2

        if (of2.getDias() > param.getMaxDiasPaquete(paquet1)) return false;
        if (pesOferta[oferta2] - paq2.getPeso() + paq1.getPeso() > of2.getPesomax()) return false;
        //El paquet 2 es pot assignar a l'oferta 1

        return true;
    }

    private String actionMove(int paquet, int oferta) {
        Parameters par = Parameters.getInstance();
        return String.format(
                "%s->%s",
                par.getPaquete(paquet),
                par.getOferta(oferta)
        );
    }

    private String actionSwap(int paquet1, int paquet2) {
        Parameters par = Parameters.getInstance();
        return String.format(
                "%s<->%s",
                par.getPaquete(paquet1),
                par.getPaquete(paquet2)
        );
    }

    public Successor move(int paquet, int oferta) {
        return new Successor(
                actionMove(paquet, oferta),
                newMove(paquet, oferta)
        );
    }

    public Successor swap(int paquet1, int paquet2) {
        return new Successor(
                actionSwap(paquet1, paquet2),
                newSwap(paquet1, paquet2)
        );
    }
    //////////////////Operadors//////////////////


    @Override
    public int hashCode() {
        return Arrays.hashCode(ofertesAssignades);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof State)) return false;
        State s = (State) obj;
        return Arrays.equals(ofertesAssignades, s.ofertesAssignades);
    }

    @Override
    public String toString() {
        Parameters par = Parameters.getInstance();
        StringBuilder out = new StringBuilder(ofertesAssignades.length*100);
        //Aproximem la mida de l'string:
        //      lineSeparator() -> 2
        //      "->" -> 2
        //      paquet -> 33 + (d->10)
        //      oferta -> 44 + 2*(d->10)
        //      Cada element = 103 caràcters
        //      Tot això, apareix ofertesAssignades.length vegades
        for (int i = 0; i < ofertesAssignades.length; ++i) {
            if (i != 0) out.append(lineSeparator());
            out.append(
                    String.format(
                            "%s->%s",
                            par.getPaquete(i),
                            par.getOferta(i)
                    )
            );
        }
        return out.toString();
    }

    public long getGenTime() {
        return genTime;
    }

    public double cost() {
        return cost;
    }

    //Cost = kg*$oferta + penalitzacio emmagatzematge
    private double getOneCost(double kg, double preuOferta, int dies){
        double pen;
        switch (dies) {
            case 1:
            case 2:
                pen = 0;
                break;
            case 3:
            case 4:
                pen = 0.25;
                break;
            case 5:
                pen = 0.5;
                break;
            default:
                throw new RuntimeException("Nombre de dies de l'oferta invàlid.");
        }
        return kg*(preuOferta + pen);
    }

    private double getCost() {
        //Ha de retornar el cost total:
        //Suma del pes de cada paquet pel preu per kg de l'oferta assignada al paquet
        Parameters param = Parameters.getInstance();
        Oferta of;
        double cost = 0;
        for (int i = 0; i < pesOferta.length; ++i) {
            of = param.getOferta(i);
            cost += getOneCost(pesOferta[i], of.getPrecio(), of.getDias());
        }
        return cost;
    }

    public long felicitat() {
        return felicitat;
    }

    private long getFelicitat() {
        Parameters param = Parameters.getInstance();
        long fel = 0;
        long tfel;
        for (int i = 0; i < param.getNPaquetes(); ++i) {
            tfel = param.getMinDiasPaquete(i) - param.getOferta(ofertesAssignades[i]).getDias();
            if (tfel > 0) fel += tfel;
        }
        return fel;
    }

    public enum GEN_ALG {
        SEQUENTIAL,
        SORTED
    }

}
