package learntest.core.machinelearning;

import java.util.LinkedList;
import java.util.List;

/** 
* @author ZhangHr 
*/
public class FormulaInfo{
	String node;
	List<String> trueFalseFormula = new LinkedList<>();
	List<String> loopFormula = new LinkedList<>();
	 public FormulaInfo(String node) {
		 this.node = node;
	 }
	 
	 public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(node+"\n");
		sb.append("trueFalseFormula : ");
		for(String formula : trueFalseFormula)
			sb.append(formula+" , ");
		sb.append("\n");
		sb.append("loopFormula : ");
		for(String formula : loopFormula)
			sb.append(formula+" , ");
		return sb.toString();
	}
}