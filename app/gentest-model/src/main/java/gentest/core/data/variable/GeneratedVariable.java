/**
 * Copyright TODO
 */
package gentest.core.data.variable;

import static gentest.main.GentestConstants.INVALID_VAR_ID;

import java.util.ArrayList;
import java.util.List;

import gentest.core.data.LocalVariable;
import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.core.data.statement.StatementCloner;
import gentest.utils.CollectionUtils;
import gentest.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class GeneratedVariable extends SelectedVariable {
	private static final long serialVersionUID = 8343808394886770235L;
	private int returnedVarId = INVALID_VAR_ID;
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
	 * this stores list of [varId, stmtIdx]
	 */
	private List<int[]> objCuttingPoints;
	
	public GeneratedVariable(int firstVarId) {
		this(firstVarId, new ArrayList<Statement>(), new ArrayList<LocalVariable>());
	}
	
	public void reset() {
		if (objCuttingPoints != null) {
			objCuttingPoints.clear();
		}
		stmts.clear();
		newVariables.clear();
	}
	
	public GeneratedVariable(int firstVarId, List<Statement> stmts,
			List<LocalVariable> vars) {
		super(stmts, vars);
		if (firstVarId < 0) {
			throw new IllegalArgumentException("Negative firstVarId");
		}
		this.firstVarId = firstVarId;
	}
	
	public int getNextVarId() {
		return newVariables.size() + firstVarId;
	}
	
	public int getLastVarId() {
		return newVariables.size() - 1 + firstVarId;
	}
	
	public void commitReturnVarIdIfNotExist() {
		if (returnedVarId == INVALID_VAR_ID) {
			returnedVarId = getReturnVarId();
		}
	}
	
	public void setReturnedVarId(int returnedVarId) {
		this.returnedVarId = returnedVarId;
	}

	/**
	 * append statement
	 **/
	public void append(RAssignment stmt) {
		/* update stmt */
		stmt.setOutVarId(getNextVarId());
		/* update stmt list and variables list */
		updateDataLists(stmt, stmt.getType(), true);
	}

	private void addStatement(Statement stmt) {
		getStmts().add(stmt);
	}
	
	private boolean updateDataLists(Statement stmt, Class<?> type,
			boolean addVariable) {
		addStatement(stmt);
		if (addVariable && type != Void.class) {
			int varId = getNextVarId();
			// TODO LLT(class to type)
			LocalVariable var = new LocalVariable(varId, type);
			getNewVariables().add(var);
		}
		return true;
	}

	public boolean append(RConstructor stmt, int[] paramIds) {
		int varId = getNextVarId();
		/* update stmt */
		stmt.setOutVarId(varId);
		stmt.setInVarIds(paramIds);
		/* update stmt list and variables list */
		return updateDataLists(stmt, stmt.getOutputType(), true);
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
		if (returnedVarId == INVALID_VAR_ID) {
			LocalVariable lastVar = CollectionUtils.getLast(newVariables);
			if (lastVar == null) {
				throw new IllegalArgumentException();
			}
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

	public GeneratedVariable clone(int firstVarId, int toVarId, int toStmtIdx) {
		GeneratedVariable cloneVar = new GeneratedVariable(firstVarId,
				new ArrayList<Statement>(toStmtIdx),
				new ArrayList<LocalVariable>(toVarId));
		int offset = firstVarId - this.firstVarId;
		for (int i = 0; i < toVarId; i++) {
			LocalVariable var = newVariables.get(i);
			// TODO LLT(class to type)
			LocalVariable newVar = new LocalVariable(var.getVarId() + offset, 
					var.getType());
			cloneVar.newVariables.add(newVar);
		}
		StatementCloner cloner = new StatementCloner(cloneVar.stmts,
				offset);
		for (int i = 0; i < toStmtIdx; i++) {
			Statement stmt = stmts.get(i);
			stmt.accept(cloner);
		}
		if (returnedVarId != INVALID_VAR_ID) {
			cloneVar.returnedVarId = returnedVarId + offset;
		}
		return cloneVar;
	}

	public int getFirstVarId() {
		return firstVarId;
	}
	
	public boolean isEmpty() {
		return stmts.isEmpty();
	}
	
	public int[] getLastFragmentIdx() {
		if (objCuttingPoints == null) {
			return new int[] { 0, 0 };
		}
		return objCuttingPoints.get(objCuttingPoints.size() - 1);
	}
	
	public int getLastFragmentIdxStmtId() {
		return getLastFragmentIdx()[1];
	}

	public void removeLastFragment() {
		int[] lastFragmentIdx = getLastFragmentIdx();
		CollectionUtils.removeLastElements(newVariables, lastFragmentIdx[0]);
		CollectionUtils.removeLastElements(stmts, lastFragmentIdx[1]);
		CollectionUtils.removeLast(objCuttingPoints);
	}

	public List<Statement> getLastFragmentStmts() {
		return stmts.subList(getLastFragmentIdxStmtId(), stmts.size());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("GeneratedVariable, stmts: ");
		sb.append(StringUtils.join(stmts, "\n"));
		return sb.toString();
	}
	
}
