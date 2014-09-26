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
public abstract class SelectedVariable implements ISelectedVariable {
	protected List<Statement> stmts;
	protected List<LocalVariable> newVariables;
	
	public SelectedVariable() {
		stmts = new ArrayList<Statement>();
		newVariables = new ArrayList<LocalVariable>();
	}
	
	@Override
	public List<Statement> getStmts() {
		return stmts;
	}

	public List<LocalVariable> getNewVariables() {
		return newVariables;
	}

	public void setNewVariables(List<LocalVariable> newVariables) {
		this.newVariables = newVariables;
	}
	
}
