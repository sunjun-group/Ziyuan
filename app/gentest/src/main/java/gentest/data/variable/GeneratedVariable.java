/**
 * Copyright TODO
 */
package gentest.data.variable;

import gentest.data.LocalVariable;
import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GeneratedVariable extends SelectedVariable {
	private int returnedVarId = Statement.INVALID_VAR_ID;
	private int firstVarId;
	
	public GeneratedVariable(int firstVarId) {
		Assert.assertTrue(firstVarId >=0, "Negative firstVarId");
		this.firstVarId = firstVarId;
	}
	
	private int getNewVarIdx() {
		return newVariables.size() + firstVarId;
	}
	
	public int getLastVarId() {
		return newVariables.size() - 1 + firstVarId;
	}
	
	public void commitReturnVarIdIfNotExist() {
		if (returnedVarId == Statement.INVALID_VAR_ID) {
			returnedVarId = getReturnVarId();
		}
	}

	public void append(RAssignment stmt) {
		/* update stmt */
		stmt.setOutVarId(getNewVarIdx());
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getType(), true);
	}

	private void addStatement(Statement stmt) {
		getStmts().add(stmt);
	}
	
	private void updateDataLists(Statement stmt, Class<?> type, boolean addVariable) {
		addStatement(stmt);
		if (addVariable && type != Void.class) {
			int varId = getNewVarIdx();
			LocalVariable var = new LocalVariable(varId, type);
			getNewVariables().add(var);
		}
	}

	public void append(RConstructor stmt, int[] paramIds) {
		int varId = getNewVarIdx();
		/* update stmt */
		stmt.setOutVarId(varId);
		stmt.setInVarIds(paramIds);
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getOutputType(), true);
	}
	
	public void append(Rmethod stmt, int[] paramIds, boolean addVariable) {
		if (stmt.hasReturnType() && addVariable) {
			int outVarId = getNewVarIdx();
			stmt.setOutVarId(outVarId);
		}
		stmt.setInVarIds(paramIds);
		updateDataLists(stmt, stmt.getReturnType(), addVariable);
	}
	
	@Override
	public int getReturnVarId() {
		if (returnedVarId == Statement.INVALID_VAR_ID) {
			LocalVariable lastVar = CollectionUtils.getLast(newVariables);
			Assert.assertTrue(lastVar != null); 
			return lastVar.getVarId();
		}
		return returnedVarId;
	}

	public void append(final RArrayConstructor arrayConstructor) {
		arrayConstructor.setOutVarId(getNewVarIdx());
		updateDataLists(arrayConstructor, arrayConstructor.getOutputType(), true);
	}

	public void append(final RArrayAssignment arrayAssignment) {
		addStatement(arrayAssignment);
		// there is no new generated variable to add to newVariables list
	}

	public GeneratedVariable newVariable() {
		return new GeneratedVariable(getNewVarIdx());
	}
	
	public void append(GeneratedVariable subVariable) {
		stmts.addAll(subVariable.stmts);
		newVariables.addAll(subVariable.newVariables);
	}
}
