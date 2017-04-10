package tzuyu.core.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import datastructure.AddBagCodeVisitor;
import datastructure.AddCheckingCodeVisitor;
import datastructure.AddConstCodeVisitor;
import datastructure.AddFieldCodeVisitor;
import datastructure.AddIncCodeVisitor;
import datastructure.AddItselfCodeVisitor;
import datastructure.AddNewCodeVisitor;
import datastructure.AddNullCodeVisitor;
import datastructure.AddOthersCodeVisitor;
import datastructure.AddSwapCodeVisitor;
import datastructure.DataStructureTemplate;
import datastructure.LearnDataStructureInvariant;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.vm.VMConfiguration;
import tzuyu.core.main.DataStructureGeneration.CodeType;
import tzuyu.core.mutantbug.FilesBackup;
import tzuyu.core.mutantbug.Recompiler;

public class DataStructureLearning {
	
	private DataStructureGeneration gen;
	
	private String resultPath;
	
	public DataStructureLearning(DataStructureGeneration gen) {
		this.gen = gen;
	}
	
	public void learnInv(DataStructureGenerationParams params,
			BreakPoint learnLoc, List<Variable> learnVars, List<Boolean> origTestResults) 
					throws Exception {
//		 = new ArrayList<Boolean>(origTestResults);
		
		String target = gen.appContext.getAppData().getTarget();
		resultPath = target.substring(0, target.lastIndexOf('/')) + "/results/";
		
//		FileUtils.cleanDirectory(new File(resultPath)); 
		
		File origFile = new File(ClassUtils.getJFilePath(gen.appContext.getAppData().getSrc(),
				params.getTestingClassName()));
		
		FilesBackup backup = FilesBackup.startBackup();
		backup.backup(origFile);
		
		File checkFile = addCode(params, origFile, learnLoc.getLineNo(), CodeType.CHECKING,
				learnVars, gen.heapTemplates, gen.pureTemplates, gen.bagTemplates);
		
		recompile(origFile, checkFile);
		
		List<Boolean> testResults = gen.runTestCases(params, params.getJunitClassNames(), false);
		
		backup(backup, origFile);
		
		LearnDataStructureInvariant learner = new LearnDataStructureInvariant(
				new ArrayList<Boolean>(testResults), resultPath);
		String oldInv = learner.learn();
		String newInv = "";
		
		System.out.println("Inv without ss = " + oldInv);
		
		DataStructureSelectiveSampling ss = new DataStructureSelectiveSampling(this);
		boolean b = false;
		
//		for (int iii = 0; iii <= 0; iii++) {
		while (true) {
			
			File folder = new File(resultPath);
			File[] files = folder.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return !name.equals(".DS_Store");
				}
			});
			//(dir, name) -> !name.equals(".DS_Store")
			
			List<Integer> tempIdx = flattenInv(oldInv);
			
			if (tempIdx.isEmpty()) {
				List<Variable> selectiveVars = new ArrayList<Variable>(learnVars);
				
				System.out.println("gohere");
				
				ss.selectiveToNull(params, learnLoc, selectiveVars, learnVars,
						testResults, origFile, backup);
				ss.selectiveToNew(params, learnLoc, selectiveVars, learnVars,
						testResults, origFile, backup);
				
				ss.selectiveToField(params, learnLoc, selectiveVars, learnVars,
						testResults, origFile, backup);
				ss.selectiveToItself(params, learnLoc, selectiveVars, learnVars,
						testResults, origFile, backup);
//				
//				ss.selectiveSwap(params, learnLoc, selectiveVars, learnVars,
//						testResults, origFile, backup);
//				
//				for (Variable firstVar : selectiveVars) {
//					List<Variable> otherVars = new ArrayList<Variable>();
//					for (Variable learnVar : learnVars) {
//						if (!firstVar.getFullName().equals(learnVar.getFullName()) &&
//								firstVar.getType().equals(learnVar.getType())) {
//							otherVars.add(learnVar);
//						}
//					}
//					otherVars.add(0, firstVar);
//					
//					ss.selectiveToOthers(params, learnLoc, otherVars, learnVars,
//							testResults, origFile, backup);
//				}
//				
//				ss.selectiveToInc(params, learnLoc, selectiveVars, learnVars,
//						testResults, origFile, 0, backup);
//				
//				ss.selectiveToConst(params, learnLoc, selectiveVars, learnVars,
//						testResults, origFile, 0, backup);
			} else {
				for (int i : tempIdx) {
					String template = files[i].getName();
					
					int i1 = template.indexOf('-');
					int i2 = template.indexOf('-', i1 + 1);
					int i3 = template.indexOf('.');
					
					String tempName = template.substring(i1 + 1, i2);
					
					String[] vars = template.substring(i2 + 1, i3).split("-");
					
					List<Variable> selectiveVars = new ArrayList<Variable>();
					int c = Integer.MIN_VALUE;
					
					for (String var : vars) {
						if (var.matches("-?\\d+")) {
							c = Integer.parseInt(var);
							continue;
						}
						
						for (Variable learnVar : learnVars) {
							if (learnVar.getFullName().equals(var) || var.contains("_" + learnVar.getFullName() + "_")) {
								CollectionUtils.addIfNotNullNotExist(selectiveVars, learnVar);
								break;
							}
						}
					}
					
					if (tempName.equals("Node")) {
						ss.selectiveToNull(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						ss.selectiveToNew(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						
						ss.selectiveToField(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						ss.selectiveToItself(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						
						Variable firstVar = selectiveVars.get(0);
						List<Variable> otherVars = new ArrayList<Variable>();
						for (Variable learnVar : learnVars) {
							if (!firstVar.getFullName().equals(learnVar.getFullName()) &&
									firstVar.getType().equals(learnVar.getType())) {
								otherVars.add(learnVar);
							}
						}
						otherVars.add(0, firstVar);
						
						ss.selectiveToOthers(params, learnLoc, otherVars, learnVars,
								testResults, origFile, backup);
					} else if (tempName.equals("Null")) {
						ss.selectiveToNew(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						
						ss.selectiveToField(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						ss.selectiveToItself(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
					} else if (tempName.equals("Eq")) {
						ss.selectiveToNull(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						ss.selectiveToNew(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
					} else if (tempName.equals("Gt0") || tempName.equals("Lt0") || tempName.equals("PEq0") ||
							tempName.equals("Gt") || tempName.equals("PEq") || tempName.equals("GtPMC") ||
							tempName.equals("GtPPC") || tempName.equals("GtPMC") || 
							tempName.equals("GtMPC") || tempName.equals("GtMMC") ||
							tempName.equals("PEqPPC") || tempName.equals("PEqPMC") ||
							tempName.equals("PEqMPC") || tempName.equals("PEqMMC") ||
							tempName.equals("GtC") || tempName.equals("LtC") ||
							tempName.equals("PEqC")) {
						List<Variable> intVars = new ArrayList<Variable>();
						List<Variable> heapVars = new ArrayList<Variable>();
						
						for (Variable var : selectiveVars) {
							if (var.getType().equals("int"))
								intVars.add(var);
							else
								heapVars.add(var);
						}
						
						if (!heapVars.isEmpty()) {
							// user-defined
							ss.selectiveToNull(params, learnLoc, heapVars, learnVars,
									testResults, origFile, backup);
							ss.selectiveToNew(params, learnLoc, heapVars, learnVars,
									testResults, origFile, backup);
							
							ss.selectiveToField(params, learnLoc, heapVars, learnVars,
									testResults, origFile, backup);
							ss.selectiveToItself(params, learnLoc, heapVars, learnVars,
									testResults, origFile, backup);
							
							ss.selectiveSwap(params, learnLoc, heapVars, learnVars,
									testResults, origFile, backup);
							
							for (Variable firstVar : heapVars) {
								List<Variable> otherVars = new ArrayList<Variable>();
								for (Variable learnVar : learnVars) {
									if (!firstVar.getFullName().equals(learnVar.getFullName()) &&
											firstVar.getType().equals(learnVar.getType())) {
										otherVars.add(learnVar);
									}
								}
								otherVars.add(0, firstVar);
								
								ss.selectiveToOthers(params, learnLoc, otherVars, learnVars,
										testResults, origFile, backup);
							}
						}
						
						if (!intVars.isEmpty()) {
							if (tempName.equals("GtC") || tempName.equals("LtC") ||
									tempName.equals("PEqC"))
//							if (tempName.equals("Gt0") || tempName.equals("Lt0") || tempName.equals("PEq0"))
								ss.selectiveToConst(params, learnLoc, intVars, learnVars,
										testResults, origFile, c, backup);
							
							if (tempName.equals("GtPPC") || tempName.equals("GtPMC") || 
									tempName.equals("GtMPC") || tempName.equals("GtMMC") ||
									tempName.equals("PEqPPC") || tempName.equals("PEqPMC") ||
									tempName.equals("PEqMPC") || tempName.equals("PEqMMC"))
//							if (tempName.equals("Gt") || tempName.equals("PEq"))
								ss.selectiveToInc(params, learnLoc, intVars, learnVars,
										testResults, origFile, c, backup);
						}
					}
					
					else {
						// user-defined
						ss.selectiveToNull(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						ss.selectiveToNew(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						
						ss.selectiveToField(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						ss.selectiveToItself(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						
						ss.selectiveSwap(params, learnLoc, selectiveVars, learnVars,
								testResults, origFile, backup);
						
						for (Variable firstVar : selectiveVars) {
							List<Variable> otherVars = new ArrayList<Variable>();
							for (Variable learnVar : learnVars) {
								if (!firstVar.getFullName().equals(learnVar.getFullName()) &&
										firstVar.getType().equals(learnVar.getType())) {
									otherVars.add(learnVar);
								}
							}
							otherVars.add(0, firstVar);
							
							ss.selectiveToOthers(params, learnLoc, otherVars, learnVars,
									testResults, origFile, backup);
						}
					}
					
				}
			}
			
			learner = new LearnDataStructureInvariant(new ArrayList<Boolean>(testResults), resultPath);
			newInv = learner.learn();
			
			System.out.println("Inv after ss = " + newInv);
			
			if (newInv.equals(oldInv)) break;
			else oldInv = newInv;
		}
	}
	
	private List<Integer> flattenInv(String inv) {
		List<Integer> tempIdx = new ArrayList<Integer>();
		
		inv = inv.replaceAll(" ", "");
		String[] ss = inv.split("[\\[\\],]");
		
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i];
			
			if (s.matches("\\d+")) {
				CollectionUtils.addIfNotNullNotExist(tempIdx, Integer.parseInt(s));
			}
		}
		
		return tempIdx;
	}
	
	public void addCheckingCode(DataStructureGenerationParams params,
			BreakPoint learnLoc,
			List<Variable> learnVars, List<Boolean> testResults, 
			File origFile, File selectiveFile) throws Exception {
		if (selectiveFile != null) {
			recompile(origFile, selectiveFile);

			File checkFile = addCode(params, origFile, learnLoc.getLineNo() + 1, CodeType.CHECKING,
					learnVars, gen.heapTemplates, gen.pureTemplates, gen.bagTemplates);
			recompile(origFile, checkFile);

			List<Boolean> testResultsNew = gen.runTestCases(params, params.getJunitClassNames(), false);
			testResults.addAll(testResultsNew);
		}
	}
	
	public void backup(FilesBackup backup, File origFile) throws Exception {
		String target = gen.appContext.getAppData().getTarget();
		
		if (backup != null) {
			backup.restoreAll();
			Recompiler recompiler = new Recompiler(new VMConfiguration(gen.appContext.getAppData()));
			recompiler.recompileJFile(target, origFile);
		}
	}
	
	private void recompile(File origFile, File newFile) throws Exception {
		AppJavaClassPath appClasspath = gen.appContext.getAppData();
		
		// overwrite the original file with the new file
		FileUtils.copyFile(newFile, origFile, false);
					
		// recompile the new file
		Recompiler recompiler = new Recompiler(new VMConfiguration(appClasspath));
		recompiler.recompileJFile(appClasspath.getTarget(), origFile);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public File addCode(DataStructureGenerationParams params, File file, int lineNo,
			CodeType index, Object... infos) throws Exception {
		// parse the original file
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		
		ModifierVisitorAdapter visitor = null;
		
		switch (index) {
		case CHECKING:
			List<Variable> learnVars = (List<Variable>) (infos[0]);
			List<DataStructureTemplate> heapTemplates = (List<DataStructureTemplate>) (infos[1]);
			List<DataStructureTemplate> pureTemplates = (List<DataStructureTemplate>) (infos[2]);
			List<DataStructureTemplate> bagTemplates = (List<DataStructureTemplate>) (infos[3]);
			
			visitor = new AddCheckingCodeVisitor(params.getMethodName(), lineNo, learnVars,
					heapTemplates, pureTemplates, bagTemplates, gen.consts, resultPath);
			break;
		case NULL:
			Variable nullVar = (Variable) (infos[0]);
			
			visitor = new AddNullCodeVisitor(params.getMethodName(), lineNo, nullVar);
			break;
		case NEW:
			Variable newVar = (Variable) (infos[0]);
			
			visitor = new AddNewCodeVisitor(params.getMethodName(), lineNo, newVar);
			break;
		case FIELD:
			Variable fieldVar = (Variable) (infos[0]);
				
			visitor = new AddFieldCodeVisitor(params.getMethodName(), lineNo, fieldVar);
			break;
		case ITSELF:
			Variable itselfVar = (Variable) (infos[0]);
			
			visitor = new AddItselfCodeVisitor(params.getMethodName(), lineNo, itselfVar);
			break;
		case OTHERS:
			List<Variable> pairVars = (List<Variable>) (infos[0]);
			
			visitor = new AddOthersCodeVisitor(params.getMethodName(), lineNo, pairVars);
			break;
		case SWAP:
			List<Variable> swapVars = (List<Variable>) (infos[0]);
			
			visitor = new AddSwapCodeVisitor(params.getMethodName(), lineNo, swapVars);
			break;
		case BAG:
			Variable bagVar = (Variable) (infos[0]);
			int newValue = (Integer) (infos[1]);
			
			visitor = new AddBagCodeVisitor(params.getMethodName(), lineNo, bagVar, newValue);
			break;
		case CONST:
			Variable conVar = (Variable) (infos[0]);
			Integer c = (Integer) (infos[1]);
			
			visitor = new AddConstCodeVisitor(params.getMethodName(), lineNo, conVar, c);
			break;
			
		case INC:
			Variable fstVar = (Variable) (infos[0]);
			Variable sndVar = (Variable) (infos[1]);
			Integer off = (Integer) (infos[2]);
			
			visitor = new AddIncCodeVisitor(params.getMethodName(), lineNo, fstVar, sndVar, off);
			break;
		}
		
		visitor.visit(cu, null);
		
		File newFile = new File("/tmp/Tmp.java");
		PrintWriter out = new PrintWriter(newFile);
		
		System.out.println(cu.toString());
		out.write(cu.toString());
		
		out.flush();
		out.close();
		
		return newFile;
	}

}
