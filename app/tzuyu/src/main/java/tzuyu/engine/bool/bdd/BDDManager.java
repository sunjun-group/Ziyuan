package tzuyu.engine.bool.bdd;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.True;
import tzuyu.engine.model.Formula;



public class BDDManager {
  private final List<BDDNode> nodes;
  private final HTable hTable;
  private final int variableCount;
  private List<Atom> atoms;
  public final BDDNode zeroNode;
  public final BDDNode oneNode;

  public BDDManager(List<Atom> atoms) {
    this.atoms = atoms;
    int numberOfVariables = atoms.size();
    variableCount = numberOfVariables;
    nodes = new ArrayList<BDDNode>();
    zeroNode = new BDDNode(this, 0, variableCount + 1, null, null);
    nodes.add(zeroNode);
    oneNode = new BDDNode(this, 1, variableCount + 1, null, null);
    nodes.add(oneNode);
    hTable = new HTable();
  }

  public BDDNode mk(int i, BDDNode l, BDDNode h) {
    if (l == h) {
      return l;
    } else if (hTable.member(i, l, h)) {
      return hTable.lookup(i, l, h);
    } else {
      BDDNode u = new BDDNode(this, nodes.size(), i, l, h);
      nodes.add(u);
      hTable.insert(i, l, h, u);
      return u;
    }
  }

  public BDDNode build(Formula t) {
    return recursiveBuild(t, 1);
  }

  private BDDNode recursiveBuild(Formula t, int varIndex) {
    if (varIndex > variableCount) {
      if (t instanceof True) {
        return oneNode;
      } else {// must be zero node
        return zeroNode;
      }
    } else {
      List<Atom> vars = new ArrayList<Atom>();
      vars.add(atoms.get(varIndex - 1));
      List<Integer> vals = new ArrayList<Integer>();
      vals.add(0);
      BDDNode v0 = recursiveBuild(t.restrict(vars, vals), varIndex + 1);

      vals.clear();
      vals.add(1);
      BDDNode v1 = recursiveBuild(t.restrict(vars, vals), varIndex + 1);
      return mk(varIndex, v0, v1);
    }
  }

  public Atom getAtom(int index) {
    return atoms.get(index);
  }

}
