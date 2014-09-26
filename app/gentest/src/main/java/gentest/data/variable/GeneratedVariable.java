/**
 * Copyright TODO
 */
package gentest.data.variable;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import gentest.data.LocalVariable;
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

	private void updateDataLists(Statement stmt, Class<?> type) {
		int stmtIdx = getNewStmtIdx();
		int varId = getNewVarIdx();
		getStmts().add(stmt);
		LocalVariable var = new LocalVariable(stmtIdx, varId, type);
		getNewVariables().add(var);
	}

	public void append(RConstructor stmt) {
		int varId = getNewVarIdx();
		/* update stmt */
		stmt.setOutVarId(varId);
		int[] inVarIds = new int[stmt.getInputTypes().size()];
		/* previous newVariables must be the input of this constructor
		 * so we loop back the newVariables list to set back to the stmt inputs */
		for (int i = 0; i < stmt.getInputTypes().size(); i++) {
			inVarIds[i] = varId - i;
		}
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getOutputType());
	}
	
	@Override
	public int getReturnVarId() {
		LocalVariable lastVar = CollectionUtils.getLast(newVariables);
		Assert.assertTrue(lastVar != null); 
		return lastVar.getVarId();
	}
}
