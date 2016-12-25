package tzuyu.engine.model.dfa;

import java.util.ArrayList;
import java.util.List;

public class State {

  public String id;
  public List<Transition> trans;

  public State() {
    id = "{}";
    trans = new ArrayList<Transition>();
  }
}
