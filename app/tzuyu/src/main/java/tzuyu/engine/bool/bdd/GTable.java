package tzuyu.engine.bool.bdd;

import java.util.Hashtable;

public class GTable {

  private Hashtable<Integer, BDDNode> table;

  public GTable() {
    table = new Hashtable<Integer, BDDNode>();
  }

  public void insert(BDDNode u1, BDDNode u2, BDDNode u) {
    int key = generateKey(u1, u2);
    if (!table.containsKey(key)) {
      table.put(key, u);
    }
  }

  public BDDNode lookup(BDDNode u1, BDDNode u2) {
    int key = generateKey(u1, u2);
    return table.get(key);
  }

  public boolean member(BDDNode u1, BDDNode u2) {
    int key = generateKey(u1, u2);
    return table.containsKey(key);
  }

  private int generateKey(BDDNode u1, BDDNode u2) {
    int key = pair(u1.var, u2.var) % 15485863;
    return key;
  }

  private int pair(int i, int j) {
    int sum = i + j;
    return sum * (sum + 1) / 2 + i;
  }

  public void clear() {
    this.table.clear();
  }
}
