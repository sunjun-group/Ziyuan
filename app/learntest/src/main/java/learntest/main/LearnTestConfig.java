package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	
	private static String pkgbase = "testdata.test.benchmark.multiply.";

	public static String typeName = "MultiplyArrayElements";
	public static String methodName = "multiply";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/benchmark/multiply/" + typeName + ".java";
	public static String className = "testdata.benchmark.multiply." + typeName;
	
	public static String pkg = pkgbase + typeName.toLowerCase();
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase();
	
}
