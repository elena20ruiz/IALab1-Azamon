package domain.successor.sa;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.List;

/**
 * @author David
 */
public interface SASuccessorFunction extends SuccessorFunction {
    //Totes les funcions successores que s'utilitzin en SA han de generar UN successor aleatori.

    @Override
    List<Successor> getSuccessors(Object state);

}
