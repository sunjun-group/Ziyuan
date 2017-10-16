package learntest.local;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sun.tools.classfile.StackMap_attribute.stack_map_frame;

import learntest.core.RunTimeInfo;
import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelReader;
import learntest.plugin.export.io.excel.TrialExcelWriter;

/** 
* @author ZhangHr 
*/
public class ExportCheck {

	public static void main(String[] args) throws Exception {
		String old = "E:/hairui/eclipse-java-mars-clean/eclipse/0814.xlsx";
		Map<String, Trial> oldTrials = readTrial(old);
		System.currentTimeMillis();
	}
	
	
	/**
	 * use content in in_txt to filter trials of OldTrials, 
	 * and save valid trials in out_xlsx
	 * @param out_xlsx
	 * @param in_txt
	 * @param oldTrials
	 */
	public static void output(String out_xlsx, String in_txt, Map<String, Trial> oldTrials) {
		
		File file = new File(in_txt);
        BufferedReader reader = null;
        try {
    		TrialExcelWriter writer = new TrialExcelWriter(new File(out_xlsx));
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	int index = tempString.lastIndexOf(".");
            	String method = tempString.substring(0, index);
            	String line = tempString.substring(index+1);
	 			if (oldTrials.containsKey(method+"_"+line)) {
	 				continue;
	 			}		
	 			System.out.println("export "+method+"_"+line);
            	Trial trial = new Trial();
            	trial.setMethodName(method);
            	trial.setMethodStartLine(Integer.parseInt(line));
            	initTrial(trial);
            	writer.addRowData(trial);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		
	}
	
	public static List<String> readTxt(String path) {
		File file = new File(path);
        BufferedReader reader = null;
        List<String> content = new LinkedList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	String[] info = tempString.split(",");
            	String method = info[0];
            	String line = info[1].substring(6);
	 			content.add(method+"_"+line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return content;
	}

	public static Map<String, Trial> readTrial(String file) {
		Map<String, Trial> oldTrials = new HashMap<>();
		try {
			TrialExcelReader reader = new TrialExcelReader(new File(file));
 			oldTrials = reader.readDataSheet();
 		} catch (Exception e) {
 			// ignore
 			}
		return oldTrials;
	}
	
	public static void export2Xlsx(String xlsx, Collection<Trial> trials) {
		TrialExcelWriter writer;
		try {
			writer = new TrialExcelWriter(new File(xlsx));
			for(Trial trial : trials){
	        	writer.addRowData(trial);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void initTrial(Trial trial) {
    	RunTimeInfo jdartRtInfo =  new RunTimeInfo(), l2RtInfo = new RunTimeInfo(),
    			ranRtInfo = new RunTimeInfo();
    	trial.setJdartRtInfo(jdartRtInfo);
    	trial.setL2tRtInfo(l2RtInfo);
    	trial.setMethodLength(0);
    	trial.setRanRtInfo(ranRtInfo);
	}
}
