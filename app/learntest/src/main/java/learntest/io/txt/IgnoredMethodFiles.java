package learntest.io.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**
 * This class is used to ignore the recorded methods.
 * have proper mutation
 * 
 * @author "linyun"
 * 
 */
public class IgnoredMethodFiles {
	private String fileName = "ignored_method.txt";
	private HashSet<String> set = new HashSet<String>();

	public IgnoredMethodFiles() {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		initializeIgnoreSet();
	}

	private void initializeIgnoreSet() {
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				set.add(line);
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}

	}
	
	public void addMethod(String method){
		if(!contains(method)){
			this.set.add(method);
			writeIgnoreSetIntoFile();
		}
	}
	
	public boolean contains(String method){
		return this.set.contains(method);
	}

	public void writeIgnoreSetIntoFile() {
		try {
			// Assume default encoding.
			FileWriter fileWriter = new FileWriter(fileName);

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			for(String method: set){
				bufferedWriter.write(method);
				bufferedWriter.newLine();
			}

			// Always close files.
			bufferedWriter.close();
		} catch (IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
	}

}
