package domain.successor.sa;

import aima.search.framework.Successor;
import domain.Parameters;
import domain.State;

import java.util.Collections;
import java.util.List;

/**
 * @author David
 */
public class SASuccessorFunction2 implements SASuccessorFunction {

    @Override
    public List<Successor> getSuccessors(Object state) {
        State current = (State) state;
        Parameters par = Parameters.getInstance();
        int p1 = -1;
        int p2 = -1;
        int max = par.getNPaquetes()*par.getNOfertas();
        int j = 0;
        while (!current.canSwap(p1, p2) && j++ < max) {
            //Si no hi ha cap estat vàlid, retornem llista buida.
            p1 = par.rand(par.getNPaquetes());
            p2 = par.rand(par.getNPaquetes());
        }
        if (j < max)
            return Collections.singletonList(current.swap(p1, p2));
        return Collections.emptyList();
    }

}
