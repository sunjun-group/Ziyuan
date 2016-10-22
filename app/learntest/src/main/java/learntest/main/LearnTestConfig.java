package learntest.main;

import java.net.URI;

import org.eclipse.jdt.core.ICompilationUnit;

import learntest.util.LearnTestUtil;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	
	public static String projectName;
	public static String testClassName;
	public static String testMethodName;
	
//	public static String typeName = "Triangle";
	public static String getSimpleClassName(){
		String name = testClassName.substring(testClassName.lastIndexOf(".")+1, testClassName.length());
		return name;
	}
	
//	public static String pkg = "testdata.test.example." + typeName.toLowerCase() + "." + testMethodName.toLowerCase();
	public static String getTestPackageName(){
		String packName = "testdata.test.example." + getSimpleClassName().toLowerCase() + "." + testMethodName.toLowerCase();
		return packName;
	}
	
//	public static String testPath = pkg + "." + typeName + "1";
	public static String getTestClass(){
		String testPath = getTestPackageName() + "." + getSimpleClassName() + "1";
		return testPath;
	}
	
	
//	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + testMethodName.toLowerCase();
	public static String getResultedTestPackage(){
		String resultPack = "testdata.result." + getTestPackageName().toLowerCase() + "." + testMethodName.toLowerCase();
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
