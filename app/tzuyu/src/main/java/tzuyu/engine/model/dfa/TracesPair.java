package tzuyu.engine.model.dfa;

import java.util.List;

import tzuyu.engine.model.Trace;


public class TracesPair {

  public List<Trace> acceptingTraces;
  public List<Trace> refusingTraces;

  public TracesPair(List<Trace> accept, List<Trace> refuse) {
    this.acceptingTraces = accept;
    this.refusingTraces = refuse;
  }
}
