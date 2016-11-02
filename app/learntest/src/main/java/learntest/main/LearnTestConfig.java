package learntest.main;

import java.net.URI;

import org.eclipse.jdt.core.ICompilationUnit;

import learntest.Activator;
import learntest.preference.LearnTestPreference;
import learntest.util.LearnTestUtil;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	
	public static String projectName;
	public static String testClassName;
	public static String testMethodName;
	public static boolean isL2TApproach;
	
	static{
		if(Activator.getDefault() != null){
			try{
				projectName = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.TARGET_PORJECT);
				testClassName = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.CLASS_NAME);
				testMethodName = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.METHOD_NAME);
				String L2TString = Activator.getDefault().getPreferenceStore().getString(LearnTestPreference.IS_L2T);
				if(L2TString != null){
					isL2TApproach = Boolean.valueOf(L2TString);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
//	public static String typeName = "Triangle";
	public static String getSimpleClassName(){
		String name = testClassName.substring(testClassName.lastIndexOf(".")+1, testClassName.length());
		return name;
	}
	
//	public static String pkg = "testdata.test.example." + typeName.toLowerCase() + "." + testMethodName.toLowerCase();
	public static String getTestPackageName(boolean isL2T){
		
		String approachName = isL2T ? "l2t" : "ram"; 
		
		String packName = "testdata." + approachName + ".test.init." +
				getSimpleClassName().toLowerCase() + "." + testMethodName.toLowerCase();
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
				getTestPackageName(isL2T).toLowerCase() + "." + testMethodName.toLowerCase();
		return resultPack;
	}
	
//	public static String filePath = "F:/git_space/Ziyuan_master/Ziyuan/app/learntest/src/test/java/testdata/example/" + typeName + ".java";
	public static String getTestClassFilePath(){
		ICompilationUnit icu = LearnTestUtil.findICompilationUnitInProject(testClassName);
		URI uri = icu.getResource().getLocationURI();
		String sourceFolderPath = uri.toString();
		return sourceFolderPath;
	}
}
