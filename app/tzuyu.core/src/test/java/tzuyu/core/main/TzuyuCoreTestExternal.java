package tzuyu.core.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author khanh
 *
 */
public class TzuyuCoreTestExternal extends TzuyuCoreTest {
	@Test
	@Ignore("For testing external codes")
	public void testExternalNoLoop() throws Exception {
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/data/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/data/target/classes");
		
		appData.setAppSrc("/Users/npn/dev/projects/data/src/main/java");
		appData.setAppTarget("/Users/npn/dev/projects/data/target/test-classes");

		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("simpleTestData.CalculatorTestFailed");
		junitClassNames.add("simpleTestData.CalculatorTestPassed");
		app.faultLocate(initFaultLocateParams("simpleTestData.Calculator", "getMax", "validateGetMax",
				null, junitClassNames, false));
	}
	
	@Test
	@Ignore("For testing with Guava codes")
	public void testGuava1() throws Exception {
		//  b2c6fb17ab4fbac8cd4014fe68799166f015a2c3
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
		appData.setAppSrc("/Users/npn/dev/projects/guava/guava/src");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
		appData.setAppTarget("/Users/npn/dev/projects/guava/guava/target/classes");

		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("com.google.common.cache.AbstractCacheTest");
		app.faultLocate(initFaultLocateParams("com.google.common.cache.AbstractCache", "getAllPresent", "dummyValidate",
				null, junitClassNames, false));
	}

	@Test
	@Ignore("For testing with Oryx codes")
	public void testOryx() throws Exception {
//		--> 11e2e168a179ad670e528b0f45abf60bf3a5abda
//		--> apply patch 14e8a456744bf12e829452381983f1ab9dff92ac
//		--> revert changes on StringLongMapping.java
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/src/test/java");
		appClasspaths.add("/Users/npn/dev/projects/oryx/common/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/commons-math3-3.2.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/oryx-common-0.4.0-SNAPSHOT-tests.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/config-1.2.0.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/oryx-common-0.4.0-SNAPSHOT.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/guava-11.0.2.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/hamcrest-core-1.3.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/jaxb-impl-2.2.6.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/jsr305-1.3.9.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/junit-4.11.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/pmml-manager-1.0.22.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/pmml-model-1.0.22.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/pmml-schema-1.0.22.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/slf4j-api-1.7.6.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/slf4j-jdk14-1.7.6.jar");
		appData.setAppSrc("/Users/npn/dev/projects/oryx/als-common/src/main/java");
		appData.setAppTarget("/Users/npn/dev/projects/oryx/als-common/target/classes");

		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("com.cloudera.oryx.als.common.StringLongMappingTest");
		app.faultLocate(initFaultLocateParams("com.cloudera.oryx.als.common.StringLongMapping", "toLong", "validate",
				null, junitClassNames, false));
	}
	
	
//	@Test
//	@Ignore("For testing with Guava codes")
//	public void testGuava2() throws Exception {
//		//  6b3dd12bb8960885afedb1807660d923fe3bfce8
//		final ApplicationData appData = testContext.getAppData();
//		final List<String> appClasspaths = appData.getAppClasspaths();
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//
//		final TzuyuCore app = new TzuyuCore(testContext);
//		List<String> testingClasses = Arrays.asList("com.google.common.hash.HashCode");
//		List<String> testPackages = Arrays.asList("com.google.common.hash");
//		List<String> junitClassNames = Arrays.asList("com.google.common.hash.HashCodeTest");
//		app.doSpectrumAndMachineLearning(testingClasses, null, junitClassNames, false);
//	}
//	
//	@Test
//	@Ignore("For testing with Guava codes")
//	public void testGuava3() throws Exception {
//		//  dc931f9621ab1865e67fbf026590b6371e4a19f3
//		final ApplicationData appData = testContext.getAppData();
//		final List<String> appClasspaths = appData.getAppClasspaths();
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//
//		final TzuyuCore app = new TzuyuCore(testContext);
//		List<String> testingClasses = Arrays.asList("com.google.common.collect.ImmutableTable");
//		List<String> testPackages = Arrays.asList("com.google.common.collect");
//		List<String> junitClassNames = Arrays.asList("com.google.common.collect.ImmutableTableTest");
//		app.doSpectrumAndMachineLearning(testingClasses, null, junitClassNames, false);
//	}
}
