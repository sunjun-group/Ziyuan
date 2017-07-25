/**
 * Copyright TODO
 */
package gentest.core;

import java.util.List;

import com.google.inject.Inject;

import gentest.core.data.IDataProvider;
import gentest.core.data.LocalVariable;
import gentest.core.data.Sequence;
import gentest.core.data.type.IType;
import gentest.core.data.variable.ISelectedVariable;
import gentest.core.data.variable.ReferenceVariable;
import gentest.core.value.generator.IRandomness;
import gentest.core.value.generator.ValueGeneratorMediator;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 * ParamterSelector actually do the both thing
 * automatic generate value for entered type, and statements 
 * for that value.
 */
public class ParameterSelector {
	@Inject
	private IDataProvider<Sequence> sequenceProvider;
	
	@Inject
	private ValueGeneratorMediator valueGenerator;
	
	@Inject
	private IRandomness randomness;
	
	public Sequence getSequence() {
		return sequenceProvider.getData();
	}
	
	public ISelectedVariable selectReceiver(IType type,
			int firstStmtIdx, int firstVarIdx) throws SavException {
		return selectParam(type, firstStmtIdx, firstVarIdx, true);
	}
	
	public ISelectedVariable selectParam(IType type, int firstStmtIdx,
			int firstVarIdx) throws SavException {
		return selectParam(type, firstStmtIdx, firstVarIdx, false);
	}
	
	private ISelectedVariable selectParam(IType type,
			int firstStmtIdx, int firstVarIdx, boolean isReceiver)
			throws SavException {
		SelectionMode selectingMode = randomChooseSelectorType(type, firstStmtIdx - 1, firstVarIdx - 1);
		switch (selectingMode) {
		case REFERENCE:
			return selectReferenceParam(type);
		default:
			return selectGeneratedParam(type, firstVarIdx, isReceiver);
		}
	}
	
	/**
	 * generate new value for parameter.
	 */
	private ISelectedVariable selectGeneratedParam(IType type, int firstVarIdx,
			boolean isReceiver) throws SavException {
		return valueGenerator.generate(type, firstVarIdx, isReceiver);
	}

	/**
	 * return value of parameter by selecting from variables of
	 * previous method call.
	 */
	private ISelectedVariable selectReferenceParam(IType type) {
		LocalVariable randomVisibleVar = Randomness.randomMember(getSequence()
				.getVariablesByType(type));
		return new ReferenceVariable(randomVisibleVar);
	}

	/**
	 * only do random select type if there is any variables for type
	 * available in the sequence.
	 * Otherwise, just exclude REFERENCE option out of selectionMode.
	 * @param curTotalVars 
	 * @param curTotalStmts 
	 */
	public SelectionMode randomChooseSelectorType(IType type, int curTotalStmts, int curTotalVars) {
		List<LocalVariable> existedVars = getSequence().getVariablesByType(type);
		if (CollectionUtils.isEmpty(existedVars)) {
			return SelectionMode.GENERATE_NEW;
		}
		double refProb = 0.3;
		if (curTotalStmts > 15 || existedVars.size() > 5) {
			refProb = 0.8;
		}
		if (randomness.weighedCoinFlip(refProb)) {
			return SelectionMode.REFERENCE;
		}
		return SelectionMode.GENERATE_NEW;
	}
	
	private static enum SelectionMode {
		REFERENCE,
		GENERATE_NEW
	}

	public void setValueGenerator(ValueGeneratorMediator valueGenerator) {
		this.valueGenerator = valueGenerator;
	}
}
