package domain.successor.sa;

import aima.search.framework.Successor;
import domain.Parameters;
import domain.State;

import java.util.Collections;
import java.util.List;

/**
 * @author David
 */
public class SASuccessorFunction1 implements SASuccessorFunction {

    @Override
    public List<Successor> getSuccessors(Object state) {
        State current = (State) state;
        Parameters par = Parameters.getInstance();
        int paq = -1;
        int of = -1;
        int max = par.getNPaquetes()*par.getNOfertas();
        int j = 0;
        while (!current.canMove(paq, of) && j++ < max) {
            //Si no hi ha cap estat vàlid, retornem llista buida.
            paq = par.rand(par.getNPaquetes());
            of = par.rand(par.getNOfertas());
        }
        if (j < max)
            return Collections.singletonList(current.move(paq, of));
        return Collections.emptyList();
    }

}
