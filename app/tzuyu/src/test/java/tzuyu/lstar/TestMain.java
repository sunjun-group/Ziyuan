package tzuyu.lstar;

import lstar.LStar;
import lstar.LStarException;
import sav.common.core.NullPrintStream;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.exception.TzException;

public class TestMain {
	public static void main(String[] argv) throws InterruptedException {
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
		} catch (TzException e) {
			e.printStackTrace();
		}
		DFA dfa = algorithm.getDFA();
		dfa.print(NullPrintStream.instance());
	}

}
