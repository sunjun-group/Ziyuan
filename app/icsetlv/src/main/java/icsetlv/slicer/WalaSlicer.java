/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.slicer;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.Assert;
import icsetlv.common.utils.ClassUtils;
import icsetlv.iface.ISlicer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Predicate;

import com.ibm.wala.classLoader.BinaryDirectoryTreeModule;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Statement.Kind;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.BasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.strings.Atom;

/**
 * @author LLT
 * 
 */
public class WalaSlicer implements ISlicer {
	private static final String JAVA_REGRESSION_EXCLUSIONS = "/Java60RegressionExclusions.txt";
	private SlicerInput input;

	public List<BreakPoint> slice(SlicerInput input) throws IcsetlvException {
		this.input = input;
		AnalysisScope scope = makeJ2SEAnalysisScope();
		IClassHierarchy cha = makeClassHierarchy(scope);
		Iterable<Entrypoint> entrypoints = makeEntrypoints(
				scope.getApplicationLoader(), cha, input.getClassEntryPoints());
		AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
		CallGraphBuilder builder = Util.makeZeroOneCFABuilder(options, new AnalysisCache(), cha, scope);
		CallGraph cg = makeCallGraph(options, builder);
		List<Statement> stmt = findSeedStmts(cg, input.getBreakpoints());
		Collection<Statement> computeBackwardSlice = new CISlicer(cg,
				builder.getPointerAnalysis(), DataDependenceOptions.REFLECTION,
				ControlDependenceOptions.NONE).computeBackwardThinSlice(stmt);
		CollectionUtils.filter(computeBackwardSlice, new Predicate<Statement>() {

			public boolean apply(Statement val) {
				return val.getNode().getMethod().getDeclaringClass()
						.getClassLoader().getReference()
						.equals(ClassLoaderReference.Application);
			}
		});
		return toBreakpoints(computeBackwardSlice);
	}
	
	public static List<BreakPoint> toBreakpoints(Collection<Statement> slice)
			throws IcsetlvException {
		List<BreakPoint> result = new ArrayList<BreakPoint>();
		try {
			for (Statement s : slice) {
				if (s instanceof StatementWithInstructionIndex
						&& CollectionUtils.existIn(s.getKind(), Kind.NORMAL,
								Kind.NORMAL_RET_CALLEE, Kind.NORMAL_RET_CALLER)) {
					StatementWithInstructionIndex stwI = (StatementWithInstructionIndex) s;
					int instructionIndex = stwI.getInstructionIndex();
					ShrikeBTMethod method = (ShrikeBTMethod) s.getNode().getMethod();
					int bcIndex = method.getBytecodeIndex(instructionIndex);
					int src_line_number = method.getLineNumber(bcIndex);

					// create new breakpoint
					BreakPoint bkp = new BreakPoint(getClassCanonicalName(method), 
															method.getName().toString());
					bkp.setLineNo(src_line_number);
					result.add(bkp);
				}
			}
		} catch (InvalidClassFileException e) {
			throw new IcsetlvException(e);
		}
		return result;
	}

	private static String getClassCanonicalName(ShrikeBTMethod method) {
		TypeName clazz = method.getDeclaringClass().getName();
		return ClassUtils.getCanonicalName(clazz.getPackage().toString(), clazz
				.getClassName().toString());
	}

	private List<Statement> findSeedStmts(CallGraph cg, List<BreakPoint> breakpoints) {
		List<Statement> stmts = new ArrayList<Statement>();
		for (BreakPoint bkp : breakpoints) {
			CGNode node = findMethod(cg, bkp.getMethodName());
			CollectionUtils.addIfNotNullNotExist(stmts,
					findSingleSeedStmt(node, bkp.getLineNo()));
		}
		return stmts;
	}
	
