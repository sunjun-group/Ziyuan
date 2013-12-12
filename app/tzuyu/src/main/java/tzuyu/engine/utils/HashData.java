package tzuyu.engine.utils;

public class HashData {
  private static final int poly = 0x88888EEF;
  private int m = -1;

  public void reset() {
    m = -1;
  }

  public int getValue() {
    return (m >>> 4) ^ (m & 15);
  }

  public void add(int value) {
    if (m < 0) {
      m += m;
      m ^= poly;
    } else {
      m += m;
    }
    m ^= value;
  }

  public void add(long value) {
    add((int) (value ^ (value >>> 32)));
  }

  public void add(Object o) {
    if (o != null) {
      add(o.hashCode());
    }
  }

  public void add(boolean b) {
    add(b ? 1231 : 1237);
  }
}
