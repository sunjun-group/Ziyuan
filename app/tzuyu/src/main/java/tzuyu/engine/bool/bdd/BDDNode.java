package tzuyu.engine.bool.bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tzuyu.engine.bool.utils.FormulaNegation;
import tzuyu.engine.bool.utils.FormulaUtils;
import tzuyu.engine.model.Formula;



public class BDDNode {
  public final BDDNode low;
  public final BDDNode high;
  public int var;
  public final int id;
  private final BDDManager manager;

  public BDDNode(BDDManager mgr, int i, int varIndex, BDDNode l, BDDNode h) {
    this.manager = mgr;
    this.id = i;
    this.var = varIndex;
    low = l;
    high = h;
  }

  public boolean isTerminal() {
    return id == 0 || id == 1;
  }

  public boolean isZero() {
    return id == 0;
  }

  public boolean isOne() {
    return id == 1;
  }

  public BDDNode and(BDDNode that) {
    return apply(Operator.And, this, that);
  }

  public BDDNode not() {
    return apply(Operator.Xor, this, manager.oneNode);
  }

  /**
   * ITE(b, c) = (this&&b)||(!this&&c)
   * 
   * @param thenBDD
   * @param elseBDD
   * @return the ITE expression where this is the condition
   */
  public BDDNode ite(BDDNode thenBDD, BDDNode elseBDD) {
    BDDNode trueBDD = apply(Operator.And, this, thenBDD);
    BDDNode falseBDD = apply(Operator.And, this.not(), elseBDD);
    return apply(Operator.Or, trueBDD, falseBDD);
  }

  public List<Map<Integer, Integer>> allSAT() {
    return allSATStep(this);
  }

  private List<Map<Integer, Integer>> allSATStep(BDDNode node) {
    if (node.isTerminal()) {
      if (node.isOne()) {
        List<Map<Integer, Integer>> result = 
            new ArrayList<Map<Integer, Integer>>();
        result.add(new HashMap<Integer, Integer>());
        return result;
      } else {
        return new ArrayList<Map<Integer, Integer>>();
      }
    } else {
      List<Map<Integer, Integer>> lowResult = allSATStep(node.low);
      for (int i = 0; i < lowResult.size(); i++) {
        Map<Integer, Integer> map = lowResult.get(i);
        map.put(node.var, 0);
      }

      List<Map<Integer, Integer>> highResult = allSATStep(node.high);
      for (int i = 0; i < highResult.size(); i++) {
        Map<Integer, Integer> map = highResult.get(i);
        map.put(node.var, 1);
      }

      lowResult.addAll(highResult);
      return lowResult;

    }
  }

  private BDDNode apply(Operator op, BDDNode u1, BDDNode u2) {
    GTable gt = new GTable();
    BDDNode node = applyStep(gt, op, u1, u2);
    gt.clear();
    return node;
  }

  private BDDNode applyStep(GTable gt, Operator op, BDDNode u1, BDDNode u2) {
    if (gt.member(u1, u2)) {
      return gt.lookup(u1, u2);
    } else if (u1.isTerminal() && u2.isTerminal()) {
      return op.getValue(u1, u2) ? manager.oneNode : manager.zeroNode;
    } else {
      BDDNode u = null;
      if (u1.var == u2.var) {
        u = manager.mk(u1.var, applyStep(gt, op, u1.low, u2.low),
            applyStep(gt, op, u1.high, u2.high));
      } else if (u1.var < u2.var) {
        u = manager.mk(u1.var, applyStep(gt, op, u1.low, u2),
            applyStep(gt, op, u1.high, u2));
      } else { // u1.var > u2.var
        u = manager.mk(u2.var, applyStep(gt, op, u1, u2.low),
            applyStep(gt, op, u1, u2.high));
      }

      gt.insert(u1, u2, u);

      return u;
    }
  }

  private String nodeDotRepresentation(BDDNode node) {
    StringBuilder sb = new StringBuilder();
    if (node.isTerminal()) {
      sb.append("\t" + node.id + " [label=\"" + node.id
          + "\", shape=square];\n");
    } else {
      sb.append("\t" + node.id + " [label=\"" + node.id + ":" + node.var
          + "\", shape = circle];\n");
      sb.append("\t" + node.id + " -> " + node.low.id + " [style=dashed];\n");
      sb.append("\t" + node.id + " -> " + node.high.id + ";\n");
    }

    return sb.toString();
  }

  public String createDotRepresentation() {

    StringBuffer result = new StringBuffer();
    result.append("digraph BDD {\n\tcenter = true;\n\tsize=\"9,11\";\n");
    List<BDDNode> workingQueue = new ArrayList<BDDNode>();
    List<BDDNode> processed = new ArrayList<BDDNode>();
    BDDNode current = this;
    workingQueue.add(current);

    while (workingQueue.size() > 0) {
      current = workingQueue.remove(0);
      if (processed.contains(current)) {
        continue;
      }
      String nodeStr = nodeDotRepresentation(current);
      processed.add(current);

      result.append(nodeStr);

      if (current.low != null) {
        workingQueue.add(current.low);
      }

      if (current.high != null) {
        workingQueue.add(current.high);
      }
    }
    result.append("}\n");
    return result.toString();
  }

  public boolean equals(Object obj) {
    if (obj instanceof BDDNode) {
      if (obj == this) {
        return true;
      }

      BDDNode node = (BDDNode) obj;

      if (node.id == this.id && node.var == this.var) {
        return true;
      }

      return false;

    } else {
      return false;
    }
  }

  public Formula getFormula() {
    if (this.isOne()) {
      return Formula.TRUE;
    } else if (this.isZero()) {
      return Formula.FALSE;
    } else {
      List<Map<Integer, Integer>> assignments = this.allSAT();

      Formula formula = Formula.FALSE;
      for (int index = 0; index < assignments.size(); index++) {
        Map<Integer, Integer> map = assignments.get(index);
        Formula term = Formula.TRUE;
        for (Integer key : map.keySet()) {
          if (map.get(key) == 0) {
            term = FormulaUtils.and(term,
            		FormulaNegation.not(manager.getAtom(key - 1)));
          } else {
            term = FormulaUtils.and(term, manager.getAtom(key - 1));
          }
        }
        term = FormulaUtils.simplify(term); 
        formula = FormulaUtils.or(formula, term);
      }
      formula = FormulaUtils.simplify(formula);
      return formula;
    }
  }

}

enum Operator {
  And(0), Or(1), Xor(2);

  private final int code;

  private Operator(int code) {
    this.code = code;
  }

  public boolean getValue(BDDNode left, BDDNode right) {
    boolean leftVal = (left.id == 0 ? false : true);
    boolean rightVal = (right.id == 0 ? false : true);
    switch (this.code) {
    case 0:
      return leftVal && rightVal;
    case 1:
      return leftVal || rightVal;
    case 2:
      return leftVal ^ rightVal;
    default:
      return false;
    }
  }

}
