package domain.successor.hc;

import aima.search.framework.Successor;
import domain.Parameters;
import domain.State;

import java.util.LinkedList;
import java.util.List;

/**
 * @author David
 */
public class HCSuccessorFunction1 implements HCSuccessorFunction {

    @Override
    public List<Successor> getSuccessors(Object state) {
        State current = (State) state;
        List<Successor> retList = new LinkedList<>();
        Parameters par = Parameters.getInstance();
        for (int paq = 0; paq < par.getNPaquetes(); ++paq)
            for (int of = 0; of < par.getNOfertas(); ++of)
                if (current.canMove(paq, of))
                    retList.add(current.move(paq, of));
        return retList;
    }

}
