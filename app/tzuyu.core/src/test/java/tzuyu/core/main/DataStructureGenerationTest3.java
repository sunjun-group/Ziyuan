package tzuyu.core.main;

import org.junit.Before;
import org.junit.Test;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.commons.testdata.opensource.TestPackage;

public class DataStructureGenerationTest3 extends AbstractTzPackageTest {

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
		params.setNumberOfTestCases(5);
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
		// params.setJunitClassNames(new ArrayList<String>());
		
		gen.genAssertion(params);
	}
	
	@Test
	public void testBstInOrder() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-inorder2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstPreOrder() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-preorder2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstPostOrder() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-postorder2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstMin() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-min2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstMax() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-max2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstPrec() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-prec2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstSucc() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-succ2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-insert2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBstDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "bst-delete2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/bsts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testAvlInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "avl-insert2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/avls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testAvlDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "avl-delete2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/avls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testRbtInsert() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "rbt-insert2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/rbts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testRbtDelete() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "rbt-delete2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/rbts/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMCFTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "mcf-travel2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/mcf/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testRoseTravel() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "rose-travel2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/rose/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTllSetRight() throws Exception {
		long startTime = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("data-structure", "tll-setright2");
		
		params.setMethodName("main2");
		
		gen.setTemplatesPath("/Users/HongLongPham/Workspace/testdata/data-structure/templates/tlls/");
		genAssertion(testPkg);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}

}