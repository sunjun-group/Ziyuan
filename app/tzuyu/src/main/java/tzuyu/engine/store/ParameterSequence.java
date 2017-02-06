package tzuyu.engine.store;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Variable;


public class ParameterSequence {

  List<Variable> variables = new ArrayList<Variable>();

  /**
   * Add a variable to the list. If the list contains this variable, ignore.
   * 
   * @param var
   */
  public void add(Variable var) {
    // First to remove the variable if it presents in the sequence
    variables.remove(var);
    // Add the var to the last position such that the latest recently
    // used one is always at the end of list;
    variables.add(var);
  }

  public List<Variable> toJDKList() {

    return variables;
  }

  /**
   * For get, we don't need to keep the LRT property
   * 
   * @param i
   * @return
   */
  public Variable get(int i) {
    if (i < 0 || i > size() - 1) {
      throw new IllegalArgumentException("index out of bound" + i);
    }
    Variable ret = variables.get(i);
    return ret;
  }

  public int size() {
    return variables.size();
  }
}
