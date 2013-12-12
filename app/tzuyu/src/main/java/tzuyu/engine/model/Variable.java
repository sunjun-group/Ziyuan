package tzuyu.engine.model;

import java.io.Serializable;
import java.util.List;

public class Variable implements Comparable<Variable>, Serializable {

  private static final long serialVersionUID = 3401683653584869789L;
  /**
   * The sequence that creates this variable, the variable is created or last
   * modified by the last statement in the sequence.
   */
  public final Sequence owner;
  /**
   * The index of the variable in the last statement of sequence. -1 means the
   * return value of the last statement; 0 is the receiver of the last statement
   * if it is an instance method; 1 to other positive index are the parameters
   * of the last statement
   */
  public final int argIdx;

  /**
   * The statement index which the variable refers to
   */
  public final int stmtIdx;

  private Class<?> type = null;

  public Variable(Sequence seq, int stmtIdx, int varIdx) {
    if (seq == null) {
      throw new IllegalArgumentException("missing owner");
    }

    if (stmtIdx < 0 || stmtIdx > seq.size() - 1) {
      throw new IllegalArgumentException("index falls out [0,owner.size()-1]:"
          + stmtIdx);
    }
    this.owner = seq;
    this.stmtIdx = stmtIdx;
    this.argIdx = varIdx;
  }

  public Variable(Sequence sequence, int index) {
    this(sequence, index, -1);
  }

  public Class<?> getType() {
    if (type != null) {
      return type;
    }

    Statement stmt = owner.getStatement(stmtIdx);
    if (argIdx == -1) {
      type = stmt.getOutputType();
    } else {
      List<Class<?>> types = stmt.getInputTypes();
      type = types.get(argIdx);
    }
    return type;
  }

  public int getDeclIndex() {
    return stmtIdx;
  }

  public int getVarIndex() {
    return this.argIdx;
  }
  
  public Statement getDeclaringStatement() {
    return owner.getStatement(stmtIdx);
  }

  @Override
  public int compareTo(Variable o) {
    if (o == null)
      throw new IllegalArgumentException();
    if (o.owner != this.owner)
      throw new IllegalArgumentException();
    int compare1 = (new Integer(stmtIdx).compareTo(new Integer(o.stmtIdx)));
    if (compare1 == 0) {
      return new Integer(this.argIdx).compareTo(new Integer(o.argIdx));
    } else {
      return compare1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Variable)) {
      return false;
    }

    Variable other = (Variable) o;

    return this.owner.equals(other.owner) && this.stmtIdx == other.stmtIdx
        && this.argIdx == other.argIdx;
  }

  @Override
  public int hashCode() {
    return this.stmtIdx * 31 + this.owner.hashCode() * 19 + this.argIdx;
  }

  @Override
  public String toString() {
    return getType().getSimpleName() + "(" + stmtIdx + "," + argIdx + ")";
  }

  public String getName() {
   return "var"+ Integer.toString(stmtIdx);
  }

}
