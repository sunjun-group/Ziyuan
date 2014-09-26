/**
 * Copyright TODO
 */
package gentest.data;


import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import gentest.data.variable.ISelectedVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 */
public class Sequence {
	private List<LocalVariable> localVariables;
	private Map<Class<?>, List<LocalVariable>> typeVariableMap;
	private Map<Class<?>, LocalVariable> receiversMap;
	private List<Statement> stmts;

	public Sequence() {
		localVariables = new ArrayList<LocalVariable>();
		typeVariableMap = new HashMap<Class<?>, List<LocalVariable>>();
		receiversMap = new HashMap<Class<?>, LocalVariable>();
		stmts = new ArrayList<Statement>();
	}
	
	public void append(ISelectedVariable param) {
		stmts.addAll(param.getStmts());
		for (LocalVariable var : param.getNewVariables()) {
			localVariables.add(var);
			CollectionUtils.getListInitIfEmpty(typeVariableMap, var.getType())
					.add(var);
		}
	}
	
	public void appendReceiver(ISelectedVariable param, Class<?> type) {
		append(param);
		/* the last localvariables must be the receiver constructor statement */
		receiversMap.put(type, localVariables.get(param.getReturnVarId()));
	}
	
	public void append(Rmethod method) {
		stmts.add(method);
		if (method.hasOutputVar()) {
			int newVarId = getVarsSize();
			LocalVariable newVar = new LocalVariable(getStmtsSize() - 1, newVarId, 
					method.getReturnType());
			localVariables.add(newVar);
			method.setOutVarId(newVarId);
		}
	}
	
	public int getStmtsSize() {
		return stmts.size();
	}
	
	public int getVarsSize() {
		return localVariables.size();
	}
	

	public List<Statement> getStmts() {
		return stmts;
	}

	public void setStmts(List<Statement> stmts) {
		this.stmts = stmts;
	}
	
	public int newVarId() {
		return localVariables.size();
	}
	
	public Map<Class<?>, List<LocalVariable>> getTypeVariableMap() {
		return typeVariableMap;
	}
	
	public List<LocalVariable> getVariablesByType(Class<?> type) {
		return getTypeVariableMap().get(type);
	}
	
	public Set<Class<?>> getDeclaredTypes() {
		return typeVariableMap.keySet();
	}

	public LocalVariable getReceiver(Class<?> declaringType) {
		return receiversMap.get(declaringType);
	}

	public void appendMethodExecStmts(Rmethod rmethod,
			List<ISelectedVariable> params) {
		// append input declaration statements
		for (ISelectedVariable param : params) {
			append(param);
		}
		// append method call statement
		append(rmethod);
	}

}
