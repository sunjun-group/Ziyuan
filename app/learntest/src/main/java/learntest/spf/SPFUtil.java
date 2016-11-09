package learntest.spf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.symbc.sequences.MethodSequences;

public class SPFUtil {
	
	public static String[] args;
	
	public static void main(String[] args) {
		runJPF(null);
	}
	
	public static List<Map<String,Integer>> runJPF(String configPath) {
		configPath = "MyClass.jpf";
		Config config = JPF.createConfig(new String[] {configPath});
		JPF jpf = new JPF(config);
		jpf.run();
		/*System.out.println("Main Results:");
		int idx = 0;
		for (Vector<String> vc : MethodSequences.methodSequences) {
			System.out.println("test" + idx ++);
			System.out.println(vc);
		}*/
		List<Vector<String>> results = MethodSequences.methodSequences;
		List<Map<String, Integer>> selectValues = new ArrayList<Map<String,Integer>>();
		for (Vector<String> vector : results) {
			String statement = vector.get(0);
			String[] values = statement.substring(statement.indexOf('(') + 1, 
					statement.indexOf(')')).split(",");
			if (values.length != args.length) {
				System.out.println("JPF Error: Length Differ");
				return null;
			}
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (String value : values) {
				System.out.println(Double.parseDouble(value));
			}
			selectValues.add(map);
		}
		return selectValues;
	}

}
