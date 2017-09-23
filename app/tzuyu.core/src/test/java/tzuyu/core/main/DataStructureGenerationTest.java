package tzuyu.core.main;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.commons.testdata.opensource.TestPackage;

public class DataStructureGenerationTest extends AbstractTzPackageTest {

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
		params.setJunitClassNames(junitClassNames);
		params.setJunitClassNames(new ArrayList<String>());
		
		gen.genAssertion(params);
	}
	
	@Test
	public void testMoEx() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "moex");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/moex/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-clean");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllGetLast() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-last");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllClone() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-clone");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-min");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllReverse() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-reverse");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllSort() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-sort");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-travel");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-insert");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-delete");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSllAppend() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-append");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSll2Dll() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sll-2dll");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/slls2/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-clean");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllClone() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-clone");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-min");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllReverse() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-reverse");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllSort() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-sort");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-travel");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-insert");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-delete");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDllAppend() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "dll-append");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/dlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-clean");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllClone() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-clone");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-min");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-travel");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-insert");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSortllDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "sortll-delete");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/sortlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCllTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-travel");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCllMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-min");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCllClean() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-clean");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testCll2Sll() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "cll-2sll");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/clls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstInOrder() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-inorder");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstPreOrder() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-preorder");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstPostOrder() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-postorder");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-min");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstMax() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-max");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstPrec() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-prec");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstSucc() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-succ");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-insert");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-delete");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testAvlInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "avl-insert");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/avls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testAvlDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "avl-delete");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/avls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testRbtInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "rbt-insert");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/rbts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testRbtDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "rbt-delete");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/rbts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testRoseTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "rose-travel");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/rose/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMcfTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "mcf-travel");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/mcf/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTllSetRight() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "tll-setright");
		
		params.setMethodName("main1");
		params.setStartTime(startTime);
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/tlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}

}