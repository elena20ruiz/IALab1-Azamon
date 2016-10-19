package domain.successor.hc;

import aima.search.framework.Successor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author David
 */
public class HCSuccessorFunction3 implements HCSuccessorFunction {

    @Override
    public List<Successor> getSuccessors(Object state) {
        Set<Successor> successors = new HashSet<>();
        successors.addAll(new HCSuccessorFunction1().getSuccessors(state));
        successors.addAll(new HCSuccessorFunction2().getSuccessors(state));
        return new ArrayList<>(successors);
    }

}
