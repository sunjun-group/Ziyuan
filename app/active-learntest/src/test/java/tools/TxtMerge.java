package tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author LLT
 *
 */
public class TxtMerge {
	
	public static void main(String[] args) throws IOException {
		TxtMerge merger = new TxtMerge();
		merger.merge("/Users/lylytran/Projects/TestProjects/trunk/apache-common-math-2.2/learntest/allValidMethods.txt", 
				"/Users/lylytran/Projects/TestProjects/trunk/apache-common-math-2.2/learntest/exclusiveMethods.txt", 
				"/Users/lylytran/Projects/TestProjects/trunk/apache-common-math-2.2/learntest/targetMethods.txt");
	}

	public void merge(String allFile, String exclusiveFile, String resultFile) throws IOException {
		List<String> exclusive = loadAllElements(exclusiveFile);
		List<String> allElement = loadAllElements(allFile);
		allElement.removeAll(exclusive);
		FileUtils.writeLines(new File(resultFile), allElement);
	}

	public List<String> loadAllElements(String validMethodFile) {
		try {
			List<?> objs = org.apache.commons.io.FileUtils.readLines(new File(validMethodFile));
			List<String> lines = new ArrayList<String>(objs.size());
			for (Object obj : objs) {
				String line = (String) obj;
				if (!line.startsWith("#")) {
					lines.add(line);
				}
			}
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
