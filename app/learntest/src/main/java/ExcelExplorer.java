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
		String project = "apache-collections-3.2.2_0";
		String root = "E:/hairui/eclipse-java-mars-clean/eclipse/";
		String file = root + project + ".xlsx";
		try {
			DetailExcelReader reader = new DetailExcelReader(new File(file));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			List<DetailTrial> l2tBetter = new LinkedList<>(), randBetter = new LinkedList<>();
			int trialsNum = 0;
			for (MethodTrial trial : methodTrials) {
				for (DetailTrial detailTrial : trial.getTrials()) {
					trialsNum++;
					if (detailTrial.getL2tBetter() != null && detailTrial.getL2tBetter().length() > 1) {
						l2tBetter.add(detailTrial);
					}
					if (detailTrial.getRanBetter() != null && detailTrial.getRanBetter().length() > 1) {
						randBetter.add(detailTrial);
					}
				}
			}
			System.out.println("get detail trials : "+trialsNum);
			System.out.println("l2t better trials : "+l2tBetter.size());
			System.out.println("rand better trials : "+randBetter.size());
			try{
			    PrintWriter writer = new PrintWriter(root + project + ".txt");
			    writer.println("total trial : "+trialsNum);
			    writer.println();
			    writer.println("l2tBetter : " + l2tBetter.size());
				for (DetailTrial detailTrial : l2tBetter) {
					writer.println(detailTrial.getMethodName() + "_" + detailTrial.getLine()+":\n\t"+detailTrial.getL2tBetter());
				}
			    writer.println();
				writer.println("randBetter : " + randBetter.size());
				for (DetailTrial detailTrial : randBetter) {
					writer.println(detailTrial.getMethodName() + "_" + detailTrial.getLine()+":\n\t"+detailTrial.getRanBetter());
				}
			    writer.close();
			} catch (IOException e) {
			   // do something
			}
 		} catch (Exception e) {
 			// ignore
 			}
	}
}
