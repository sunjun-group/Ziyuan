/**
 * Copyright TODO
 */
package gentest.core.execution;

import gentest.core.data.Sequence;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.StatementVisitor;
import gentest.core.data.variable.ISelectedVariable;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author LLT
 * A runtimeExecutor of a {@link Sequence}. 
 * to execute a {@link Sequence} during the generation
 * and run whenever a new method is added to the {@link Sequence}
 */
public class RuntimeExecutor extends VariableRuntimeExecutor implements StatementVisitor {
	private static Logger log = LoggerFactory.getLogger(RuntimeExecutor.class);
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
			visitRmethod(rmethod, VariableRuntimeExecutor.methodExecTimeout);
		} catch (Throwable e) {
			log.debug(e.getMessage());
			successful = false;
		}
		return successful;
	}
	
}
