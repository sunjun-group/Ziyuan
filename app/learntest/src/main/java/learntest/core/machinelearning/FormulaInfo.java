package learntest.core.machinelearning;

import java.util.LinkedList;
import java.util.List;

import cfg.CfgNode;
import learntest.plugin.utils.Settings;

/**
 * @author ZhangHr
 */
public class FormulaInfo {
	CfgNode node;
	List<String> trueFalseFormula = new LinkedList<>();
	List<String> loopFormula = new LinkedList<>();
	boolean learned = false;
	Integer learnedState = null;

	public FormulaInfo(CfgNode node) {
		this.node = node;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n" + node + " learned " + getLearnedState() + "\n");
		sb.append("trueFalseFormula :\n");
		for (String formula : trueFalseFormula)
			sb.append(formula + " ,\n");
		sb.append("loopFormula :\n");
		for (String formula : loopFormula)
			sb.append(formula + " ,\n");
		return sb.toString();
	}

	public void addLoopFormula(String formula, double acc) {
		loopFormula.add(formula + "," + acc);
	}

	public void addTFFormula(String formula, double acc) {
		trueFalseFormula.add(formula + "," + acc);
	}

	public static final int VALID = 1, INVALID = -1, NO = 0;

	/**
	 * 
	 * @return 1 valid formula, 0 no formula, -1 invalid formula
	 */
	public int getLearnedState() {
		if (learnedState != null) {
			return learnedState;
		}
		double threshold = Settings.formulaAccThreshold;

		if (trueFalseFormula.size() > 0) {
			String[] info = trueFalseFormula.get(trueFalseFormula.size() - 1).split(",");
			double acc = Double.parseDouble(info[1]);
			if (acc > threshold) {
				learnedState = VALID;
				return learnedState;
			}
		}
		if (loopFormula.size() > 0) {
			String[] info = loopFormula.get(loopFormula.size() - 1).split(",");
			double acc = Double.parseDouble(info[1]);
			if (acc > threshold) {
				learnedState = VALID;
				return learnedState;
			}
		}
		if (trueFalseFormula.size() == 0 && loopFormula.size() == 0) {
			learnedState = NO;
		} else {
			learnedState = INVALID;
		}
		return learnedState;
	}

	public CfgNode getNode() {
		return node;
	}

	public List<String> getTrueFalseFormula() {
		return trueFalseFormula;
	}
}