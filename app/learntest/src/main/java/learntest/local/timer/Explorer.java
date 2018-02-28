package learntest.local.timer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelConstants;
import sav.common.core.utils.FileUtils;

public class Explorer {
	public static void main(String[] args) {
		List<String> paths = new LinkedList<>();
//		paths.add("E:\\172\\SUTD\\statistic\\限定时间比较\\colt_1.xlsx");
		paths.add("E:\\172\\SUTD\\statistic\\限定时间比较\\jscience_0.xlsx");
		paths.add("E:\\172\\SUTD\\statistic\\限定时间比较\\jblas_0.xlsx");
		try {
			for (String path : paths) {
				TimerTrialExcelReader reader = new TimerTrialExcelReader(new File(path));
				Map<String, Trial> map = reader.readDataSheet();
				StringBuilder sBuilder = new StringBuilder();
				for (String s : map.keySet()) {
					s = s.replace(TrialExcelConstants.METHOD_ID_SEPARATOR, ".");
					sBuilder.append(s + "\n");
				}
				FileUtils.write(path+".skip.txt", sBuilder.toString());
				System.out.println(path);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
