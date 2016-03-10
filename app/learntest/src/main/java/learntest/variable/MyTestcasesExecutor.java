package learntest.variable;

import icsetlv.variable.TestcasesExecutor;
import sav.strategies.junit.JunitResult;

public class MyTestcasesExecutor extends TestcasesExecutor {

	public MyTestcasesExecutor(int valRetrieveLevel) {
		super(valRetrieveLevel);
	}

	@Override
	protected void onFinish(JunitResult jResult) {
	}

}
