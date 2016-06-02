package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	
	private static String pkgbase = "testdata.test.benchmark.";

	public static String typeName = "MiddleValue";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/benchmark/" + typeName + ".java";
	public static String className = "testdata.benchmark." + typeName;
	public static String methodName = "middle";
	
	public static String pkg = pkgbase + typeName.toLowerCase();
	public static String testPath = pkg + "." + typeName + "1";
	
}
