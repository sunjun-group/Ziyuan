/**
 * Copyright TODO
 */
package gentest.data.variable;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import gentest.data.LocalVariable;
import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.statement.Statement;



/**
 * @author LLT
 *
 */
public class GeneratedVariable extends SelectedVariable {
	private int firstStmtIdx;
	private int firstVarIdx;
	
	public GeneratedVariable(int firstStmtIdx, int firstVarId) {
		Assert.assertTrue(firstStmtIdx >=0, "Negative firstStmtIdx");
		Assert.assertTrue(firstVarId >=0, "Negative firstVarId");
		this.firstStmtIdx = firstStmtIdx;
		this.firstVarIdx = firstVarId;
	}
	
	private int getNewStmtIdx() {
		return stmts.size() + firstStmtIdx;
	}
	
	private int getNewVarIdx() {
		return newVariables.size() + firstVarIdx;
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
		int varId = getNewVarIdx();
		LocalVariable var = new LocalVariable(stmtIdx, varId, type);
		getNewVariables().add(var);
	}

	public void append(RConstructor stmt) {
		int varId = getNewVarIdx();
		/* update stmt */
		stmt.setOutVarId(varId);
		int size = stmt.getInputTypes().size();
		int[] inVarIds = new int[size];
		/* previous newVariables must be the input of this constructor
		 * so we loop back the newVariables list to set back to the stmt inputs */
		for (int i = 0; i < size; i++) {
			inVarIds[i] = varId - size + i;
		}
		/* TODO LLT: check if update inputVarIds for this stnt is redundant or not*/
		stmt.setInVarIds(inVarIds);
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getOutputType());
	}
	
	@Override
	public int getReturnVarId() {
		LocalVariable lastVar = CollectionUtils.getLast(newVariables);
		Assert.assertTrue(lastVar != null); 
		return lastVar.getVarId();
	}

	public void append(final RArrayConstructor arrayConstructor) {
		arrayConstructor.setOutVarId(getNewVarIdx());
		updateDataLists(arrayConstructor, arrayConstructor.getOutputType());
	}

	public void append(final RArrayAssignment arrayAssignment) {
		// No new variable
		addStatement(arrayAssignment);
	}

}
