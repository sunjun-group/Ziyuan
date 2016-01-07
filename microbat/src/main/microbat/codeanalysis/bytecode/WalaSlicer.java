package microbat.codeanalysis.bytecode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

import com.ibm.wala.classLoader.BinaryDirectoryTreeModule;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
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
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.types.generics.MethodTypeSignature;
import com.ibm.wala.types.generics.TypeSignature;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.strings.Atom;

public class WalaSlicer{
	private static final String JAVA_REGRESSION_EXCLUSIONS = "/Java60RegressionExclusions.txt";
	
	public WalaSlicer(){
		
	}

	public List<BreakPoint> slice(AppJavaClassPath appClassPath, List<BreakPoint> breakpoints,
			List<String> junitClassNames) throws Exception {
		
		AnalysisScope scope = makeJ2SEAnalysisScope(appClassPath);
		IClassHierarchy cha = ClassHierarchy.make(scope);
		
		Iterable<Entrypoint> entrypoints = makeEntrypoints(scope.getApplicationLoader(), cha, breakpoints.get(0));
		AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
//		CallGraphBuilder builder = Util.makeZeroOneCFABuilder(options, new AnalysisCache(), cha, scope);
//		CallGraphBuilder builder = Util.makeNCFABuilder(3, options, new AnalysisCache(), cha, scope);
		CallGraphBuilder builder = Util.makeVanillaNCFABuilder(1, options, new AnalysisCache(), cha, scope);
		
		CallGraph cg = builder.makeCallGraph(options, null);
		List<Statement> stmt = findSeedStmts(cg, breakpoints);
		
		PointerAnalysis<InstanceKey> pa = builder.getPointerAnalysis();

//		SDG sdg = new SDG(cg, builder.getPointerAnalysis(),
//				DataDependenceOptions.NO_BASE_PTRS,
//				ControlDependenceOptions.NONE);
		
//		Collection<Statement> computeBackwardSlice = new CISlicer(cg, builder.getPointerAnalysis(), DataDependenceOptions.NO_HEAP,
//				ControlDependenceOptions.NONE).computeBackwardThinSlice(stmt);
		try {
			Collection<Statement> computeBackwardSlice;
			computeBackwardSlice = Slicer.computeBackwardSlice(stmt.get(0), cg, pa, DataDependenceOptions.NO_BASE_PTRS,
					ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
			
//			ThinSlicer ts = new ThinSlicer(cg,pa);
//			computeBackwardSlice = ts.computeBackwardThinSlice (stmt.get(0));
			
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
			e.printStackTrace();
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
						e.printStackTrace();
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


	public AnalysisScope makeJ2SEAnalysisScope(AppJavaClassPath appClassPath) throws SavException {
		AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
		try {
			/**
			 * add j2se jars
			 */
			ClassLoaderReference primordialLoader = scope.getPrimordialLoader();
			String[] libs = WalaProperties.getJarsInDirectory(appClassPath.getJavaHome());
			for (String lib : libs) {
				if(lib.contains("rt.jar")){
					scope.addToScope(primordialLoader, new JarFile(lib));					
				}
			}
			/**
			 * add jars in class path
			 */
			for(String classPath: appClassPath.getClasspaths()){
				BinaryDirectoryTreeModule module = new BinaryDirectoryTreeModule(new File(classPath));
				scope.addToScope(scope.getApplicationLoader(), module);
			}
			
			scope.setExclusions(getJavaExclusions());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scope;
	}

	private FileOfClasses getJavaExclusions() throws IOException {
		URL url = getClass().getResource(JAVA_REGRESSION_EXCLUSIONS);
		
		return new FileOfClasses(url.openStream()); 
	}

	
	private Iterable<Entrypoint> makeEntrypoints(final ClassLoaderReference loaderRef, final IClassHierarchy cha,
			final BreakPoint breakpoint){

		return new Iterable<Entrypoint>() {
			public Iterator<Entrypoint> iterator() {
				return new Iterator<Entrypoint>() {
					private int index = 0;

					public void remove() {
						Assert.fail("unsupported!!");
					}

					public boolean hasNext() {
						return index == 0;
					}

					public Entrypoint next() {
						
						String classSignature = trimSignature(SignatureUtils.getSignature(breakpoint.getClassCanonicalName()));
						TypeReference typeRef = TypeReference.findOrCreate(loaderRef, TypeName.string2TypeName(classSignature));
						
						String methodName = SignatureUtils.extractMethodName(breakpoint.getMethodSign());
						Atom method = Atom.findOrCreateAsciiAtom(methodName);
						
						Descriptor desc = createDescriptor(breakpoint.getMethodSign());
						MethodReference mainRef = MethodReference.findOrCreate(typeRef, method, desc);
						
						index++;
						
						return new DefaultEntrypoint(mainRef, cha);
					}
					
					private Descriptor createDescriptor(String methodSign) {
						MethodTypeSignature methodTypeSign = MethodTypeSignature.make(methodSign);
						TypeName[] types;
						TypeSignature[] arguments = methodTypeSign.getArguments();
						if (CollectionUtils.isEmpty(arguments)) {
							types = new TypeName[0];
						} else {
							types = new TypeName[arguments.length];
							for (int i = 0; i < arguments.length; i++) {
								types[i] = toTypeName(arguments[i].toString());
							}
						}
						TypeName returnType;
						if (methodSign.substring(methodSign.lastIndexOf(")") + 1, methodSign.length()).equals("V")) {
							returnType = TypeReference.VoidName; 
						} else {
							returnType = toTypeName(methodTypeSign.getReturnType().toString());
						}
						return Descriptor.findOrCreate(types, returnType);
					}
					
					private TypeName toTypeName(String sign) {
						return TypeName.findOrCreate(trimSignature(sign));
					}
					
					public String trimSignature(String typeSign) {
						String newSig = typeSign.replace(";", "");
						return newSig;
//						return StringUtils.replace(typeSign, ";", "");
					}
				};
			}
		};
	}

}
