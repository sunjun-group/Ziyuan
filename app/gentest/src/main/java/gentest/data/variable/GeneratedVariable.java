/**
 * Copyright TODO
 */
package gentest.data.variable;

import java.util.ArrayList;
import java.util.List;

import gentest.data.LocalVariable;
import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import gentest.value.StatementDuplicator;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GeneratedVariable extends SelectedVariable {
	private int returnedVarId = Statement.INVALID_VAR_ID;
	private int firstVarId;
	/**
	 * the field objCuttingPoints is used for Object value.
	 * The generated sequence for object initialization will be like this:
	 * Foo foo = new Foo();
	 * foo.add(x);
	 * foo.add(y);
	 * ..
	 * objCuttingPoints will store the points at which object method will be call (to change state of the object itself)
	 * this field is added for select random sequence of object initialization in cache.  
	 */
	private List<int[]> objCuttingPoints;
	
	public GeneratedVariable(int firstVarId) {
		this(firstVarId, new ArrayList<Statement>(), new ArrayList<LocalVariable>());
	}
	
	public GeneratedVariable(int firstVarId, List<Statement> stmts,
			List<LocalVariable> vars) {
		super(stmts, vars);
		Assert.assertTrue(firstVarId >=0, "Negative firstVarId");
		this.firstVarId = firstVarId;
	}
	
	public int getNextVarId() {
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
	
	public void setReturnedVarId(int returnedVarId) {
		this.returnedVarId = returnedVarId;
	}

	public void append(RAssignment stmt) {
		/* update stmt */
		stmt.setOutVarId(getNextVarId());
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getType(), true);
	}

	private void addStatement(Statement stmt) {
		getStmts().add(stmt);
	}
	
	private void updateDataLists(Statement stmt, Class<?> type, boolean addVariable) {
		addStatement(stmt);
		if (addVariable && type != Void.class) {
			int varId = getNextVarId();
			LocalVariable var = new LocalVariable(varId, type);
			getNewVariables().add(var);
		}
	}

	public void append(RConstructor stmt, int[] paramIds) {
		int varId = getNextVarId();
		/* update stmt */
		stmt.setOutVarId(varId);
		stmt.setInVarIds(paramIds);
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getOutputType(), true);
	}
	
	public void append(Rmethod stmt, int[] paramIds, boolean addVariable) {
		if (stmt.hasReturnType() && addVariable) {
			int outVarId = getNextVarId();
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
		arrayConstructor.setOutVarId(getNextVarId());
		updateDataLists(arrayConstructor, arrayConstructor.getOutputType(), true);
	}

	public void append(final RArrayAssignment arrayAssignment) {
		addStatement(arrayAssignment);
		// there is no new generated variable to add to newVariables list
	}

	public GeneratedVariable newVariable() {
		return new GeneratedVariable(getNextVarId());
	}
	
	public void append(GeneratedVariable subVariable) {
		stmts.addAll(subVariable.stmts);
		newVariables.addAll(subVariable.newVariables);
	}
	
	public void newCuttingPoint() {
		if (objCuttingPoints == null) {
			objCuttingPoints = new ArrayList<int[]>();
		}
		objCuttingPoints.add(new int[]{newVariables.size(), stmts.size()});
	}
	
	/**
	 * a cutting point is specified by (excluded) last varId,
	 * and (excluded) last statement idx
	 */
	public List<int[]> getObjCuttingPoints() {
		return objCuttingPoints;
	}

	public GeneratedVariable duplicate(int firstVarId, int toVarId, int toStmtIdx) {
		GeneratedVariable duplicateVar = new GeneratedVariable(firstVarId,
				new ArrayList<Statement>(toStmtIdx),
				new ArrayList<LocalVariable>(toVarId));
		int offset = firstVarId - this.firstVarId;
		for (int i = 0; i < toVarId; i++) {
			LocalVariable var = newVariables.get(i);
			LocalVariable newVar = new LocalVariable(var.getVarId() + offset, 
					var.getType());
			duplicateVar.newVariables.add(newVar);
		}
		StatementDuplicator duplicator = new StatementDuplicator(duplicateVar.stmts,
				offset);
		try {
			for (int i = 0; i < toStmtIdx; i++) {
				Statement stmt = stmts.get(i);
				stmt.accept(duplicator);
			}
		} catch (Throwable e) {
			// TODO LLT: exception handling
			e.printStackTrace();
		}
		if (returnedVarId != Statement.INVALID_VAR_ID) {
			duplicateVar.returnedVarId = returnedVarId + offset;
		}
		return duplicateVar;
	}

}
