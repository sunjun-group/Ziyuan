/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.store.iface.ITypeInitializerStore;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;
import gentest.main.GentestConstants;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;
import sav.strategies.gentest.ISubTypesScanner;

/**
 * @author LLT
 *
 */
public class ValueGeneratorMediator {
	private static Logger log = LoggerFactory.getLogger(ValueGeneratorMediator.class);
	@Inject
	private IVariableStore variableStore;
	@Inject
	private ISubTypesScanner subTypeScanner;
	@Inject
	private ITypeMethodCallStore typeMethodCallsStore;
	@Inject
	private ITypeInitializerStore typeInitializerStore;
	@Inject
	private PrimitiveValueGenerator primitiveGenerator;
	
	public GeneratedVariable generate(IType type, 
			int firstVarId, boolean isReceiver) throws SavException {
		GeneratedVariable variable = new GeneratedVariable(firstVarId);
		return append(variable, 1, type, isReceiver);
	}

	public GeneratedVariable append(GeneratedVariable rootVariable, int level,
			IType type) throws SavException {
		return append(rootVariable, level, type, false);
	}

	/**
	 * @param level: start from 1.
	 */
	public GeneratedVariable append(GeneratedVariable rootVariable, int level,
			IType type, boolean isReceiver) throws SavException {
		GeneratedVariable variable = null;
		List<GeneratedVariable> candidatesInCache = getVariableStore()
				.getVariableByType(type);
		boolean selectFromCache = Randomness
				.weighedCoinFlip(calculateProbToGetValFromCache(candidatesInCache.size()));
		selectFromCache = false;
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
				variable = variable.clone(rootVariable.getNextVarId(),
						toVarId, toStmtIdx);
			}
		}
		
		if (variable == null) {
			boolean goodVariable = false;
			variable = rootVariable.newVariable();
			/* generate the new one*/
			if (PrimitiveValueGenerator.accept(type.getRawType())) {
				goodVariable = primitiveGenerator.doAppend(variable, level, type.getRawType());
			}  else if (level > GentestConstants.VALUE_GENERATION_MAX_LEVEL) {
				log.debug("level of value generation exceeds the limit ({} levels)", GentestConstants.VALUE_GENERATION_MAX_LEVEL);
				ValueGenerator.assignNull(variable, type.getRawType());
			} else if (level > 1 && Randomness
					.weighedCoinFlip(getProbIncreaseByLevel(level, GentestConstants.VALUE_GENERATION_MAX_LEVEL))) {
				// increase the probability of generating null objects for fields
					ValueGenerator.assignNull(variable, type.getRawType());
			} else {
				ValueGenerator generator = ValueGenerator.findGenerator(type, isReceiver);
				generator.setValueGeneratorMediator(this);
				goodVariable = generator.doAppendVariable(variable, level);
			}
			if (goodVariable) {
				getVariableStore().put(type, variable);
			}
		}
		rootVariable.append(variable);
		return variable;
	}

	private double getProbIncreaseByLevel(int level, int maxLevel) {
		if (level <= 3) {
			return 0.4;
		}
		return 0.8; 
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

	public void setPrimitiveGenerator(PrimitiveValueGenerator primitiveGenerator) {
		this.primitiveGenerator = primitiveGenerator;
	}

	public ITypeInitializerStore getTypeInitializerStore() {
		return typeInitializerStore;
	}

	public void setTypeInitializerStore(ITypeInitializerStore typeInitializerStore) {
		this.typeInitializerStore = typeInitializerStore;
	}
	
}
