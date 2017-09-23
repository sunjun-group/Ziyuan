package tzuyu.core.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastructure.Utilities;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import tzuyu.core.main.DataStructureGeneration.CodeType;
import tzuyu.core.mutantbug.FilesBackup;

public class DataStructureSelectiveSampling {
	
	private DataStructureLearning learner;
	
	private Map<String, List<String>> ssMap = new HashMap<String, List<String>>();
	
	public DataStructureSelectiveSampling(DataStructureLearning learner) {
		this.learner = learner;
	}
	
	public void selectiveToNull(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, FilesBackup backup) throws Exception {
		for (Variable var : selectiveVars) {
			if (var.getType().equals("int")) continue;
			
			if (!ssMap.containsKey("toNull")) {
				ssMap.put("toNull", new ArrayList<String>());
			}
			
			if (ssMap.get("toNull").contains(var.getFullName())) continue;
			else ssMap.get("toNull").add(var.getFullName());
			
			backup.backup(origFile);
				
			File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.NULL, var);
			learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
			
			learner.backup(backup, origFile);
		}
	}
	
	public void selectiveToConst(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, int c, FilesBackup backup) throws Exception {
		List<Integer> consts = Arrays.asList(c, c - 1, c + 1);
		
		for (Variable var : selectiveVars) {
			if (!var.getType().equals("int")) continue;
			
			for (Integer i : consts) {
				if (!ssMap.containsKey("toConst")) {
					ssMap.put("toConst", new ArrayList<String>());
				}
				
				String s = "";
				s += var.getFullName() + "-" + i;
				
				if (ssMap.get("toConst").contains(s)) continue;
				else ssMap.get("toConst").add(s);
				
				backup.backup(origFile);
					
				File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.CONST, var, i);
				learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
				
				learner.backup(backup, origFile);
			}
		}
	}
	
	public void selectiveToInc(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, int c, FilesBackup backup) throws Exception {
		List<Integer> consts = Arrays.asList(c, c - 1, c + 1, -c, -c - 1, -c + 1);
				
		for (Variable var : selectiveVars) {
			if (!var.getType().equals("int")) continue;
			
			for (Integer i : consts) {
				if (!ssMap.containsKey("toInc")) {
					ssMap.put("toInc", new ArrayList<String>());
				}
				
				String s = "";
				s += var.getFullName() + "-" + i;
				
				if (ssMap.get("toInc").contains(s)) continue;
				else ssMap.get("toInc").add(s);
				
				backup.backup(origFile);
					
				File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.INC,
						var, var, i);
				learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
				
				learner.backup(backup, origFile);
			}
		}
	}
	
	public void selectiveToNew(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, FilesBackup backup) throws Exception {
		for (Variable var : selectiveVars) {
			if (var.getType().equals("int")) continue;
			
			if (!ssMap.containsKey("toNew")) {
				ssMap.put("toNew", new ArrayList<String>());
			}
			
			if (ssMap.get("toNew").contains(var.getFullName())) continue;
			else ssMap.get("toNew").add(var.getFullName());
			
			backup.backup(origFile);
				
			File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.NEW, var);
			learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
			
			learner.backup(backup, origFile);
		}
	}
	
	public void selectiveToField(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, FilesBackup backup) throws Exception {
		for (Variable var : selectiveVars) {
			if (var.getType().equals("int")) continue;
			
			if (!ssMap.containsKey("toField")) {
				ssMap.put("toField", new ArrayList<String>());
			}
			
			if (ssMap.get("toField").contains(var.getFullName())) continue;
			else ssMap.get("toField").add(var.getFullName());
			
			backup.backup(origFile);
				
			File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.FIELD, var);
			learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
			
			learner.backup(backup, origFile);
		}
	}
	
	public void selectiveToItself(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, FilesBackup backup) throws Exception {
		for (Variable var : selectiveVars) {
			if (var.getType().equals("int")) continue;
			
			if (!ssMap.containsKey("toItself")) {
				ssMap.put("toItself", new ArrayList<String>());
			}
			
			if (ssMap.get("toItself").contains(var.getFullName())) continue;
			else ssMap.get("toItself").add(var.getFullName());
			
			backup.backup(origFile);
				
			File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.ITSELF, var);
			learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
			
			learner.backup(backup, origFile);
		}
	}
	
	public void selectiveToOthers(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, FilesBackup backup) throws Exception {
		List<List<Variable>> totalPairs = new ArrayList<List<Variable>>();
		
		Variable firstVar = null;
		
		for (int i = 0; i < selectiveVars.size(); i++) {
			if (i == 0) firstVar = selectiveVars.get(i);
			else {
				Variable secondVar = selectiveVars.get(i);
				List<Variable> pair = new ArrayList<Variable>();
				pair.add(firstVar);
				pair.add(secondVar);
				totalPairs.add(pair);
			}
		}
		
		for (List<Variable> pair : totalPairs) {
			if (!pair.get(0).getType().equals(pair.get(1).getType())) continue;
			
			if (!ssMap.containsKey("toOther")) {
				ssMap.put("toOther", new ArrayList<String>());
			}
			
			String s = "";
			s += pair.get(0).getFullName() + "-" + pair.get(1).getFullName();
			
			if (ssMap.get("toOther").contains(s)) continue;
			else ssMap.get("toOther").add(s);
			
			backup.backup(origFile);
				
			File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.OTHERS, pair);
			learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
			
			learner.backup(backup, origFile);
		}
	}
	
	public void selectiveSwap(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> selectiveVars,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, FilesBackup backup) throws Exception {
		List<List<Variable>> totalPairs = new ArrayList<List<Variable>>();
		List<List<Variable>> pairsVars = Utilities.comb(selectiveVars, 2);
		
		// why we have a permutation here
		// it seems that it is not necessary in swapping
		// anyway, the normalizing matrix step remove
		// redundant rows so this is not a problem
		for (List<Variable> pair : pairsVars) {
			totalPairs.addAll(Utilities.perm(pair));
		}
				
		for (List<Variable> pair : totalPairs) {
			if (!pair.get(0).getType().equals(pair.get(1).getType())) continue;
			
			if (!ssMap.containsKey("toSwap")) {
				ssMap.put("toSwap", new ArrayList<String>());
			}
			
			String s = "";
			s += pair.get(0).getFullName() + "-" + pair.get(1).getFullName();
			
			if (ssMap.get("toSwap").contains(s)) continue;
			else ssMap.get("toSwap").add(s);
			
			backup.backup(origFile);
				
			File selectiveFile = learner.addCode(params, origFile, learnLoc.getLineNo(), CodeType.SWAP, pair);
			learner.addCheckingCode(params, learnLoc, learnVars, testResults, origFile, selectiveFile);
			
			learner.backup(backup, origFile);
		}
	}

}