	private Statement findSingleSeedStmt(CGNode n, int lineNo) {
		IR ir = n.getIR();
		SSACFG cfg = ir.getControlFlowGraph();
		ShrikeBTMethod btMethod = (ShrikeBTMethod)n.getMethod();
		SSAInstruction[] instructions = ir.getInstructions();
		for (int i = 0; i <= cfg.getMaxNumber(); i++) {
			BasicBlock bb = cfg.getNode(i);
			int start = bb.getFirstInstructionIndex();
			int end = bb.getLastInstructionIndex();
			for (int j = start; j <= end; j++) {
				if (instructions[j] != null) {
					try {
						int bcIdx;
						bcIdx = btMethod.getBytecodeIndex(j);
						int lineNumber = btMethod.getLineNumber(bcIdx);
						if (lineNumber == lineNo) {
							return new NormalStatement(n, j);
						}
					} catch (InvalidClassFileException e) {
						// TODO LLT logging
					}
				}
			}
		}
		return null;
	}
	
	public static CGNode findMethod(CallGraph cg, String name) {
		Atom a = Atom.findOrCreateUnicodeAtom(name);
		for (Iterator<? extends CGNode> it = cg.iterator(); it.hasNext();) {
			CGNode n = it.next();
			if (n.getMethod().getName().equals(a)) {
				return n;
			}
		}
		Assert.assertFail("failed to find method " + name);
		return null;
	}

	private ClassHierarchy makeClassHierarchy(AnalysisScope scope)
			throws IcsetlvException {
		try {
			return ClassHierarchy.make(scope);
		} catch (ClassHierarchyException e) {
			throw new IcsetlvException(e);
		}
	}

	private CallGraph makeCallGraph(AnalysisOptions options,
			CallGraphBuilder builder) throws IcsetlvException {
		try {
			return builder.makeCallGraph(options, null);
		} catch (IllegalArgumentException e) {
			throw new IcsetlvException(e, "Exception when make callGraph");
		} catch (CallGraphBuilderCancelException e) {
			throw new IcsetlvException(e, "Exception when make callGraph");
		}
	}

	public AnalysisScope makeJ2SEAnalysisScope() throws IcsetlvException {
		AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
		try {
			// add j2se jars
			ClassLoaderReference primordialLoader = scope.getPrimordialLoader();
			for (String lib : WalaProperties.getJarsInDirectory(input
					.getJavaHome())) {
				scope.addToScope(primordialLoader, new JarFile(lib));
			}
			// add app jars
			if (input.getAppBinFolder() != null) {
				scope.addToScope(
						scope.getApplicationLoader(),
						new BinaryDirectoryTreeModule(new File(input
								.getAppBinFolder())));
			}
			scope.setExclusions(getJavaExclusions());
		} catch (IOException e) {
			throw new IcsetlvException(e, "Slicer _ cannot create jarFile");
		}
		return scope;
	}

	private FileOfClasses getJavaExclusions() throws IOException {
		return new FileOfClasses(getClass().getResource(
				JAVA_REGRESSION_EXCLUSIONS).openStream()); 
	}

	/**
	 * ********************************************************************************/
	public static Iterable<Entrypoint> makeEntrypoints(
			final ClassLoaderReference loaderRef, final IClassHierarchy cha,
			final List<Pair<String, String>> classEntryPoints)
			throws IcsetlvException {
		Assert.assertTrue(!CollectionUtils.isEmpty(classEntryPoints),
				"no entrypoint detected!");
		for (Pair<String, String> classEntry : classEntryPoints) {
			if (classEntry.fst.indexOf("L") != 0) {
				throw new IllegalArgumentException(
						"Expected class name to start with L " + classEntry);
			}
			if (classEntry.fst.indexOf(".") > 0) {
				throw new IcsetlvException(
						"Expected class name formatted with /, not . "
								+ classEntry);
			}
		}

		return new Iterable<Entrypoint>() {
			public Iterator<Entrypoint> iterator() {
				return new Iterator<Entrypoint>() {
					private int index = 0;

					public void remove() {
						Assert.assertFail("unsupported!!");
					}

					public boolean hasNext() {
						return index < classEntryPoints.size();
					}

					public Entrypoint next() {
						TypeReference T = TypeReference.findOrCreate(loaderRef,
								TypeName.string2TypeName(classEntryPoints.get(index).fst));
						Atom method = Atom
								.findOrCreateAsciiAtom(classEntryPoints.get(index).snd);
						MethodReference mainRef = MethodReference.findOrCreate(
								T, method, Descriptor.findOrCreate(new TypeName[0],
																TypeReference.VoidName));
						index++;
						return new DefaultEntrypoint(mainRef, cha);
					}
				};
			}
		};
	}

}
