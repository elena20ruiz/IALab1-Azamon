package domain.successor.hc;

import aima.search.framework.Successor;
import domain.Parameters;
import domain.State;

import java.util.LinkedList;
import java.util.List;

/**
 * @author David
 */
public class HCSuccessorFunction2 implements HCSuccessorFunction {

    @Override
    public List<Successor> getSuccessors(Object state) {
        State current = (State) state;
        List<Successor> retList = new LinkedList<>();
        Parameters par = Parameters.getInstance();
        for (int p1 = 0; p1 < par.getNPaquetes(); ++p1)
            for (int p2 = p1+1; p2 < par.getNPaquetes(); ++p2)
                if (current.canSwap(p1, p2))
                    retList.add(current.swap(p1, p2));
        return retList;
    }

}
