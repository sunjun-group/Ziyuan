/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.TzConstants;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.iface.NullTzPrintStream;
import tzuyu.engine.junit.printer.JFileOutputPrinter;
import tzuyu.engine.junit.printer.JOutputPrinter;
import tzuyu.engine.junit.printer.JStrOutputPrinter;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzExceptionType;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.CollectionsExt;
import tzuyu.engine.utils.Files;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.ReflectionUtils;

/**
 * @author LLT
 *
 */
public class JFileWriter {

	protected String junitDriverClassName;
	protected String packageName;
	protected String junitDirName;
	protected boolean includeParseableString = false;
	protected TzConfiguration config;
	protected File dir;
	protected int testsPerFile;
	
	private Map<String, List<List<Sequence>>> createdSequencesAndClasses = new LinkedHashMap<String, List<List<Sequence>>>();
	private IPrintStream outStream = new NullTzPrintStream();

	public JFileWriter(String driverClassName) {
		this.junitDriverClassName = driverClassName;
	}

	public JFileWriter(TzConfiguration config, String className,
			boolean passTcs) {
		this(className);
		config(config, passTcs);
	}

	public void config(TzConfiguration config, boolean passTcs) {
		this.config = config;
		this.testsPerFile = config.getMaxMethodsPerGenTestClass();
		this.packageName = config.getPackageName(passTcs);
		this.junitDirName = config.getOutputPath();
	}
	
	/**
	 * start point of junit files generator.
	 */
	public List<File> createJUnitTestFiles(List<Sequence> sequences,
			int firstIdx) throws TzException {
		if (sequences.size() == 0) {
			outStream.println("No sequences are given, No Junit class created.");
			return new ArrayList<File>();
		}
		
		createOutputDir();
		
		List<File> files = new ArrayList<File>();
		List<List<Sequence>> subSuites = CollectionsExt.<Sequence> chunkUp(
				sequences, testsPerFile);
		for (int i = 0; i < subSuites.size(); i++) {
			files.add(writeSubSuite(subSuites.get(i), junitDriverClassName
					+ (i + firstIdx)));
		}
		createdSequencesAndClasses.put(junitDriverClassName, subSuites);
		return files;
	}
	
	private File createClassFile(String className) {
		File outputFile = Files.newFile(getDir().getAbsolutePath(),
				className, TzConstants.JAVA_SUFFIX);
		return outputFile;
	}
	
	public File writeClass(String newClassName, String content) throws TzException {
		File outputFile = Files.newFile(getDir().getAbsolutePath(),
				newClassName, TzConstants.JAVA_SUFFIX);
		try {
			Files.writeToFile(content, outputFile);
		} catch (IOException e) {
			throw new TzException(TzExceptionType.JUNIT_FAIL_WRITE_FILE,
					outputFile.getAbsolutePath());
		}
		return outputFile;
	}

	protected File writeSubSuite(List<Sequence> seqs, String newClassName)
			throws TzException {
		Assert.notNull(seqs, "The sequences can not be null.");

		File classFile = createClassFile(newClassName);
		JOutputPrinter out = new JFileOutputPrinter(classFile);
		
		/* package section */
		appendPackageSection(packageName, out);

		/* import section */
		List<Class<?>> imports = new ArrayList<Class<?>>();
		for (Sequence seq : seqs) {
			extractImportClasses(seq, imports);
		}
		appendImportsSection(imports, out);

		/* class declaration section */
		appendStartClass(newClassName, out);
		
		/* method section */
		int curNoMethods = 1;
		for (Sequence seq : seqs) {
			appendStartMethod(newClassName, curNoMethods++, out);
			appendMethodContent(seq, out);
			appendEndMethod(out);
		}
		
		/* end of class */
		out.newLine().append("}");
		out.close();
		return classFile;
	}

