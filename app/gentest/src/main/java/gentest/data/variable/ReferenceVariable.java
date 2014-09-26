/**
 * Copyright TODO
 */
package gentest.data.variable;

import gentest.data.LocalVariable;
import gentest.data.statement.Statement;

import java.util.ArrayList;
import java.util.List;


/**
 * @author LLT
 * 
 */
public class ReferenceVariable extends SelectedVariable {
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
