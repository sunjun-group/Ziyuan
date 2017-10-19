import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.result.TestGenerationResult;

public class Evosuitor {
	public void evosuite(String targetClass, String cp) {
        EvoSuite evosuite = new EvoSuite();
        Properties.TARGET_CLASS = targetClass;
        String[] command = new String[] { "-generateSuite", "-class", targetClass, "-projectCP", cp};

        List<List<TestGenerationResult>> result = (List<List<TestGenerationResult>>)evosuite.parseCommandLine(command);
        for (List<TestGenerationResult> list : result) {
			for (TestGenerationResult testGenerationResult : list) {
				System.out.println(testGenerationResult);
			}
		}

    }
	public static void main(String[] args) {
		String targetClass = "Example", cp = "bin";
		Evosuitor evosuitor = new Evosuitor();
		evosuitor.evosuite(targetClass, cp);
	}
}
