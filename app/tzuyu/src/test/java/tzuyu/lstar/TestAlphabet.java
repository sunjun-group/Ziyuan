package tzuyu.lstar;

import tzuyu.engine.model.Action;

public class TestAlphabet extends Action {
  private String text;

  @Override
  public String toString() {
    return this.text;
  }

  public TestAlphabet(String text) {
    this.text = text;
  }

  @Override
  public boolean isConstructor() {
    return false;
  }

}
