/**
 * Copyright TODO
 */
package gentest.data;


import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import gentest.data.variable.ISelectedVariable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * VariableId : variable Ids are only used for Local variables using in the sequence,
 * from localVariables to method parameter, receiver, and output of
 * the method.
 * VariableId is actually the index of a localVariable in sequence's localVariables list.
 * therefore, a variableId is valid iff there is a localVariables at that index in sequence local variables. 
 * Except for methodcall, variableId for each statement will be set at initialized time,
 * 		 based on the last indices of stored lists in the sequence,
 * 		not when it is attached to the sequence.
 * For methodCall statement, its outputVarId will be set at the attaching time. 
 */
public class Sequence {
	private List<LocalVariable> localVariables;
	private Map<Type, List<LocalVariable>> typeVariableMap;
	private Map<Class<?>, LocalVariable> receiversMap;
	private List<Statement> stmts;
	private Set<Class<?>> staticClasses; // which the methodCalls belong to

	public Sequence() {
		localVariables = new ArrayList<LocalVariable>();
		typeVariableMap = new HashMap<Type, List<LocalVariable>>();
		receiversMap = new HashMap<Class<?>, LocalVariable>();
		stmts = new ArrayList<Statement>();
		staticClasses = new HashSet<Class<?>>();
	}
	
	public void append(ISelectedVariable param) {
		stmts.addAll(param.getStmts());
		for (LocalVariable var : param.getNewVariables()) {
			addLocalVariable(var);
		}
	}

	private void addLocalVariable(LocalVariable var) {
		localVariables.add(var);
		CollectionUtils.getListInitIfEmpty(typeVariableMap, var.getType()).add(
				var);
	}

	public void appendReceiver(ISelectedVariable param, Class<?> type) {
		append(param);
		/* the last localvariables must be the receiver constructor statement */
		receiversMap.put(type, localVariables.get(param.getReturnVarId()));
	}
	
	public void append(Rmethod method) {
		stmts.add(method);
		if (method.hasReturnType()) {
			int newVarId = getVarsSize();
			LocalVariable newVar = new LocalVariable(getStmtsSize() - 1, newVarId, 
					method.getReturnType());
			addLocalVariable(newVar);
			method.setOutVarId(newVarId);
		}
		if (method.isStatic()) {
			staticClasses.add(method.getDeclaringType());
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

	public Map<Type, List<LocalVariable>> getTypeVariableMap() {
		return typeVariableMap;
	}
	
	public List<LocalVariable> getVariablesByType(Type paramType) {
		return getTypeVariableMap().get(paramType);
	}
	
	public Set<Class<?>> getDeclaredTypes() {
		Set<Class<?>> declaredTypes = new HashSet<Class<?>>();
		declaredTypes.addAll(staticClasses);
		for (Type type : typeVariableMap.keySet()) {
			if (type instanceof Class<?>) {
				declaredTypes.add((Class<?>) type);
			} 
//		TODO LLT: REVIEW	else if (type instanceof generic)
		}
		return declaredTypes;
	}
	
	public LocalVariable getReceiver(Class<?> declaringType) {
		LocalVariable receiver = receiversMap.get(declaringType);
		if (receiver == null) {
			// another chance
			for (Class<?> type : receiversMap.keySet()) {
				if (type.isAssignableFrom(declaringType)) {
					receiver = receiversMap.get(type);
					break;
				}
			}
		}
		return receiver;
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

	@SuppressWarnings("unchecked")
	public <T extends Statement>List<T> getStatementByType(Enum<?> type) {
		List<T> result = new ArrayList<T>();
		for (Statement stmt : stmts) {
			if (stmt.getKind() == type) {
				result.add((T) stmt);
			}
		}
		return result;
	}
}
