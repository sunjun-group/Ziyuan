package main;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimulateJPFTest {
	public static void run() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException, IOException {
		String[] arrayTestConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+jpf-jdart.classpath+=../../bin",
				"+target=InputArray",
				"+concolic.method=m1",
				"+concolic.method.m1=InputArray.m1(c:char[],i:int)"};
		String[] fooConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+jpf-jdart.classpath+=E:\\workspace\\JPF\\jdart\\jdart\\build\\examples",
				"+target=features.simple.Input",
				"+concolic.method=foo",
				"+concolic.method.foo=${target}.foo(i:int)",
				"+concolic.method.foo.config=all_fields_symbolic"
		};
		
		String[] quicksortConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+jpf-jdart.classpath+=../../bin",
				"+target=Sorting",
				"+concolic.method=quicksort",
				"+concolic.method.quicksort=${target}.quicksort(a:int[])",
				"+concolic.method.quicksort.config=all_fields_symbolic"
		};

		String[] zooConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+jpf-jdart.classpath+=../../bin",
				"+target=Input",
				"+concolic.method.zoo=${target}.zoo(i:int,j:short,f:float)",
				"+concolic.method.zoo.config=zoo",
				"+jdart.configs.zoo.constraints=(f > 256.0f &&j >= 0)",
				"+jdart.configs.zoo.exploration.initial=true",
				"+jdart.configs.zoo.exploration.suspend=*.zoo_sub(*"
		};
		
		String[] bazConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+jpf-jdart.classpath+=../../bin",
				"+target=Input",
				"+concolic.method=baz",
				"+concolic.method.baz=${target}.baz(d:Input$Data)",
				"+concolic.method.baz.config=baz",
				"+jdart.configs.baz.symbolic.include=d.*"
		};
		RunJPF.run(quicksortConfig);
	}

	private static void testCoverage() {

		String targetClass = "org.apache.commons.math.stat.descriptive.DescriptiveStatistics";
		String method = "org.apache.commons.math.stat.descriptive.DescriptiveStatistics.addValue(sym)";
		String classPath = "bin";
		String[] appArgs = {
				"+classpath="+classPath,
				"+target="+targetClass,
				"+symbolic.method="+method,
				"+vm.storage.class=nil",
				"+search.multiple_errors=false",
				"+symbolic.debug=true",
				"+symbolic.min_int=-200",
				"+symbolic.max_int=200",
				"+symbolic.min_double=-2000.0",
				"+symbolic.max_double=2000.0",
				"+search.class=.search.heuristic.BFSHeuristic",
				"+listener=gov.nasa.jpf.symbc.sequences.SymbolicSequenceListener,.listener.CoverageAnalyzer",
				"+coverage.include=T1,T2",
				"+coverage.show_methods=true"
				};
		SimulateJPF.runJPF(appArgs);
		
	}

	private static void testExample(){
		String targetClass = "PushPop";//"org.apache.commons.math.stat.descriptive.DescriptiveStatistics";
		String method = "PushPop.push_pop(sym#sym#sym#sym#sym#sym)";//"org.apache.commons.math.stat.descriptive.DescriptiveStatistics.addValue(sym)";
		String classPath = "bin";
		String[] appArgs = {
				"+classpath="+classPath,
				"+target="+targetClass,
				"+symbolic.method="+method,
				"+vm.storage.class=nil",
				"+search.multiple_errors=false",
				"+symbolic.debug=true",
				"+symbolic.min_int=-200",
				"+symbolic.max_int=200",
				"+symbolic.min_double=-2000.0",
				"+symbolic.max_double=2000.0",
				"+symbolic.debug=on",
				"+search.class=.search.heuristic.BFSHeuristic",
				"+listener=gov.nasa.jpf.symbc.sequences.SymbolicSequenceListener"
				};
//		new Test().runJPF(new String[]{"+app=src/main/addValue.jpf"});
		SimulateJPF.runJPF(appArgs);
		System.out.println("======================================================================");
	}
	
	
	static void reflect(String[] conf)throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException, IOException{

//		reflect(new String[]{"+app=src/main/detail.jpf"});
		  Class clazz = Class.forName("gov.nasa.jpf.tool.RunJPF");  
	      Method method = clazz.getMethod("main", String[].class);//��Main����  
	      method.invoke(null, new Object[]{conf});
	}
}
