package tzuyu.engine.model;

import java.io.PrintStream;
import java.util.List;

public abstract class StatementKind {

  public abstract Class<?> getReturnType();

  public abstract List<Class<?>> getInputTypes();

  public abstract ExecutionOutcome execute(Object[] inputVals, PrintStream out);

  public abstract boolean isPrimitive();

  public abstract boolean hasNoArguments();

  public abstract String toParseableString();

  
  public abstract void appendCode(Variable newVar, List<Variable> inputVars, 
      StringBuilder b);
  
  public abstract boolean hasReceiverParameter();

  public boolean isConstructor() {
    return false;
  }

  public boolean isStatic() {
    return false;
  }

  @Override
  public String toString() {
    return toParseableString();
  }

}
