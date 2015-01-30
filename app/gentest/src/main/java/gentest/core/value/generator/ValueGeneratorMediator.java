/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;
import gentest.main.GentestConstants;

import java.lang.reflect.Type;
import java.util.List;

import com.google.inject.Inject;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;
import sav.strategies.gentest.ISubTypesScanner;

/**
 * @author LLT
 *
 */
public class ValueGeneratorMediator {
	@Inject
	private IVariableStore variableStore;
	@Inject
	private ISubTypesScanner subTypeScanner;
	@Inject
	private ITypeMethodCallStore typeMethodCallsStore;
	
	public GeneratedVariable generate(Class<?> clazz, Type type, 
			int firstVarId, boolean isReceiver) throws SavException {
		GeneratedVariable variable = new GeneratedVariable(firstVarId);
		return append(variable, 1, clazz, type, isReceiver);
	}

	public GeneratedVariable append(GeneratedVariable rootVariable, int level,
			Class<?> clazz, Type type) throws SavException {
		return append(rootVariable, level, clazz, type, false);
	}

	public GeneratedVariable append(GeneratedVariable rootVariable, int level,
			Class<?> clazz, Type type, boolean isReceiver) throws SavException {
		GeneratedVariable variable = null;
		List<GeneratedVariable> candidatesInCache = getVariableStore()
				.getVariableByType(type, clazz);
		boolean selectFromCache = Randomness
				.weighedCoinFlip(calculateProbToGetValFromCache(candidatesInCache.size()));
		if (selectFromCache) {
			/* trying to lookup in cache */
			variable = Randomness.randomMember(candidatesInCache);
			if (variable != null) {
				int toVarId = variable.getNewVariables().size();
				int toStmtIdx = variable.getStmts().size();
				if (CollectionUtils.isNotEmpty(variable.getObjCuttingPoints())) {
					int[] stopPoint = Randomness.randomMember(variable
							.getObjCuttingPoints());
					toVarId = stopPoint[0];
					toStmtIdx = stopPoint[1];
				}
				variable = variable.duplicate(rootVariable.getNextVarId(),
						toVarId, toStmtIdx);
			}
		}
		
		if (variable == null) {
			boolean goodVariable = false;
			variable = rootVariable.newVariable();
			/* generate the new one*/
			if (PrimitiveValueGenerator.accept(clazz, type)) {
				goodVariable = PrimitiveValueGenerator.doAppend(variable, level, clazz);
			}  else if (level > GentestConstants.VALUE_GENERATION_MAX_LEVEL) {
				ValueGenerator.assignNull(variable, clazz);
			} else {
				ValueGenerator generator = ValueGenerator.findGenerator(clazz,
						type, isReceiver);
				generator.setValueGeneratorMediator(this);
				goodVariable = generator.doAppend(variable, level, clazz);
			}
			if (goodVariable) {
				getVariableStore().put(type, clazz, variable);
			}
		}
		rootVariable.append(variable);
		return variable;
	}

	protected double calculateProbToGetValFromCache(int varsSizeInCache) {
		double prob = GentestConstants.PROBABILITY_OF_CACHE_VALUE
				+ ((double) varsSizeInCache / GentestConstants.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE)
					* (1 - GentestConstants.PROBABILITY_OF_CACHE_VALUE);
		if (prob == 1) {
			prob -= 0.1;
		}
		return prob;
	}

	public IVariableStore getVariableStore() {
		return variableStore;
	}

	public void setVariableStore(IVariableStore variableStore) {
		this.variableStore = variableStore;
	}

	public ISubTypesScanner getSubTypeScanner() {
		return subTypeScanner;
	}

	public void setSubTypeScanner(ISubTypesScanner subTypeScanner) {
		this.subTypeScanner = subTypeScanner;
	}

	public ITypeMethodCallStore getTypeMethodCallsStore() {
		return typeMethodCallsStore;
	}

	public void setTypeMethodCallsStore(ITypeMethodCallStore typeMethodCallsStore) {
		this.typeMethodCallsStore = typeMethodCallsStore;
	}
}
