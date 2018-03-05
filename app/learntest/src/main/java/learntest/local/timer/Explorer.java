package learntest.local.timer;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelConstants;
import sav.common.core.utils.FileUtils;

public class Explorer {
	public static void main(String[] args) {
		List<String> paths = new LinkedList<>();
//		paths.add("E:\\172\\SUTD\\fse统计\\90s\\colt0_0_u1.xlsx");
//		paths.add("E:\\172\\SUTD\\fse统计\\90s\\apache-common-math-2.2_0_u1.xlsx");
		paths.add("E:\\172\\SUTD\\fse统计\\90s\\jscience_0.xlsx");

		try {
			for (String path : paths) {
				System.out.println(path + " start");
				TimerTrialExcelWriter reader = new TimerTrialExcelWriter(new File(path));
				reader.updateRowData();
				System.out.println("finish!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getMethodLables(){
		List<String> paths = new LinkedList<>();
		paths.add("E:\\172\\SUTD\\statistic\\限定时间比较\\colt_1.xlsx");
		paths.add("E:\\172\\SUTD\\statistic\\限定时间比较\\jscience_0.xlsx");
		paths.add("E:\\172\\SUTD\\statistic\\限定时间比较\\jblas_0.xlsx");
		try {
			for (String path : paths) {
				TimerTrialExcelReader reader = new TimerTrialExcelReader(new File(path));
				Map<String, Trial> map = reader.readDataSheet();
				List<String> list = new LinkedList<>();		
				list.addAll(map.keySet());
				list.sort(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						int l1 = o1.length();
						int l2 = o2.length();
						for (int i = 0; i < Math.min(l1, l2); i++) {
							if (o1.charAt(i) > o2.charAt(i)) {
								return 1;
							}else if (o1.charAt(i) < o2.charAt(i)) {
								return -1;
							}
						}
						if (l1 > l2) {
							return 1;
						}else if (l2 > l1) {
							return -1;
						}
						return 0;
					}
				});
				StringBuilder sBuilder = new StringBuilder();
				for (String s : list) {
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
