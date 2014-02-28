/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.JClassWriter;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.ReflectionUtils;

/**
 * @author LLT
 * 
 */
public class NewSequencePrettyPrinter {
	private static final int MIN_METHOD_LENGTH = 6;

	/**
	 * The output package name
	 * */
	public String packageName;
	/**
	 * The output class name
	 * */
	public String className;
	private int noMethodsPerClass;
	private int noLinesPerClass;
	
	private TzConfiguration config;
	private JClassWriter classCreator;
	
	private NewSequencePrettyPrinter() { }

	public static InternalPrinter setUp(JClassWriter classCreator, TzClass tzClazz, TzConfiguration tzConfig) {
		assert tzClazz.getClassName() != null : "The clazzName can not be null.";
		// note that the package name can be null, which means in a default
		// package
		NewSequencePrettyPrinter printer = new NewSequencePrettyPrinter();
		printer.classCreator = classCreator;
		printer.packageName = tzConfig.getOutputPackageName();
		printer.className = tzClazz.getClassName();
		printer.config = tzConfig;
		printer.noMethodsPerClass = tzConfig.getMaxMethodsPerGenTestClass();
		printer.noLinesPerClass = tzConfig.getMaxLinesPerGenTestClass();
		return printer.new InternalPrinter();
	}
	
	public class InternalPrinter {

		public void print(List<Sequence> seqs) {
			assert seqs != null : "The sequences can not be null.";
			assert noMethodsPerClass >= 1 && noLinesPerClass >= 100;
			
			int curNoMethods = 0;
			int curNoLines = 0;
			int classIdx = 0;
			/* package section*/
			JStringBuilder packageSb = buildPackageSection(packageName);
			
			/* import class for import section*/
			Set<Class<?>> imports = resetImports(null);
			
			/* class section prefix */
			String newClassName = buildClassName(className, classIdx);
			JStringBuilder classPrefixSb = buildClassPrefixSection(newClassName);
			JStringBuilder classSurfixSb = new JStringBuilder().newLine()
																.append("}");
			
			curNoLines = packageSb.noLines + classPrefixSb.noLines + classSurfixSb.noLines;
			
			List<JStringBuilder> methodSbList = new ArrayList<NewSequencePrettyPrinter.JStringBuilder>(); 
			for (Sequence seq : seqs) {
				curNoMethods ++;
				
				// new imports
				Set<Class<?>> seqImports = getImportClasses(seq, imports);
				
				/* method section */
				JStringBuilder methodSb = buildTestMethodContent(seq);
				
				/* print out */
				// decide to add to current test class or create a new one
				// + 1 because we need to include import junit.framework.TestCase;
				curNoLines += (seqImports.size() + 1) + methodSb.noLines + MIN_METHOD_LENGTH;
				/*	if number of methods or number of lines exceeds the limit,
				 * 	then write the current string to file
				 * and start a new String for the other file 
				 * */
				if (curNoLines > noLinesPerClass || curNoMethods > noMethodsPerClass) {
					// write class
					classCreator.writeClass(newClassName, packageName,
							new JStringBuilder()
								.append(packageSb)
								.append(buildImportsSection(imports))
								.append(classPrefixSb)
								.append(methodSbList)
								.append(classSurfixSb).toString());
					
					// create and prepare new class
					newClassName = buildClassName(className, ++ classIdx);
					classPrefixSb = buildClassPrefixSection(newClassName);
					imports = resetImports(imports);
					methodSbList.clear();
					// rebuild method
					curNoMethods = 1;
					curNoLines = packageSb.noLines + classPrefixSb.noLines + classSurfixSb.noLines;
					
				} 
				imports.addAll(seqImports);
				methodSbList.add(buildSingleMethodSection(newClassName, methodSb, curNoMethods));
			}
			/**
			 * for the last generated class
			 */
			if (curNoLines < noLinesPerClass && curNoMethods < noMethodsPerClass
					&& curNoMethods > 0) {
				// write class
				classCreator.writeClass(newClassName, packageName,
						new JStringBuilder()
							.append(packageSb)
							.append(buildImportsSection(imports))
							.append(classPrefixSb)
							.append(methodSbList)
							.append(classSurfixSb).toString());
			}
		}
	}
	
