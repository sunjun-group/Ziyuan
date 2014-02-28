package tzuyu.lstar;

import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;
import lstar.LStar;
import lstar.LStarException;

public class TestMain {
  public static void main(String[] argv) {
    Alphabet alphabet = new Alphabet();

    alphabet.addSymbol(new TestAlphabet("0"));
    alphabet.addSymbol(new TestAlphabet("1"));

    Teacher teacher = new TestTeacher();
    LStar algorithm = new LStar(null);
    algorithm.setTeacher(teacher);
    algorithm.setAlphabet(alphabet);

    try {
		algorithm.startLearning(alphabet);
	} catch (LStarException e) {
		e.printStackTrace();
		System.exit(0);
	}
    DFA dfa = algorithm.getDFA();
    dfa.print();

  }

}
