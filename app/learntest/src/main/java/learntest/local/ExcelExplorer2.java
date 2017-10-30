package learntest.local;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.bcel.classfile.Field;

/**
 * @author ZhangHr
 */
public class ExcelExplorer2 {

	public static void main(String[] args) throws Exception {
		String root = "D:/eclipse/", project = "apache-common-math-2.2" ;
		String jdartP = "apache-common-math-2.2-jdart.xlsx",
				l2tP = "apache-common-math-2.2-l2t-l2tAdv.xlsx";
		String output = root + project + ".l2tAdv.merge..xlsx";
//		ExcelExplorer2.mergeJdartAndL2t(output, root+jdartP, root+l2tP, false);
		
		output = root + project + "_whole.xlsx";
		ExcelExplorer2.calculateBranchD(root, output);
	}
	
	/**
	 * 
	 * @param project
	 * @param jdartP
	 * @param l2tP
	 * @param append if append the file 
	 */
	public static void mergeJdartAndL2t(String output, String jdartP, String l2tP, boolean append) {

		DetailExcelReader reader;
		try {
			reader = new DetailExcelReader(new File(jdartP));
			List<MethodTrial> jmethodTrials = reader.readDataSheet();
			HashMap<String, MethodTrial> jMap = new HashMap<>(jmethodTrials.size());
			for (MethodTrial methodTrial : jmethodTrials) {
				String name = methodTrial.getMethodName()+"_"+methodTrial.getLine();
				if (jMap.containsKey(name)) {
					System.err.println("jdart result : " + name + " has existed!");
				}
				jMap.put(name, methodTrial);
			}
			
			reader.reset(new File(l2tP));
			List<MethodTrial> l2tmethodTrials = reader.readDataSheet();
			for (MethodTrial methodTrial : l2tmethodTrials) {
				String name = methodTrial.getMethodName()+"_"+methodTrial.getLine();
				if (!jMap.containsKey(name)) {
					System.err.println("compare : " + name + " does not exist!");
				}else {
					MethodTrial jTrial = jMap.get(name);
					methodTrial.setJdartCnt(jTrial.getJdartCnt());
					methodTrial.setJdartCov(jTrial.getJdartCov());
					methodTrial.setJdartTime(jTrial.getJdartTime());
				}
			}
			if (!append) {
				File file = new File(output);
				file.delete();;
			}
			DetailExcelWriter writer = new DetailExcelWriter(new File(output));
			for (MethodTrial methodTrial : l2tmethodTrials) {
				writer.addRowData(methodTrial);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void calculateBranchD(String root, String xlsx){		
		try {
			DetailExcelReader reader = new DetailExcelReader(new File(xlsx));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			List<DetailTrial> l2tBetter = new LinkedList<>(), randBetter = new LinkedList<>();
			
			try {
				String output = root + new File(xlsx).getName() + ".txt";
				PrintWriter writer = new PrintWriter(output);
				writer.println("methods : " + methodTrials.size());
				writer.println(branchInfo(methodTrials, l2tBetter, randBetter));
				writer.println(evosuiteInfo(methodTrials));
				writer.println(jdartInfo(methodTrials));
				writer.println();
				writer.println("l2tBetter : " + l2tBetter.size());
				for (DetailTrial detailTrial : l2tBetter) {
					writer.println(detailTrial.getMethodName() + "." + detailTrial.getLine() + ":\n\t"
							+ detailTrial.getL2tBetter());
					writer.println();
				}
				writer.println();
				writer.println("randBetter : " + randBetter.size());
				for (DetailTrial detailTrial : randBetter) {
					writer.println(detailTrial.getMethodName() + "." + detailTrial.getLine() + ":\n\t"
							+ detailTrial.getRanBetter());
					writer.println();
				}
				writer.close();
				LogExplorer.readFileByLines(output, 20);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String branchInfo(List<MethodTrial> methodTrials, List<DetailTrial> l2tBetter, List<DetailTrial> randBetter){
		int trialsNum = 0, validNum = 0;
		int mlearnAndAdvNum = 0, mlearnAndNegNum = 0, mlearnAndSame = 0;
		int tlearnAndAdvNum = 0, tlearnAndNegNum = 0, tlearnAndSame = 0;
		
		for (MethodTrial trial : methodTrials) {
			
			if (trial.getValidAveCoverageAdv() > 0) {
				mlearnAndAdvNum++;
			} else if (trial.getValidAveCoverageAdv() == 0) {
				mlearnAndSame++;
			} else {
				mlearnAndNegNum++;
			}
			
			for (DetailTrial detailTrial : trial.getTrials()) {
				trialsNum++;
				if (detailTrial.getLearnedState() > 0) {
					validNum++;
					if (detailTrial.getAdvantage() > 0) {
						tlearnAndAdvNum++;
					} else if (detailTrial.getAdvantage() == 0) {
						tlearnAndSame++;
					} else {
						tlearnAndNegNum++;
					}
				}
				if (detailTrial.getL2tBetter() != null && detailTrial.getL2tBetter().length() > 1) {
					if (detailTrial.getRandoop() != 1) {
						l2tBetter.add(detailTrial);
					}
				}
				if (detailTrial.getRanBetter() != null && detailTrial.getRanBetter().length() > 1) {
					if (detailTrial.getL2t() != 1) {
						randBetter.add(detailTrial);
					} 							
				}
			}
		}
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("total trial : " + trialsNum + "\n");
		sBuilder.append("get valid trials : " + validNum + "\n");
		sBuilder.append("learn and average advantage methods: " + mlearnAndAdvNum + "\n");
		sBuilder.append("learn and average negative methods: " + mlearnAndNegNum + "\n");
		sBuilder.append("average same methods: " + mlearnAndSame + "\n");
		sBuilder.append("learn and average advantage trials: " + tlearnAndAdvNum + "\n");
		sBuilder.append("learn and average negative trials: " + tlearnAndNegNum + "\n");
		sBuilder.append("learn and average same trials: " + tlearnAndSame + "\n");
		sBuilder.append("trials with l2t better branches : " + l2tBetter.size() + "\n");
		sBuilder.append("trials with rand better branches : " + randBetter.size() + "\n");
		return sBuilder.toString();
	}
	
	public static String evosuiteInfo(List<MethodTrial> methodTrials){

		StringBuilder sBuilder = new StringBuilder();

		int evosuiteBetter = 0, evosuiteWorse = 0, evosuiteRun = 0, evosuiteError = 0;
		int diffSig = 0;
		
		for (MethodTrial trial : methodTrials) {
			int sigCount = 0;
			double evosuiteCov = trial.getEvosuiteCov();
			String evosuiteInfo = trial.getEvosuiteInfo();
			
			if (evosuiteInfo.length() == 0) {
				evosuiteRun++;
			}else {
				evosuiteError++;
			}
			
			
			for (DetailTrial detailTrial : trial.getTrials()) {
				
				if (detailTrial.getL2t() > 0 && detailTrial.getL2t() < evosuiteCov) {
					evosuiteBetter++;
				}else if (detailTrial.getL2t() > 0 && detailTrial.getL2t() > evosuiteCov) {
					evosuiteWorse++;
				}
				if (detailTrial.getL2t() <= 0.25 && evosuiteCov == 1) {
					sigCount++;
				}
			}
			if(sigCount == trial.getTrials().size()){
				System.out.println("diffSig : " + trial.getMethodName() + "." + trial.getLine());
				diffSig++;
			}
		}

		sBuilder.append("evosuite valid methods : " + evosuiteRun + "\n");
		sBuilder.append("evosuite error methods : " + evosuiteError + "\n");
		sBuilder.append("evosuite better than l2t trials : " + evosuiteBetter + "\n");
		sBuilder.append("evosuite worse than l2t trials : " + evosuiteWorse + "\n");
		sBuilder.append("evosuite better than l2t significantly methods : " + diffSig + "\n");
		
		return sBuilder.toString();
	}
	
	public static String jdartInfo(List<MethodTrial> methodTrials){

		StringBuilder sBuilder = new StringBuilder(), methodRecorder = new StringBuilder();
		methodRecorder.append("jdart better methods : \n");
		int jdartE = 0, jdartB = 0;
		
		for (MethodTrial trial : methodTrials) {
			double jdartCov = trial.getJdartCov();		
			boolean jdartBetter = false;
			for (DetailTrial detailTrial : trial.getTrials()) {
				
				if (detailTrial.getL2t() > 0 && jdartCov > detailTrial.getL2t()) {
					jdartB++;
					System.out.println("jdart better : " + trial.getMethodName() + "." + trial.getLine() + " , " + jdartCov + "," + detailTrial.getL2t());
					sBuilder.append("jdart better : " + trial.getMethodName() + "." + trial.getLine() + " , " + jdartCov + "," + detailTrial.getL2t() + "\n");
					jdartBetter = true;
				}else if (jdartCov == detailTrial.getL2t()) {
					jdartE++;
//					System.out.println("jdart equal : " + trial.getMethodName() + "." + trial.getLine() + " , " + jdartCov);	
//					sBuilder.append("jdart equal : " + trial.getMethodName() + "." + trial.getLine() + " , " + jdartCov + "\n");						
				}				
				
			}
			if (jdartBetter) {
				methodRecorder.append(trial.getMethodName() + "." + trial.getLine()+"\n");
			}
		}
		sBuilder.append("jdart better than l2t trials : " + jdartB + "\n");
		sBuilder.append("jdart equal to l2t trials : " + jdartE + "\n");
		
		return methodRecorder.toString() + sBuilder.toString();
	}
	
}
