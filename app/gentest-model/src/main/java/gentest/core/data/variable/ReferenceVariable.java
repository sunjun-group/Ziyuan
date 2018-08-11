/**
 * Copyright TODO
 */
package gentest.core.data.variable;

import gentest.core.data.LocalVariable;
import gentest.core.data.statement.Statement;

import java.util.ArrayList;
import java.util.List;


/**
 * @author LLT
 * 
 */
public class ReferenceVariable extends SelectedVariable {
	private static final long serialVersionUID = -3067070535038873137L;
	private LocalVariable refVar;

	public ReferenceVariable(LocalVariable refVar) {
		this.refVar = refVar;
	}

	@Override
	public int getReturnVarId() {
		return refVar.getVarId();
	}

	@Override
	public List<Statement> getStmts() {
		return new ArrayList<Statement>(0);
	}

}