	private void appendImportsSection(List<Class<?>> imports, JOutputPrinter out) {
		out.append("import org.junit.Test;").newLine();
		// print import
		for (Class<?> importClz : imports) {
			out.append("import ");
			out.append(ReflectionUtils.getCompilableName(importClz));
			out.append(";");
			out.newLine();
		}
		out.newLine();
	}

	private void appendStartMethod(String newClassName, int methodIdx,
			JOutputPrinter out) {
		// print the test method
		out.tab().append("@Test").newLine();
		out.tab().append(
				"public void test" + (methodIdx) + "() throws Throwable {");
		out.newLine();
		out.tab()
				.tab()
				.append("if (debug) {")
				.newLine()
				.tab()
				.tab()
				.tab()
				.append("System.out.println(\"%n" + newClassName + ".test"
						+ methodIdx + "\");").newLine().tab().tab().append("}");
		out.newLine();
	}

	private void appendEndMethod(JOutputPrinter out) {
		out.tab().append("}");
		out.newLine();
		out.newLine();
	}

	private void appendMethodContent(Sequence eseq, JOutputPrinter out) {
		VariableRenamer renamer = new VariableRenamer(eseq);
		StringBuilder contentSb = new StringBuilder();
		JStrOutputPrinter contentPrinter = new JStrOutputPrinter(contentSb);
		
		SequenceJWriter printer = new SequenceJWriter(eseq, renamer, config);
		printer.printSequenceAsCodeString(contentPrinter);
		String[] all_code_lines = contentSb.toString().split(Globals.lineSep);
		for (int lineNum = 0; lineNum < all_code_lines.length; lineNum++) {
			String codeline = all_code_lines[lineNum];
			out.tab().tab().append(codeline);
			out.newLine();
		}
	}

	private void appendStartClass(String className, JOutputPrinter out) {
		out.append("public class " + className)
				.append(" { ").newLine().tab()
				.append("public static boolean debug = false;").newLine()
				.newLine();
	}

	private void appendPackageSection(String packageName, JOutputPrinter out) {
		if (packageName != null && !packageName.trim().equals("")) {
			out.append("package " + packageName + ";").newLine().newLine()
					.toString();
		}
	}

	/**
	 * extract all new import classes from sequence that doesn't exist in
	 * curImportClaz list
	 */
	private void extractImportClasses(Sequence sequence,
			List<Class<?>> imports) {
		// keep all needed import

		for (int i = 0; i < sequence.size(); i++) {
			Statement statement = sequence.getStatement(i);
			// add return type, input types and more than that.
			for (Class<?> type : statement.getOrgStatement()
					.getAllDeclaredTypes()) {
				Class<?> corrType = correctTypeIfNeeded(type);
				if (needImport(corrType)) {
					CollectionUtils.addIfNotNullNotExist(imports, corrType);
				}
			}

			// if it is a RMethod, consider the case it may be
			// static method
			if ((statement.getAction().getAction()) instanceof RMethod) {
				RMethod rmethod = (RMethod) statement.getAction().getAction();
				if (rmethod.isStatic()) {
					CollectionUtils.addIfNotNullNotExist(imports, rmethod.getMethod()
							.getDeclaringClass());
				}
			}
		}
	}

	/**
	 * to check whether the reference class of a type using in the statement is
	 * need to import or not
	 * */
	private boolean needImport(Class<?> clazz) {
		return !clazz.equals(void.class) && !clazz.isPrimitive();
	}

	private Class<?> correctTypeIfNeeded(Class<?> type) {
		if (type.isArray()) {
			while (type.isArray()) {
				type = type.getComponentType();
			}
		}
		return type;
	}

	public File getDir() {
		if (dir == null) {
			return createOutputDir();
		}
		return dir;
	}

	private File createOutputDir() {
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
		
		if (!dir.exists()) {
			boolean success = dir.mkdir();
			if (!success) {
				throw new TzRuntimeException("Unable to create the directory: "
						+ dir.getAbsolutePath());
			}
		}
		return dir;
	}

	public void setOutStream(IPrintStream outStream) {
		this.outStream = outStream;
	}
}
