package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";
	
	private static String pkgbase = "testdata.test.benchmark.bankaccount.";

	public static String typeName = "BankAccountDriverSeqSym";
	public static String methodName = "testDriver";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/benchmark/bankaccount/" + typeName + ".java";
	public static String className = "testdata.benchmark.bankaccount." + typeName;
	
	public static String pkg = pkgbase + typeName.toLowerCase();
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase();
	
}
