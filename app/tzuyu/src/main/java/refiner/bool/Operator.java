package refiner.bool;

public enum Operator {
  GE(">="), LE("<="), GT(">"), LT("<"), EQ("=="), NE("!=");

  private final String name;

  private Operator(String op) {
    this.name = op;
  }

  public boolean evaluate(double left, double right) {
    switch (this) {
    case GE:
      return left >= right;
    case LE:
      return left <= right;
    case GT:
      return left > right;
    case LT:
      return left < right;
    case EQ:
      return left == right;
    case NE:
      return left != right;
    default:
      return false;
    }
  }

  @Override
  public String toString() {
    return name;
  }

}
