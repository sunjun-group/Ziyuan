package lstar;

import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;
import lstar.LStar;
import lstar.Teacher;

public class TestMain {
  public static void main(String[] argv) {
    Alphabet alphabet = new Alphabet();

    alphabet.addSymbol(new TestAlphabet("0"));
    alphabet.addSymbol(new TestAlphabet("1"));

    Teacher teacher = new TestTeacher();
    LStar algorithm = new LStar(teacher);
    algorithm.setAlphabet(alphabet);

    algorithm.startLearning();
    DFA dfa = algorithm.getDFA();
    dfa.print();

  }

}
