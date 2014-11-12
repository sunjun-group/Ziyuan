/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.wala;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Predicate;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.slicing.ISlicer;

import com.ibm.wala.classLoader.BinaryDirectoryTreeModule;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Statement.Kind;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.BasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.strings.Atom;

/**
 * @author LLT
 * 
 */
public class WalaSlicer implements ISlicer {
	private static final String JAVA_REGRESSION_EXCLUSIONS = "/Java60RegressionExclusions.txt";
	private SlicerInput input;
	private AnalysisScope scope;
	private IClassHierarchy cha;
	
	public WalaSlicer(SlicerInput input) throws SavException {
		this.input = input;
		this.scope = makeJ2SEAnalysisScope();
		this.cha = makeClassHierarchy(scope);
	}

	@Override
	public List<BreakPoint> slice(List<BreakPoint> breakpoints,
			List<String> junitClassNames) throws Exception {
		Iterable<Entrypoint> entrypoints = makeEntrypoints(
				scope.getApplicationLoader(), cha, breakpoints);
		AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
		CallGraphBuilder builder = Util.makeZeroOneCFABuilder(options,
				new AnalysisCache(), cha, scope);
		CallGraph cg = makeCallGraph(options, builder);
		List<Statement> stmt = findSeedStmts(cg, breakpoints);

		SDG sdg = new SDG(cg, builder.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_PTRS,
				ControlDependenceOptions.NONE);
		// Collection<Statement> computeBackwardSlice = new CISlicer(cg,
		// builder.getPointerAnalysis(), DataDependenceOptions.NO_HEAP,
		// ControlDependenceOptions.NONE).computeBackwardThinSlice(stmt);
		try {
			Collection<Statement> computeBackwardSlice;
			computeBackwardSlice = Slicer.computeBackwardSlice(sdg, stmt);
			CollectionUtils.filter(computeBackwardSlice,
					new Predicate<Statement>() {

						public boolean apply(Statement val) {
							return val.getNode().getMethod()
									.getDeclaringClass().getClassLoader()
									.getReference()
									.equals(ClassLoaderReference.Application);
						}
					});
			return toBreakpoints(computeBackwardSlice);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CancelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<BreakPoint> toBreakpoints(Collection<Statement> slice)
			throws SavException {
		List<BreakPoint> result = new ArrayList<BreakPoint>();
		
		Map<String, Set<Integer>> bkpMap = new HashMap<String, Set<Integer>>();
		try {
			for (Statement s : slice) {
				if (s instanceof StatementWithInstructionIndex
						&& CollectionUtils.existIn(s.getKind(), Kind.NORMAL,
								Kind.NORMAL_RET_CALLEE, Kind.NORMAL_RET_CALLER)) {
					StatementWithInstructionIndex stwI = (StatementWithInstructionIndex) s;
					int instructionIndex = stwI.getInstructionIndex();
					ShrikeBTMethod method = (ShrikeBTMethod) s.getNode().getMethod();
					if (!method.isClinit()) {

						int bcIndex = method.getBytecodeIndex(instructionIndex);
						int src_line_number = method.getLineNumber(bcIndex);

						// create new breakpoint
						Set<Integer> lineNos = CollectionUtils.getSetInitIfEmpty(bkpMap, 
								StringUtils.spaceJoin(getClassCanonicalName(method), 
										method.getSignature()));
						lineNos.add(src_line_number);
						System.out.println("line: " + src_line_number);
					}
				}
			}
			for (String key : bkpMap.keySet()) {
				String[] clzzMethod = key.split(StringUtils.SPACE);
				for (Integer lineNo : bkpMap.get(key)) {
					result.add(new BreakPoint(clzzMethod[0], clzzMethod[1], lineNo));
				}
			}
		} catch (InvalidClassFileException e) {
			throw new SavException(Constants.MODULE, e);
		}
		return result;
	}

	private String getClassCanonicalName(IMethod method) {
		TypeName clazz = method.getDeclaringClass().getName();
		return ClassUtils.getCanonicalName(clazz.getPackage().toString()
				.replace("/", "."), clazz.getClassName().toString());
	}

	private List<Statement> findSeedStmts(CallGraph cg, List<BreakPoint> breakpoints) {
		List<Statement> stmts = new ArrayList<Statement>();
		for (BreakPoint bkp : breakpoints) {
			CGNode node = findMethod(cg, bkp.getClassCanonicalName(), bkp.getMethodName());
			List<Statement> seedStmts = findSingleSeedStmt(node, bkp.getLineNo());
			for (Statement stmt : seedStmts) {
				CollectionUtils.addIfNotNullNotExist(stmts, stmt);
			}
		}
		return stmts;
	}
	
	private List<Statement> findSingleSeedStmt(CGNode n, int lineNo) {
		IR ir = n.getIR();
		SSACFG cfg = ir.getControlFlowGraph();
		ShrikeBTMethod btMethod = (ShrikeBTMethod)n.getMethod();
		SSAInstruction[] instructions = ir.getInstructions();
		List<Statement> stmts = new ArrayList<Statement>();
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
							stmts.add(new NormalStatement(n, j));
						}
					} catch (InvalidClassFileException e) {
						// TODO LLT logging
					}
				}
			}
		}
		return stmts;
	}
	
	public CGNode findMethod(CallGraph cg, String className, String methodName) {
		Atom a = Atom.findOrCreateUnicodeAtom(methodName);
		for (Iterator<? extends CGNode> it = cg.iterator(); it.hasNext();) {
			CGNode n = it.next();
			IMethod method = n.getMethod();
			if (getClassCanonicalName(method).equals(className)
					&& method.getName().equals(a)) {
				return n;
			}
		}
		Assert.fail("failed to find method " + methodName);
		return null;
	}

	private ClassHierarchy makeClassHierarchy(AnalysisScope scope)
			throws SavException {
		try {
			return ClassHierarchy.make(scope);
		} catch (ClassHierarchyException e) {
			throw new SavException(Constants.MODULE, e);
		}
	}

	private CallGraph makeCallGraph(AnalysisOptions options,
			CallGraphBuilder builder) throws SavException {
		try {
			return builder.makeCallGraph(options, null);
		} catch (IllegalArgumentException e) {
			throw new SavException(Constants.MODULE, e);
		} catch (CallGraphBuilderCancelException e) {
			throw new SavException(Constants.MODULE, e);
		}
	}

	public AnalysisScope makeJ2SEAnalysisScope() throws SavException {
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
			throw new SavException(Constants.MODULE, e, "Slicer _ cannot create jarFile");
		}
		return scope;
	}

	private FileOfClasses getJavaExclusions() throws IOException {
		return new FileOfClasses(getClass().getResource(
				JAVA_REGRESSION_EXCLUSIONS).openStream()); 
	}

	/**
	 * ********************************************************************************/
	public Iterable<Entrypoint> makeEntrypoints(
			final ClassLoaderReference loaderRef, final IClassHierarchy cha,
			final List<BreakPoint> breakpoints) throws SavException {
		EntrypointMaker<?> entrypointMaker;
		if (input.getClassEntryPoints() == null) {
			entrypointMaker = new BkpEntrypointMaker(loaderRef, cha, breakpoints);
		} else {
			entrypointMaker = new DefaultEntrypointMaker(loaderRef, cha,
					input.getClassEntryPoints());
		}
		return entrypointMaker.makeEntrypoints();
	}

	@Override
	public void setFiltering(List<String> analyzedClasses,
			List<String> analyzedPackages) {
		// TODO Auto-generated method stub
	}

}
