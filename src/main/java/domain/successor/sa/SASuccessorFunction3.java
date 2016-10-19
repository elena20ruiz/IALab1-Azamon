package domain.successor.sa;

import aima.search.framework.Successor;
import domain.Parameters;

import java.util.List;

/**
 * @author David
 */
public class SASuccessorFunction3 implements SASuccessorFunction {

    //p paquets
    //o ofertes

    @Override
    public List<Successor> getSuccessors(Object state) {
        Parameters param = Parameters.getInstance();
        double a = 1d/param.getNPaquetes(); //a = 1/p
        double b = 1d/param.getNOfertas(); //b = 1/o
        if (Parameters.getInstance().rand(a+b) < b)
            return new SASuccessorFunction1().getSuccessors(state); //O(p·o)
        else return new SASuccessorFunction2().getSuccessors(state); //O(p^2)
    }

    //p(S1)=p/(p+o)
    //p(S2)=o/(p+o)
    //p(S1)+p(S2)=1

    //C(S1)=f(S1)·p(S1)=p·o·p/(p+o)=o·p^2/(p+o)
    //C(S2)=f(S2)·p(S2)=p·p·o/(p+o)=o·p^2/(p+o)
    //C(S1)=C(S2)

}
