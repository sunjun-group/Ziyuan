package tzuyu.engine.instrument;

import java.util.ArrayList;
import java.util.List;

import tester.IInstrumentor;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.model.Variable;


public class TzuYuInstrumentor implements IInstrumentor {

  private boolean isInProgress;

  private List<Prestate> prestates;

  public TzuYuInstrumentor() {
    isInProgress = false;
    prestates = new ArrayList<Prestate>();
  }

  @Override
  public List<Prestate> getRuntimeTrace() {
    List<Prestate> retCopy = new ArrayList<Prestate>();
    retCopy.addAll(prestates);
    
    //NOTE: we need a copy of the states, don't directly return the prestates
    return retCopy;
  }

  @Override
  public void instrument(
      Statement stmt, List<Variable> vars, List<Object> runtimeObjects) {
    Prestate state = Prestate.log(vars, runtimeObjects);
    prestates.add(state);
  }

  @Override
  public void startInstrument() {
    if (isInProgress) {
      throw new TzuYuException("the instrumentation is alreay in progress");
    } else {
      isInProgress = true;
      prestates.clear();
    }
  }

  @Override
  public void endInstrument() {
    if (!isInProgress) {
      throw new TzuYuException("the instrumentation is finished");
    } else {
      isInProgress = false;
    }

  }

}
