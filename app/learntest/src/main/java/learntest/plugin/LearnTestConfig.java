package learntest.plugin;

import learntest.plugin.preferences.LearnTestPreference;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	private static LearnTestConfig INSTANCE;
	private String projectName;
	private String targetClassName;
	private String targetMethodName;
	private boolean isL2TApproach;
	private String targetMethodLineNum;
	
	static{
		if(LearntestPlugin.getDefault() != null){
			try{
				INSTANCE = new LearnTestConfig();
				INSTANCE.projectName = LearntestPlugin.getDefault().getPreferenceStore().getString(LearnTestPreference.TARGET_PORJECT);
				INSTANCE.targetClassName = LearntestPlugin.getDefault().getPreferenceStore().getString(LearnTestPreference.CLASS_NAME);
				INSTANCE.targetMethodName = LearntestPlugin.getDefault().getPreferenceStore().getString(LearnTestPreference.METHOD_NAME);
				String L2TString = LearntestPlugin.getDefault().getPreferenceStore().getString(LearnTestPreference.IS_L2T);
				if(L2TString != null){
					INSTANCE.isL2TApproach = Boolean.valueOf(L2TString);
				}
				INSTANCE.targetMethodLineNum = LearntestPlugin.getDefault().getPreferenceStore().getString(LearnTestPreference.METHOD_LINE_NUMBER);
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

	public static LearnTestConfig getInstance() {
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
