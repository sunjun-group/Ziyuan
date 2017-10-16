package learntest.local;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ZhangHr
 */
public class ExcelExplorer {

	public static void main(String[] args) throws Exception {
		String project = "apache-common-math-2.2_0";
		String root = "D:/eclipse/";
		String file = root + project + ".xlsx";
		try {
			DetailExcelReader reader = new DetailExcelReader(new File(file));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			List<DetailTrial> l2tBetter = new LinkedList<>(), randBetter = new LinkedList<>();
			int trialsNum = 0, validNum = 0, learnAndAdvNum = 0, learnAndNegNum = 0, learnAndSame = 0;
			for (MethodTrial trial : methodTrials) {
				for (DetailTrial detailTrial : trial.getTrials()) {
					trialsNum++;
					if (detailTrial.getLearnedState() > 0) {
						validNum++;
						if (trial.getValidCoverageAdv() > 0) {
							learnAndAdvNum++;
						} else if (trial.getValidCoverageAdv() == 0) {
							learnAndSame++;
						} else {
							learnAndNegNum++;
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
			System.out.println("get detail trials : " + trialsNum);
			System.out.println("get valid trials : " + validNum);
			System.out.println("learn and advantage : " + learnAndAdvNum);
			System.out.println("learn and negative : " + learnAndNegNum);
			System.out.println("learn and same : " + learnAndSame);
			System.out.println("l2t better trials : " + l2tBetter.size());
			System.out.println("rand better trials : " + randBetter.size());
			try {
				PrintWriter writer = new PrintWriter(root + project + ".txt");
				writer.println("total trial : " + trialsNum);
				writer.println("get valid trials : " + validNum);
				writer.println("learn and advantage : " + learnAndAdvNum);
				writer.println("learn and negative : " + learnAndNegNum);
				writer.println("learn and same : " + learnAndSame);
				writer.println("l2t better trials : " + l2tBetter.size());
				writer.println("rand better trials : " + randBetter.size());
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