	private JStringBuilder buildImportsSection(Set<Class<?>> imports) {
		JStringBuilder sb = new JStringBuilder()
							.append("import junit.framework.TestCase;")
							.newLine();
		// print import
		for (Class<?> importClz : imports) {
			sb.append("import ");
			sb.append(ReflectionUtils.getCompilableName(importClz));
			sb.append(";");
			sb.newLine();
		}
		sb.newLine();
		return sb;
	}

	public Set<Class<?>> resetImports(Set<Class<?>> imports) {
		if (imports == null) {
			imports = new HashSet<Class<?>>();
		} else {
			imports.clear();
		}
		return imports;
	}

	// update min method length.
	private JStringBuilder buildSingleMethodSection(String newClassName, JStringBuilder content, int methodIdx) {
		JStringBuilder sb = new JStringBuilder();
		// print the test method
		sb.tab().append("public void test" + (methodIdx)
				+ "() throws Throwable {");
		sb.newLine();
		sb.tab().tab()
			.append("if (debug) {")
			.newLine().tab().tab().tab()
			.append("System.out.println(\"%n" + newClassName + ".test" + methodIdx + "\");")
			.newLine().tab().tab()
			.append("}");
		sb.newLine();
		sb.append(content);
		sb.tab().append("}");
		sb.newLine();
		sb.newLine();
		return sb;
	}

	private JStringBuilder buildTestMethodContent(Sequence eseq) {
		VariableRenamer renamer = new VariableRenamer(eseq);
		JStringBuilder sb = new JStringBuilder();
		SequenceDumper printer = new SequenceDumper(eseq, renamer, config);
		String codelines = printer.printSequenceAsCodeString();
		String[] all_code_lines = codelines.split(Globals.lineSep);
		for (int lineNum = 0; lineNum < all_code_lines.length; lineNum++) {
			String codeline = all_code_lines[lineNum];
			sb.tab().tab().append(codeline);
			sb.newLine();
		}
		return sb;
	}
	
	private JStringBuilder buildClassPrefixSection(String className) {
		return new JStringBuilder().append("public class " + className)
					.append(" extends TestCase { ")
					.newLine()
					.tab().append("public static boolean debug = false;")
					.newLine()
					.newLine();
	}
	
	private String buildClassName(String className, int classIdx) {
		return className + classIdx; 
	}
	
	private JStringBuilder buildPackageSection(String packageName) {
		JStringBuilder sb = new JStringBuilder();
		if (packageName != null && !packageName.trim().equals("")) {
			sb.append("package " + packageName + ";")
							.newLine()
							.newLine()
							.toString();
		}
		return sb;
	}

	/**
	 * extract all new import classes from sequence that doesn't exist in curImportClaz list
	 */
	private Set<Class<?>> getImportClasses(Sequence sequence, Set<Class<?>> imports) {
		// keep all needed import
		Set<Class<?>> seqNewImportClaz = new HashSet<Class<?>>();
		
		for (int i = 0; i < sequence.size(); i++) {
			Statement statement = sequence.getStatement(i);
			// add return type, input types and more than that.
			for (Class<?> type : statement.getOrgStatement().getAllDeclaredTypes()) {
				Class<?> corrType = correctTypeIfNeeded(type);
				if (needImport(corrType)) {
					seqNewImportClaz.add(corrType);
				}
			}
			
			// if it is a RMethod, consider the case it may be
			// static method
			if ((statement.getAction().getAction()) instanceof RMethod) {
				RMethod rmethod = (RMethod) statement.getAction().getAction();
				if (rmethod.isStatic()) {
					seqNewImportClaz.add(rmethod.getMethod()
							.getDeclaringClass());
				}
			}
		}
		
		return seqNewImportClaz;
	}

	/**
	 * Needs import a class?
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
	
	public static class JStringBuilder {
		private StringBuilder sb;
		private int noLines = 1;
		
		public JStringBuilder() {
			sb = new StringBuilder();
		}

		public JStringBuilder append(List<JStringBuilder> others) {
			for (JStringBuilder otherSb : others) {
				append(otherSb);
			}
			return this;
		}

		public JStringBuilder append(JStringBuilder otherSb) {
			sb.append(otherSb.toString());
			noLines += otherSb.noLines;
			return this;
		}

		public JStringBuilder append(String str) {
			sb.append(str);
			return this;
		}
		
		public JStringBuilder newLine() {
			append(Globals.lineSep);
			noLines++;
			return this;
		}
		
		public JStringBuilder tab() {
			return append("\t");
		}
		
		public int getNoLines() {
			return noLines;
		}
		
		public String toString() {
			return sb.toString();
		}
	}

}
