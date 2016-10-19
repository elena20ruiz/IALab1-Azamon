package domain;

import IA.Azamon.Oferta;
import IA.Azamon.Paquete;
import IA.Azamon.Paquetes;
import IA.Azamon.Transporte;

import java.util.List;
import java.util.Random;

import static IA.Azamon.Paquete.*;
import static java.util.Collections.unmodifiableList;

/**
 * @author David
 */
public class Parameters {

    private static Parameters instance;
    private final List<Oferta> ofertas; //Conjunt d'ofertes
    private final List<Paquete> paquetes; //Conjunt de paquets

    private final Random random;

    private Parameters(List<Paquete> paquetes, List<Oferta> ofertas, int seed) {
        this.ofertas = ofertas;
        this.paquetes = paquetes;
        random = new Random(seed);
    }


    public Oferta getOferta(int i) {
        return ofertas.get(i);
    }

    public Paquete getPaquete(int i) {
        return paquetes.get(i);
    }

    public int getMinDiasPaquete(int i) {
        switch (paquetes.get(i).getPrioridad()) {
            case PR1:
                return 1;
            case PR2:
                return 2;
            case PR3:
                return 4;
            default:
                throw new RuntimeException("Prioritat desconeguda.");
        }
    }

    public int getMaxDiasPaquete(int i) {
        switch (paquetes.get(i).getPrioridad()) {
            case PR1:
                return 1;
            case PR2:
                return 3;
            case PR3:
                return 5;
            default:
                throw new RuntimeException("Prioritat desconeguda.");
        }
    }


    public int getNOfertas() {
        return ofertas.size();
    }

    public int getNPaquetes() {
        return paquetes.size();
    }


    public int rand(int limit) {
        return random.nextInt(limit);
    }

    public double rand(double limit) {
        return random.nextDouble()*limit;
    }

    public static Parameters getInstance() {
        return instance;
    }

    public static void ini(int npaq, double proporcion, int seed) {
        Paquetes paq = new Paquetes(npaq, seed);
        instance = new Parameters(
                unmodifiableList(paq),
                unmodifiableList(new Transporte(paq, proporcion, seed)),
                seed
        );
    }

}
