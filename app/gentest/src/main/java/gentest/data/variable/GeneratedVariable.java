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
	private int firstStmtIdx;
	private int firstVarId;
	
	public GeneratedVariable(int firstStmtIdx, int firstVarId) {
		Assert.assertTrue(firstStmtIdx >=0, "Negative firstStmtIdx");
		Assert.assertTrue(firstVarId >=0, "Negative firstVarId");
		this.firstStmtIdx = firstStmtIdx;
		this.firstVarId = firstVarId;
	}
	
	private int getNewStmtIdx() {
		return stmts.size() + firstStmtIdx;
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
		updateDataLists(stmt, stmt.getType());
	}

	private int addStatement(Statement stmt) {
		int stmtIdx = getNewStmtIdx();
		getStmts().add(stmt);
		return stmtIdx;
	}
	
	private void updateDataLists(Statement stmt, Class<?> type) {
		int stmtIdx = addStatement(stmt);
		if (type != Void.class) {
			int varId = getNewVarIdx();
			LocalVariable var = new LocalVariable(stmtIdx, varId, type);
			getNewVariables().add(var);
		}
	}

	public void append(RConstructor stmt, int[] paramIds) {
		int varId = getNewVarIdx();
		/* update stmt */
		stmt.setOutVarId(varId);
		int size = stmt.getInputTypes().size();
		int[] inVarIds = new int[size];
		stmt.setInVarIds(inVarIds);
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getOutputType());
	}
	
	public void append(Rmethod stmt, int[] paramIds) {
		if (stmt.hasOutputVar()) {
			int outVarId = getNewVarIdx();
			stmt.setOutVarId(outVarId);
		}
		stmt.setInVarIds(paramIds);
		updateDataLists(stmt, stmt.getReturnType());
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
		updateDataLists(arrayConstructor, arrayConstructor.getOutputType());
	}

	public void append(final RArrayAssignment arrayAssignment) {
		addStatement(arrayAssignment);
		// Search for the previous array constructor statement
		LocalVariable arrayVar = null;
		for (LocalVariable var: getNewVariables()) {
			if (var.getVarId() == arrayAssignment.getArrayVarID()) {
				arrayVar = var;
				break;
			}
		}
		// Re-add it at the last position
		getNewVariables().add(arrayVar);
	}

	public GeneratedVariable newVariable() {
		return new GeneratedVariable(getNewStmtIdx(), getNewVarIdx());
	}
	
	public void append(GeneratedVariable subVariable) {
		stmts.addAll(subVariable.stmts);
		newVariables.addAll(subVariable.newVariables);
	}
}
