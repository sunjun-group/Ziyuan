package learntest.local;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ZhangHr
 */
public class ExcelExplorer {

	public static void main(String[] args) throws Exception {
		String root = "D:/eclipse/", project = "apache-common-math-2.2" ;
		String jdartP = "apache-common-math-2.2-jdart.xlsx",
				l2tP = "apache-common-math-2.2-l2t.xlsx";
		ExcelExplorer.mergeJdartAndL2t(root+project, root+jdartP, root+l2tP, false);

		String file = "apache-common-math-2.2_merge";
		ExcelExplorer.calculateBranchD(root, file);
	}
	
	/**
	 * 
	 * @param project
	 * @param jdartP
	 * @param l2tP
	 * @param append if append the file 
	 */
	public static void mergeJdartAndL2t(String project, String jdartP, String l2tP, boolean append) {

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
			String output = project+"_merge.xlsx";
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
	
	public static void calculateBranchD(String root, String file){
		String xlsx = root + file + ".xlsx";
		
		try {
			DetailExcelReader reader = new DetailExcelReader(new File(xlsx));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			List<DetailTrial> l2tBetter = new LinkedList<>(), randBetter = new LinkedList<>();
			int trialsNum = 0, validNum = 0;
			int mlearnAndAdvNum = 0, mlearnAndNegNum = 0, mlearnAndSame = 0;
			int tlearnAndAdvNum = 0, tlearnAndNegNum = 0, tlearnAndSame = 0;
			int jdartE = 0, jdartB = 0;
			for (MethodTrial trial : methodTrials) {
				double jdartCov = trial.getJdartCov();
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
					if (jdartCov > detailTrial.getL2t()) {
						jdartB++;
//						System.out.println("jdart better : " + trial.getMethodName() + "_" + trial.getLine() + " , " + jdartCov);
					}else if (jdartCov == detailTrial.getL2t()) {
						jdartE++;
//						System.out.println("jdart equal : " + trial.getMethodName() + "_" + trial.getLine() + " , " + jdartCov);						
					}
				}
			}
			try {
				String output = root + file + ".txt";
				PrintWriter writer = new PrintWriter(output);
				writer.println("methods : " + methodTrials.size());
				writer.println("total trial : " + trialsNum);
				writer.println("get valid trials : " + validNum);
				writer.println("jdart better than l2t trials : " + jdartB);
				writer.println("jdart equal to l2t trials : " + jdartE);
				writer.println("learn and average advantage methods: " + mlearnAndAdvNum);
				writer.println("learn and average negative methods: " + mlearnAndNegNum);
				writer.println("average same methods: " + mlearnAndSame);
				writer.println("learn and average advantage trials: " + tlearnAndAdvNum);
				writer.println("learn and average negative trials: " + tlearnAndNegNum);
				writer.println("learn and average same trials: " + tlearnAndSame);
				writer.println("trials with l2t better branches : " + l2tBetter.size());
				writer.println("trials with rand better branches : " + randBetter.size());
				writer.println();
				writer.println("l2tBetter : " + l2tBetter.size());
				for (DetailTrial detailTrial : l2tBetter) {
					writer.println(detailTrial.getMethodName() + "_" + detailTrial.getLine() + ":\n\t"
							+ detailTrial.getL2tBetter());
					writer.println();
				}
				writer.println();
				writer.println("randBetter : " + randBetter.size());
				for (DetailTrial detailTrial : randBetter) {
					writer.println(detailTrial.getMethodName() + "_" + detailTrial.getLine() + ":\n\t"
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
}
