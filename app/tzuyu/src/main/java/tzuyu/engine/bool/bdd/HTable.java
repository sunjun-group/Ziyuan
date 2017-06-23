package tzuyu.engine.bool.bdd;

import java.util.Hashtable;

public class HTable {
  private Hashtable<Integer, BDDNode> table;

  public HTable() {
    table = new Hashtable<Integer, BDDNode>();
  }

  public void insert(int i, BDDNode l, BDDNode h, BDDNode u) {
    int key = generateKey(i, l, h);
    if (!table.containsKey(key)) {
      table.put(key, u);
    }
  }

  public BDDNode lookup(int i, BDDNode l, BDDNode h) {
    int key = generateKey(i, l, h);
    return table.get(key);
  }

  public boolean member(int i, BDDNode l, BDDNode h) {
    int key = generateKey(i, l, h);
    return table.containsKey(key);
  }

  private int generateKey(int i, BDDNode l, BDDNode h) {
    int nodePair = pair(l.id, h.id);
    int key = pair(i, nodePair) % 15485863;
    return key;
  }

  private int pair(int i, int j) {
    int sum = i + j;
    return sum * (sum + 1) / 2 + i;
  }

}
