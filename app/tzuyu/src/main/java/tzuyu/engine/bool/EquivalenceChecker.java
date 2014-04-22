package tzuyu.engine.bool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tzuyu.engine.bool.bdd.BDDManager;
import tzuyu.engine.bool.bdd.BDDNode;
import tzuyu.engine.bool.formula.Atom;
import tzuyu.engine.model.Formula;



/**
 * The equivalence checker is responsible for checking whether two boolean
 * formula are equal by converting them to OBDD and then comapre the two OBDD
 * for equivalence.
 * 
 * @author Spencer Xiao
 * 
 */
public class EquivalenceChecker {

  public static boolean checkEquivalence(Formula a, Formula b) {
    if (a == b) {
      return true;
    }

    // First step: Find the atomics referenced in the two formula,

    List<Atom> aAtomics = a.getAtomics();
    List<Atom> bAtomics = b.getAtomics();
    // If the atomics referenced in the tow formula are not equal,
    // then the formula cannot be equal.
    if (!checkAtoms(aAtomics, bAtomics)) {
      return false;
    }

    BDDManager manager = new BDDManager(aAtomics);

    BDDNode aRoot = manager.build(a);
    BDDNode bRoot = manager.build(b);
    return checkNode(aRoot, bRoot);
  }

  private static boolean checkAtoms(List<Atom> a, List<Atom> b) {
    Set<Atom> aSet = new HashSet<Atom>(a);
    Set<Atom> bSet = new HashSet<Atom>(b);
    return aSet.equals(bSet);
  }

  private static boolean checkNode(BDDNode a, BDDNode b) {
    if (a.isTerminal() || b.isTerminal()) {
      return a.equals(b);
    }
    if (a.equals(b)) {
      return checkNode(a.low, b.low) && checkNode(a.high, b.high);
    } else {
      return false;
    }
  }
}
