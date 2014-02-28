package tzuyu.lstar;

import lstar.ReportHandler;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;

/**
 * This class is a testing teacher which is implemented for the example in the
 * paper of Dana Angluin
 * 
 * @author Spencer Xiao
 * 
 */
public class TestTeacher implements Teacher<TzuYuAlphabet> {

	public boolean membershipQuery(Trace str) {
		if (str.isEpsilon()) {
			return true;
		}

		String strVal = str.toString();

		if (strVal.equals("11") || strVal.equals("22") || strVal.equals("1221")
				|| strVal.equals("2211") || strVal.equals("2222")
				|| strVal.equals("1212")) {
			return true;
		}

		return false;
	}

	public Trace candidateQuery(DFA dfa) {
		if (dfa.getStateSize() == 2) {
			// return counter example "11"
			Trace str = new Trace(new TestAlphabet("1"));

			str.appendAtTail(new TestAlphabet("1"));
			return str;
		}

		if (dfa.getStateSize() == 3) {
			// return counter example "011"
			Trace str = new Trace(new TestAlphabet("0"));
			str.appendAtTail(new TestAlphabet("2"));
			str.appendAtTail(new TestAlphabet("2"));
			return str;
		}

		if (dfa.getStateSize() == 3) {
			return null;
		}

		return new Trace();
	}

	public TestTeacher() {
	}

	public void report(ReportHandler<TzuYuAlphabet> reporter) {
		
	}

	public void setInitAlphabet(TzuYuAlphabet sig) {
		
	}

}
