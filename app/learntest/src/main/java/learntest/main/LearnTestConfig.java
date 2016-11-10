package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";

	public static String typeName = "Gcd";
	public static String methodName = "gcd";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/numeric/" + typeName + ".java";
	public static String className = "testdata.numeric." + typeName;
	
	public static String pkg = "testdata.test.numeric." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
}
