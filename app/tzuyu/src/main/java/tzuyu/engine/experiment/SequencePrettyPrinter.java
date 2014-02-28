package tzuyu.engine.experiment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Files;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.ReflectionUtils;

public class SequencePrettyPrinter {

	/**
	 * The list of sequences to print
	 * */
	public final List<Sequence> outputSequences;
	/**
	 * The output package name
	 * */
	public final String packageName;
	/**
	 * The output class name
	 * */
	public final String className;
	private TzConfiguration config;

	public SequencePrettyPrinter(List<Sequence> seqs, String packageName,
			String clzName, TzConfiguration config) {
		assert seqs != null : "The sequences can not be null.";
		assert clzName != null : "The clazzName can not be null.";
		// note that the package name can be null, which means in a default
		// package
		this.outputSequences = seqs;
		this.packageName = packageName;
		this.className = clzName;
		this.config = config;
	}

	/**
	 * Prints the given list of sequences in a string
	 * */
	public String prettyPrintSequences() {
		String[] all_import_classes = this.extractImportClasses();
		StringBuilder sb = new StringBuilder();
		// print package
		if (this.packageName != null && !this.packageName.trim().equals("")) {
			sb.append("package " + packageName + ";");
			sb.append(Globals.lineSep);
			sb.append(Globals.lineSep);
		}
		// print import
		for (String import_clz : all_import_classes) {
			sb.append("import ");
			sb.append(import_clz);
			sb.append(";");
			sb.append(Globals.lineSep);
		}
		sb.append(Globals.lineSep);
		sb.append("import junit.framework.TestCase;");
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);

		// print class header
		sb.append("public class " + this.className + " extends TestCase { ");
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);
		sb.append("  public static boolean debug = false;");
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);

		int count = 0;
		for (Sequence eseq : this.outputSequences) {
			VariableRenamer renamer = new VariableRenamer(eseq);
			// print the test method
			sb.append(indent("public void test" + (count++)
					+ "() throws Throwable {", 2));
			sb.append(Globals.lineSep);
			sb.append(Globals.lineSep);
			sb.append("    if(debug) System.out.println(\"%n" + this.className
					+ ".test" + count + "\");");
			sb.append(Globals.lineSep);
			sb.append(Globals.lineSep);
			// makes 4 indentation here
			SequenceDumper printer = new SequenceDumper(eseq, renamer, config);
			String codelines = printer.printSequenceAsCodeString();
			String[] all_code_lines = codelines.split(Globals.lineSep);
			for (int lineNum = 0; lineNum < all_code_lines.length; lineNum++) {
				String codeline = all_code_lines[lineNum];
				sb.append(indent(codeline, 4));
				sb.append(Globals.lineSep);
			}

			sb.append(indent("}", 2));
			sb.append(Globals.lineSep);
			sb.append(Globals.lineSep);
		}
		String mainObj = "mainObj";
		// Output the main function which calls all the test cases.
		sb.append(indent("public static void main(String[] args) {", 2));
		sb.append(Globals.lineSep);
		sb.append(indent(className + " " + mainObj + " = new " + className
				+ "();", 4));
		sb.append(Globals.lineSep);
		sb.append(indent("try {", 4));
		sb.append(Globals.lineSep);
		for (int index = 0; index < outputSequences.size(); index++) {
			sb.append(indent(mainObj + ".test" + index + "();", 6));
			sb.append(Globals.lineSep);
		}
		sb.append(indent("} catch (Throwable e) {", 4));
		sb.append(Globals.lineSep);
		sb.append(indent("}", 4));
		sb.append(Globals.lineSep);
		sb.append(indent("}", 2));

		sb.append(Globals.lineSep);
		sb.append("}");

		return sb.toString();
	}

	public File createFile(String output_dir) {
		assert output_dir != null : "The output dir can not be null.";
		File f = new File(output_dir);
		if (f.exists()) {
			assert f.isDirectory() : "The output dir: " + output_dir
					+ " should be a dir.";
		} else {
			f.mkdirs();
		}
		File outputFile = new File(output_dir
				+ System.getProperty("file.separator") + this.className
				+ ".java");
		String content = this.prettyPrintSequences();
		try {
			Files.writeToFile(content, outputFile);
			return outputFile;
		} catch (IOException e) {
			throw new Error("Can not write in file: " + outputFile);
		}
	}

	private String[] extractImportClasses() {
		// keep all needed import
		Set<String> import_clz_set = new HashSet<String>();
		for (Sequence sequence : this.outputSequences) {
			int length = sequence.size();
			for (int i = 0; i < length; i++) {
				Statement statement = sequence.getStatement(i);
				for (Class<?> type : statement.getOrgStatement().getAllDeclaredTypes()) {
					Class<?> corrType = correctTypeIfNeeded(type);
					if (needImport(corrType)) {
						import_clz_set.add(ReflectionUtils
								.getCompilableName(corrType));
					}
				}
				// if it is a RMethod, consider the case it may be
				// static method
				if ((statement.getAction().getAction()) instanceof RMethod) {
					RMethod rmethod = (RMethod) statement.getAction()
							.getAction();
					if (rmethod.isStatic()) {
						Class<?> declaring_class = rmethod.getMethod()
								.getDeclaringClass();
						if (needImport(declaring_class)) {
							import_clz_set.add(ReflectionUtils
									.getCompilableName(declaring_class));
						}
					}
				}
			}
		}
		// return the import class array
		String[] retArray = import_clz_set.toArray(new String[0]);
		Arrays.sort(retArray);
		return retArray;
	}

	/**
	 * Needs import a class?
	 * */
	private boolean needImport(Class<?> clazz) {
		return !clazz.equals(void.class) && !clazz.isPrimitive();
	}

	private String indent(String str, int num) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			sb.append(" ");
		}
		sb.append(str);
		return sb.toString();
	}

	private Class<?> correctTypeIfNeeded(Class<?> type) {
		if (type.isArray()) {
			while (type.isArray()) {
				type = type.getComponentType();
			}
		}
		return type;
	}

}
