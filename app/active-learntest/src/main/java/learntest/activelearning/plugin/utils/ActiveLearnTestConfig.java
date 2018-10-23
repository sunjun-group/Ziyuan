package learntest.activelearning.plugin.utils;

import learntest.activelearning.plugin.ActiveLearntestPlugin;
import learntest.activelearning.plugin.preferences.ActiveLearnTestPreference;

public class ActiveLearnTestConfig {
	
	public static final String MODULE = "learntest";
	private static ActiveLearnTestConfig INSTANCE;
	private String projectName;
	private String targetClassName;
	private String targetMethodName;
	private boolean isL2TApproach;
	private String targetMethodLineNum;
	
	static{
		if(ActiveLearntestPlugin.getDefault() != null){
			try{
				INSTANCE = new ActiveLearnTestConfig();
				INSTANCE.projectName = ActiveLearntestPlugin.getDefault().getPreferenceStore().getString(ActiveLearnTestPreference.TARGET_PORJECT);
				INSTANCE.targetClassName = ActiveLearntestPlugin.getDefault().getPreferenceStore().getString(ActiveLearnTestPreference.CLASS_NAME);
				INSTANCE.targetMethodName = ActiveLearntestPlugin.getDefault().getPreferenceStore().getString(ActiveLearnTestPreference.METHOD_NAME);
				INSTANCE.targetMethodLineNum = ActiveLearntestPlugin.getDefault().getPreferenceStore().getString(ActiveLearnTestPreference.METHOD_LINE_NUMBER);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public int getMethodLineNumber() {
		int lineNumber = 0;
		try{
			lineNumber = Integer.valueOf(targetMethodLineNum);
		}catch(Exception e){}
		
		return lineNumber;
	}

	public static ActiveLearnTestConfig getInstance() {
		return INSTANCE;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public String getTargetMethodName() {
		return targetMethodName;
	}

	public void setTargetMethodName(String targetMethodName) {
		this.targetMethodName = targetMethodName;
	}

	public boolean isL2TApproach() {
		return isL2TApproach;
	}

	public void setL2TApproach(boolean isL2TApproach) {
		this.isL2TApproach = isL2TApproach;
	}

	public String getTargetMethodLineNum() {
		return targetMethodLineNum;
	}

	public void setTargetMethodLineNum(String targetMethodLineNum) {
		this.targetMethodLineNum = targetMethodLineNum;
	}

}
