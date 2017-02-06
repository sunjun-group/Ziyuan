package tzuyu.engine.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;


/**
 * This class stores all the generated variables for parameters of a
 * {@link StatementKind}. This store can be later used by execution engine to
 * execute the statement under all generated variables such that the new guard
 * condition would not always fail.
 * 
 * @author Spencer Xiao
 * 
 */
public class MethodParameterStore {

  private Map<ParameterIndex, ParameterSequence> generated = 
      new LinkedHashMap<ParameterIndex, ParameterSequence>();

  public void add(StatementKind stmt, int index, Variable var) {
    ParameterIndex paramIndex = new ParameterIndex(stmt, index);

    ParameterSequence param = generated.get(paramIndex);

    if (param == null) {
      param = new ParameterSequence();
      generated.put(paramIndex, param);
    }

    param.add(var);
  }

  public List<Variable> get(StatementKind stmt, int index) {

    ParameterIndex paramIndex = new ParameterIndex(stmt, index);

    ParameterSequence param = generated.get(paramIndex);
    if (param == null) {
      return new ArrayList<Variable>();
    }

    return param.toJDKList();
  }

  public Variable getLRU(StatementKind stmt, int argIndex, int index) {
    ParameterIndex paramIndex = new ParameterIndex(stmt, argIndex);

    ParameterSequence param = generated.get(paramIndex);
    if (param == null || param.size() == 0) {
      return null;
    }
    return param.get(index);
  }

  /**
   * Get the number of parameters generated for the non-receiver argument.
   * 
   * @param stmt
   * @param index
   * @return
   */
  public int getParameterSize(StatementKind stmt, int index) {
    ParameterIndex parameterIndex = new ParameterIndex(stmt, index);

    ParameterSequence parameter = this.generated.get(parameterIndex);
    if (parameter == null) {
      return 0;
    }

    return parameter.size();
  }
}

class ParameterIndex implements Serializable {
  private static final long serialVersionUID = -2535561004146654785L;

  public final StatementKind stmt;
  public final int paramIndex;

  public ParameterIndex(StatementKind statement, int index) {
    this.stmt = statement;
    this.paramIndex = index;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof ParameterIndex)) {
      return false;
    }

    ParameterIndex param = (ParameterIndex) o;

    return param.stmt.equals(stmt) && param.paramIndex == paramIndex;
  }

  @Override
  public int hashCode() {

    return stmt.hashCode() * 31 + paramIndex;
  }

  @Override
  public String toString() {
    return stmt.toString() + ":" + paramIndex;
  }
}
