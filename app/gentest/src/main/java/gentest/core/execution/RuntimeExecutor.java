/**
 * Copyright TODO
 */
package gentest.core.execution;

import gentest.core.data.Sequence;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.StatementVisitor;
import gentest.core.data.variable.ISelectedVariable;

import java.util.List;


/**
 * @author LLT
 * A runtimeExecutor of a {@link Sequence}. 
 * to execute a {@link Sequence} during the generation
 * and run whenever a new method added to the {@link Sequence}
 */
public class RuntimeExecutor extends VariableRuntimeExecutor implements
		StatementVisitor {
	protected Sequence sequence;
	
	public RuntimeExecutor() {
		data = new RuntimeData();
	}

	public void reset(Sequence sequence) {
		super.reset();
		this.sequence = sequence;
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
	
	public Sequence getSequence() {
		return sequence;
	}
}
