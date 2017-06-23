package learntest.main;

import org.eclipse.jdt.core.ICompilationUnit;

import learntest.Activator;
import learntest.plugin.preferences.LearnTestPreference;
import learntest.util.LearnTestUtil;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	
	public static String projectName;
	public static String targetClassName;
	public static String targetMethodName;
	public static boolean isL2TApproach;
	public static String targetMethodLineNum;
	
	static{
		if(Activator.getDefault() != null){
			try{
				projectName = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.TARGET_PORJECT);
				targetClassName = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.CLASS_NAME);
				targetMethodName = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.METHOD_NAME);
				String L2TString = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.IS_L2T);
				if(L2TString != null){
					isL2TApproach = Boolean.valueOf(L2TString);
				}
				targetMethodLineNum = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.METHOD_LINE_NUMBER);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
//	public static String typeName = "Triangle";
	public static String getSimpleClassName(){
		String name = targetClassName.substring(targetClassName.lastIndexOf(".")+1, targetClassName.length());
		return name;
	}
	
//	public static String pkg = "testdata.test.example." + typeName.toLowerCase() + "." + testMethodName.toLowerCase();
	public static String getTestPackageName(boolean isL2T){
		
		String approachName = isL2T ? "l2t" : "ram"; 
		
		String packName = "testdata." + approachName + ".test.init." +
				getSimpleClassName().toLowerCase() + "." + targetMethodName.toLowerCase();
		return packName;
	}
	
//	public static String testPath = pkg + "." + typeName + "1";
	public static String getTestClass(boolean isL2T){
		String testPath = getTestPackageName(isL2T) + "." + getSimpleClassName() + "1";
		return testPath;
	}
	
	
	
//	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + testMethodName.toLowerCase();
	public static String getResultedTestPackage(boolean isL2T){
		
		String approachName = isL2T ? "l2t" : "ram"; 
		
		String resultPack = "testdata." + approachName + ".result." + 
				getTestPackageName(isL2T).toLowerCase() + "." + targetMethodName.toLowerCase();
		return resultPack;
	}
	
//	public static String filePath = "F:/git_space/Ziyuan_master/Ziyuan/app/learntest/src/test/java/testdata/example/" + typeName + ".java";
	public static String getTestClassFilePath(){
		ICompilationUnit icu = LearnTestUtil.findICompilationUnitInProject(targetClassName);
		return LearnTestUtil.getOsPath(icu);
	}

	public static int getMethodLineNumber() {
		int lineNumber = 0;
		try{
			lineNumber = Integer.valueOf(targetMethodLineNum);
		}catch(Exception e){}
		
		return lineNumber;
	}
}
