package tzuyu.engine.model.dfa;

public class RunningResult {

  /**
   * The last transition.
   */
  public final Transition tran;

  /**
   * Number of transitions have been run.
   */
  public final int runLen;

  public RunningResult(Transition tran, int len) {
    this.tran = tran;
    this.runLen = len;
  }
}
