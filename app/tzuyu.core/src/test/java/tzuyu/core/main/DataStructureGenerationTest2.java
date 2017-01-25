package tzuyu.core.main;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.commons.testdata.opensource.TestPackage;

public class DataStructureGenerationTest2 extends AbstractTzPackageTest {

	protected DataStructureGeneration gen;
	protected DataStructureGenerationParams params;
	
	@Before
	public void setup() {
		super.setup();

		String jarPath = SystemVariablesUtils.updateSavJunitJarPath(appData);
		appData.addClasspath(jarPath);

		gen = new DataStructureGeneration(context);

		params = new DataStructureGenerationParams();
		params.setMachineLearningEnable(true);
		params.setRankToExamine(10);
		params.setRunMutation(false);
		params.setUseSlicer(true);
		params.setValueRetrieveLevel(3);
		params.setNumberOfTestCases(10);
		params.setVarNameCollectionMode(VarNameCollectionMode.FULL_NAME);
		
		svm.svm_set_print_string_function(new svm_print_interface() {

			@Override
			public void print(String s) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	public void genAssertion(TestPackage testPkg) throws Exception {
		prepare(testPkg);
		
		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
//		params.setJunitClassNames(junitClassNames);
		params.setJunitClassNames(new ArrayList<String>());
		
		gen.genAssertion(params);
	}
	
	@Test
	public void testSllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-clean2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllClone() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-clone2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-min2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllReverse() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-reverse2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllSort() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-sort2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-travel2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-insert2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-delete2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllAppend() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-append2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-clean2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllClone() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-clone2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-min2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllReverse() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-reverse2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllSort() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-sort2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-travel2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-insert2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-delete2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllAppend() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-append2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();

		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-clean2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllClone() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-clone2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-min2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-travel2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-insert2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-delete2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-travel2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-min2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-clean2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCll2Sll() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-2sll2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
}