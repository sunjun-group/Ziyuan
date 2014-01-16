package tzuyu.engine.experiment;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.JClassWriter;
import tzuyu.engine.iface.DefaultClassCreator;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.utils.CollectionsExt;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.Log;

public class JUnitFileWriter {

	public String junitDriverClassName;

	public String packageName;

	public String junitDirName;

	public boolean includeParseableString = false;

	private int testsPerFile;

	private TzConfiguration config;
	private JClassWriter classCreator;
	
	private Map<String, List<List<Sequence>>> createdSequencesAndClasses = new LinkedHashMap<String, List<List<Sequence>>>();

	public JUnitFileWriter(String dirName, String packageName,
			String driverClassName, int testcasesPerFile) {
		this.junitDriverClassName = driverClassName;
		this.packageName = packageName;
		this.junitDirName = dirName;
		this.testsPerFile = testcasesPerFile;
	}

	public void config(TzConfiguration config) {
		this.config = config;
	}

	public List<File> createJUnitTestFiles(List<Sequence> sequences) {
		if (sequences.size() == 0) {
			System.out
					.println("No sequences are given, No Junit class created.");
			return new ArrayList<File>();
		}

		createOutputDir();

		List<File> ret = new ArrayList<File>();
		List<List<Sequence>> subSuites = CollectionsExt.<Sequence> chunkUp(
				sequences, testsPerFile);
		for (int i = 0; i < subSuites.size(); i++) {
			ret.add(writeSubSuite(subSuites.get(i), junitDriverClassName + i));
		}
		createdSequencesAndClasses.put(junitDriverClassName, subSuites);
		return ret;
	}

	private File writeSubSuite(List<Sequence> sequences, String junitClassName) {
		if (config.isPrettyPrint()) {
			SequencePrettyPrinter printer = new SequencePrettyPrinter(
					sequences, packageName, junitClassName, config);
			return printer.createFile(getDir().getAbsolutePath());
		}

		String className = junitClassName;
		File file = new File(getDir(), className + ".java");
		PrintStream out = createTextOutputStream(file);

		try {
			outputPackageName(out, packageName);
			out.println();
			out.println("import junit.framework.*;");
			out.println();
			out.println("public class " + className + " extends TestCase {");
			out.println();
			out.println("  public static boolean debug = false;");
			out.println();
			int testCounter = 1;
			for (Sequence s : sequences) {
				if (includeParseableString) {
					out.println("/*");
					out.println(s.toString());
					out.println("*/");
				}
				out.println(indent("public void test" + testCounter++
						+ "() throws Throwable {"));
				out.println();
				out.println(indent("if (debug) { System.out.println();"
						+ "System.out.print(\"" + className + ".test"
						+ (testCounter - 1) + "\"); }"));
				out.println();
				out.println(indent(s.toCodeString()));
				out.println("  }");
				out.println();
			}
		} finally {
			if (out != null)
				out.close();
		}

		return file;
	}

	private static PrintStream createTextOutputStream(File file) {
		try {
			return new PrintStream(file);
		} catch (IOException e) {
			Log.out.println("Exception thrown while creating file:"
					+ file.getName());
			e.printStackTrace();
			System.exit(1);
			throw new Error("This can't happen");
		}
	}

	private static void outputPackageName(PrintStream out, String packageName) {
		boolean isDefaultPackage = packageName.length() == 0;
		if (!isDefaultPackage)
			out.println("package " + packageName + ";");
	}

	private void createOutputDir() {
		File dir = getDir();
		if (!dir.exists()) {
			boolean success = dir.mkdir();
			if (!success) {
				throw new TzuYuException("Unable to create the directory: "
						+ dir.getAbsolutePath());
			}
		}
	}

	/**
	 * TODO LLT: only use output dir parameter in the configuration.
	 * (correct for command line mode).
	 */
	public File getDir() {
		if (config.getOutputDir() != null) {
			return config.getOutputDir();
		}

		File dir = null;
		if (junitDirName == null || junitDirName.length() == 0) {
			dir = new File(System.getProperty("user.dir"));
		} else {
			dir = new File(junitDirName);
		}

		if (packageName == null) {
			return dir;
		}

		packageName = packageName.trim(); // Just in case.
		if (packageName.length() == 0) {
			return dir;
		}

		String[] split = packageName.split("\\.");
		for (String s : split) {
			dir = new File(dir, s);
		}
		return dir;
	}
	
	public JClassWriter getClassCreator() {
		if (classCreator == null) {
			return new DefaultClassCreator();
		}
		return classCreator;
	}

	public static String indent(String str) {
		StringBuilder indented = new StringBuilder();
		String[] lines = str.split(Globals.lineSep);
		for (String line : lines) {
			indented.append("    " + line + Globals.lineSep);
		}
		return indented.toString();
	}
}
