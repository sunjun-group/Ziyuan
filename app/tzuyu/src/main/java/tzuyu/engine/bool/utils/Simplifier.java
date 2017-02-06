package tzuyu.engine.bool.utils;

import tzuyu.engine.bool.bdd.BDDManager;
import tzuyu.engine.bool.bdd.BDDNode;
import tzuyu.engine.model.Formula;

/**
 * The formula simplifier simplifies a boolean formula by converting it to an
 * OBDD and returns the disjunction of the satisfying assignments of the final
 * OBDD
 * 
 * @author Spencer Xiao
 * 
 */
public class Simplifier {

  public static Formula simplify(Formula f) {
    Formula newFormula = FormulaUtils.simplify(f); 

    BDDManager manager = new BDDManager(newFormula.getAtomics());

    BDDNode root = manager.build(newFormula);

    return root.getFormula();
  }
  
}
