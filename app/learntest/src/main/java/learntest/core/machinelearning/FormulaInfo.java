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
	boolean learned = false;
	 public FormulaInfo(String node) {
		 this.node = node;
	 }
	 
	 public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n"+node+ " learned "+ learned() + "\n");
		sb.append("trueFalseFormula :\n");
		for(String formula : trueFalseFormula)
			sb.append(formula+" ,\n");
		sb.append("loopFormula :\n");
		for(String formula : loopFormula)
			sb.append(formula+" ,\n");
		return sb.toString();
	}
	 	 
	 public void addLoopFormula(String formula, double acc) {
			loopFormula.add(formula+","+acc);
		}
	 
	 public void addTFFormula(String formula, double acc) {
			trueFalseFormula.add(formula+","+acc);
		}
	 
	 public int learned(){
		 double threshold = 0.5;
		 
		 if (trueFalseFormula.size()>0) {
			String[] info = trueFalseFormula.get(trueFalseFormula.size()-1).split(",");
			double acc = Double.parseDouble(info[1]);
			if (acc > threshold) {
				return 1;
			}
		}
		 if (loopFormula.size()>0) {
			String[] info = loopFormula.get(loopFormula.size()-1).split(",");
			double acc = Double.parseDouble(info[1]);
			if (acc > threshold) {
				return 1;
			}
		}
		 if (trueFalseFormula.size() == 0 && loopFormula.size()>0) {
			return 0;
		}else{
		 return -1;
		}
	 }
}