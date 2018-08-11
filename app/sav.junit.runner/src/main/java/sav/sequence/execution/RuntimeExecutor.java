/**
 * Copyright TODO
 */
package sav.sequence.execution;

import java.util.List;

import gentest.core.data.Sequence;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.StatementVisitor;
import gentest.core.data.variable.ISelectedVariable;


/**
 * @author LLT
 * A runtimeExecutor of a {@link Sequence}. 
 * to execute a {@link Sequence} during the generation
 * and run whenever a new method is added to the {@link Sequence}
 */
public class RuntimeExecutor extends VariableRuntimeExecutor implements StatementVisitor {
	public RuntimeExecutor() {
		data = new RuntimeData();
	}
	
	public boolean execute(Rmethod rmethod, List<ISelectedVariable> selectParams) {
		for (ISelectedVariable param : selectParams) {
			if (!execute(param)) {
				return successful;
			}
		}
		try {
			rmethod.accept(this);
		} catch (Throwable e) {
			successful = false;
		}
		return successful;
	}
	
}
