package domain.successor.hc;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.List;

/**
 * @author David
 */
public interface HCSuccessorFunction extends SuccessorFunction {
    //Totes les funcions successores que s'utilitzin en HC han de generar TOTS els successors.

    @Override
    List<Successor> getSuccessors(Object state);
}
