package microbat.codeanalysis.bytecode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import microbat.model.BreakPoint;
import microbat.model.variable.ArrayElementVar;
import microbat.model.variable.FieldVar;
import microbat.model.variable.LocalVar;
import microbat.util.JavaUtil;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Predicate;
import sav.common.core.utils.SignatureUtils;
import sav.strategies.dto.AppJavaClassPath;

import com.ibm.wala.classLoader.BinaryDirectoryTreeModule;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.classLoader.ShrikeCTMethod;
import com.ibm.wala.classLoader.ShrikeClass;
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
import com.ibm.wala.shrikeBT.ArrayLoadInstruction;
import com.ibm.wala.shrikeBT.ArrayStoreInstruction;
import com.ibm.wala.shrikeBT.GetInstruction;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.LoadInstruction;
import com.ibm.wala.shrikeBT.PutInstruction;
import com.ibm.wala.shrikeBT.ReturnInstruction;
import com.ibm.wala.shrikeBT.StoreInstruction;
import com.ibm.wala.shrikeCT.ConstantPoolParser;
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

/**
 * 
 * @author "linyun"
 *
 */
public class MicrobatSlicer{
	private static final String JAVA_REGRESSION_EXCLUSIONS = "/Java60RegressionExclusions.txt";
	
	public MicrobatSlicer(){}

	public List<BreakPoint> slice(AppJavaClassPath appClassPath, List<BreakPoint> breakpoints) throws Exception {
		
		AnalysisScope scope = makeJ2SEAnalysisScope(appClassPath);
		IClassHierarchy cha = ClassHierarchy.make(scope);
		
		Iterable<Entrypoint> entrypoints = makeEntrypoints(scope.getApplicationLoader(), cha, breakpoints.get(0));
		AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
		
//		CallGraphBuilder builder = Util.makeZeroOneCFABuilder(options, new AnalysisCache(), cha, scope);
//		CallGraphBuilder builder = Util.makeNCFABuilder(3, options, new AnalysisCache(), cha, scope);
		CallGraphBuilder builder = Util.makeVanillaNCFABuilder(1, options, new AnalysisCache(), cha, scope);
		
		System.out.println("builder is set.");
		
		CallGraph callGraph = builder.makeCallGraph(options, null);
		System.out.println("Call graph is built!");
		
		List<Statement> stmtList = findSeedStmts(callGraph, breakpoints);
		
		PointerAnalysis<InstanceKey> pointerAnalysis = builder.getPointerAnalysis();

//		SDG sdg = new SDG(cg, builder.getPointerAnalysis(), DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
		
//		Collection<Statement> computeBackwardSlice = new CISlicer(cg, builder.getPointerAnalysis(), DataDependenceOptions.NO_HEAP,
//				ControlDependenceOptions.NONE).computeBackwardThinSlice(stmt);
		try {
			Collection<Statement> computeBackwardSlice;
			computeBackwardSlice = Slicer.computeBackwardSlice(stmtList.get(0), callGraph, pointerAnalysis, 
					DataDependenceOptions.NO_BASE_PTRS, ControlDependenceOptions.NONE);
			System.out.println("program is sliced!");
			
			
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
			List<BreakPoint> bps = toBreakpoints(computeBackwardSlice);
			
			for(BreakPoint bp: bps){
				System.out.println(bp);
			}
			
			return bps;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	private int getStatementLineNumber(ShrikeCTMethod method, 
			StatementWithInstructionIndex stmt) throws InvalidClassFileException{
		int instructionIndex = stmt.getInstructionIndex();
		int byteCodeIndex = method.getBytecodeIndex(instructionIndex);
		int lineNumber = method.getLineNumber(byteCodeIndex);
		
		return lineNumber;
	}
	
	public List<BreakPoint> toBreakpoints(Collection<Statement> slice) throws SavException, InvalidClassFileException {
		Map<String, BreakPoint> bkpSet = new HashMap<String, BreakPoint>();
		
		for (Statement s : slice) {
			if (s instanceof StatementWithInstructionIndex
					&& CollectionUtils.existIn(s.getKind(), Kind.NORMAL,
							Kind.NORMAL_RET_CALLEE, Kind.NORMAL_RET_CALLER)) {
				StatementWithInstructionIndex stwI = (StatementWithInstructionIndex) s;
				ShrikeCTMethod method = (ShrikeCTMethod) s.getNode().getMethod();

				int stmtLinNumber = getStatementLineNumber(method, stwI);
				
				IInstruction[] allInsts = method.getInstructions();
				
				for(int index=0; index<allInsts.length; index++){
					int bcIndex = method.getBytecodeIndex(index);						
					int insLinNumber = method.getLineNumber(bcIndex);
					
					if(insLinNumber == stmtLinNumber){
						
						String className = getClassCanonicalName(method);
						String methodSig = method.getSignature();
						String key = className + "." + methodSig + "(line " + stmtLinNumber + ")";
						
						BreakPoint point = bkpSet.get(key);
						if(point == null){
							point = new BreakPoint(className, methodSig, stmtLinNumber);
							bkpSet.put(key, point);
						}
						
						appendReadWritenVariable(point, method, allInsts[index], index, s.getNode().getIR());
						
					}
				}
			}
		}

		ArrayList<BreakPoint> result = new ArrayList<>(bkpSet.values());
		
		
		return result;
	}

	@SuppressWarnings("rawtypes")
	private LocalVar generateLocalVar(ShrikeCTMethod method, int pc, int varIndex){
		Method getBCInfoMethod;
		try {
			getBCInfoMethod = ShrikeBTMethod.class.getDeclaredMethod("getBCInfo", new Class[]{});
			getBCInfoMethod.setAccessible(true);
			
			Object byteInfo = getBCInfoMethod.invoke(method, new Object[]{});
			
			Class byteCodeInfoClass = Class.forName("com.ibm.wala.classLoader.ShrikeBTMethod$BytecodeInfo");
			Field localVariableMapField = byteCodeInfoClass.getDeclaredField("localVariableMap");
			localVariableMapField.setAccessible(true);
			Object mapObject = localVariableMapField.get(byteInfo);
			int[][] map = (int[][])mapObject;
			
			int[] localPairs = map[pc];
			int nameIndex = localPairs[2*varIndex];
			int typeIndex = localPairs[2*varIndex+1];
			
			ConstantPoolParser parser = ((ShrikeClass)method.getDeclaringClass()).getReader().getCP();
			String varName = parser.getCPUtf8(nameIndex);
			String typeName = parser.getCPUtf8(typeIndex);
			typeName = SignatureUtils.signatureToName(typeName);
			
			if(varName.equals("this")){
				System.currentTimeMillis();
			}
			
			LocalVar var = new LocalVar(varName, typeName);
			return var;
			
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InvalidClassFileException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	private void appendReadWritenVariable(BreakPoint point, ShrikeCTMethod method, IInstruction ins, 
			int insIndex, IR ir) throws InvalidClassFileException {
		
//		String varName = null;
		
		int pc = method.getBytecodeIndex(insIndex);
		int lineNumber = method.getLineNumber(pc);
		CompilationUnit cu = JavaUtil.findCompilationUnitInProject(point.getClassCanonicalName());
		
		if(ins instanceof GetInstruction){
			GetInstruction gIns = (GetInstruction)ins;
			String varName = gIns.getFieldName();
			
//			if(lineNumber == 27 && varName.equals("flag")){
//				System.currentTimeMillis();
//			}
			
			WrittenFieldRetriever wfRetriever = new WrittenFieldRetriever(cu, lineNumber, varName);
			cu.accept(wfRetriever);
			String fullFieldName = wfRetriever.fullFieldName;
			
			if(fullFieldName == null){
				System.err.println("cannot find specific " + varName + " in line " + 
						lineNumber + " of " + point.getClassCanonicalName());
			}
			
			String type = gIns.getFieldType();
			type = SignatureUtils.signatureToName(type);
			FieldVar var = new FieldVar(gIns.isStatic(), fullFieldName, type);
			point.addReadVariable(var);
		}
		else if(ins instanceof PutInstruction){
			PutInstruction pIns = (PutInstruction)ins;
			String varName = pIns.getFieldName();
			
			System.currentTimeMillis();
			
			ReadFieldRetriever rfRetriever = new ReadFieldRetriever(cu, lineNumber, varName);
			cu.accept(rfRetriever);
			String fullFieldName = rfRetriever.fullFieldName;
			
			System.currentTimeMillis();
			
			if(fullFieldName == null){
				System.err.println("cannot find specific " + varName + " in line " + 
						lineNumber + " of " + point.getClassCanonicalName());
			}
			
			
			String type = pIns.getFieldType();
			type = SignatureUtils.signatureToName(type);
			FieldVar var = new FieldVar(pIns.isStatic(), fullFieldName, type);
			point.addWrittenVariable(var);
		}
		else if(ins instanceof LoadInstruction){
			LoadInstruction lIns = (LoadInstruction)ins;
			int varIndex = lIns.getVarIndex();
			
			LocalVar var = generateLocalVar(method, pc, varIndex);
			point.addReadVariable(var);
			
		}
		else if(ins instanceof StoreInstruction){
			StoreInstruction sIns = (StoreInstruction)ins;
			int varIndex = sIns.getVarIndex();
			/**
			 * I do not know why, but for wala library, the retrieved pc cannot
			 * be used to find the variable name directly. I have to try some other
			 * bcIndex to find a variable name.
			 */
			String varName = null;
			for(int j=pc; j<pc+10; j++){
				varName = method.getLocalVariableName(j, varIndex);
				if(varName != null){
					pc = j;
					break;
				}					
			}
			
			if(varName == null){
				System.err.println("Cannot achieve variable name in line " + lineNumber);
			}
			else{
//				TypeInference typeInf = TypeInference.make(ir, false);
//				TypeAbstraction type = typeInf.getType(varIndex);
//				String className = type.getType().getName().toString();
				
				LocalVar var = generateLocalVar(method, pc, varIndex);
				point.addWrittenVariable(var);
			}
			
		}
		else if(ins instanceof ArrayLoadInstruction){
			ArrayLoadInstruction alIns = (ArrayLoadInstruction)ins;
			String typeSig = alIns.getType();
			String typeName = SignatureUtils.signatureToName(typeSig);
			
			ReadArrayElementRetriever raeRetriever = new ReadArrayElementRetriever(cu, lineNumber, typeName);
			cu.accept(raeRetriever);
			String readArrayElement = raeRetriever.arrayElementName;
			
			if(readArrayElement == null){
				System.err.println("cannot find specific read array element in line " + 
						lineNumber + " of " + point.getClassCanonicalName());
			}
			else{
				ArrayElementVar var = new ArrayElementVar(readArrayElement, "unknown");
				point.addReadVariable(var);
			}
			
		}
		else if(ins instanceof ArrayStoreInstruction){
			ArrayStoreInstruction asIns = (ArrayStoreInstruction)ins;
			String typeSig = asIns.getType();
			String typeName = SignatureUtils.signatureToName(typeSig);

			WrittenArrayElementRetriever waeRetriever = new WrittenArrayElementRetriever(cu, lineNumber, typeName);
			cu.accept(waeRetriever);
			String writtenArrayElement = waeRetriever.arrayElementName;
			
			if(writtenArrayElement == null){
				System.err.println("cannot find specific written array element in line " + 
						lineNumber + " of " + point.getClassCanonicalName());
			}
			else{
				ArrayElementVar var = new ArrayElementVar(writtenArrayElement, "unknown");
				point.addWrittenVariable(var);
			}
		}
		else if(ins instanceof ReturnInstruction){
//			ReturnInstruction rIns = (ReturnInstruction)ins;
			point.setReturnStatement(true);
		}
		
	}
	
	class ASTNodeRetriever extends ASTVisitor{
		CompilationUnit cu;
		int lineNumber;
		String varName;
		
		public ASTNodeRetriever(CompilationUnit cu, int lineNumber, String varName){
			this.cu = cu;
			this.lineNumber = lineNumber;
			this.varName = varName;
		}
	}
	
	/**
	 * TODO 
	 * A rigorous implementation. I just find the first array access in a given source code line which has
	 * the specific type. A more precise implementation is left in the future.
	 * @author "linyun"
	 *
	 */
	class ReadArrayElementRetriever extends ASTNodeRetriever{
		String typeName;
		String arrayElementName;
		
		public ReadArrayElementRetriever(CompilationUnit cu, int lineNumber, String typeName){
			super(cu, lineNumber, "");
			this.typeName = typeName;
		}
		
		public boolean visit(ArrayAccess access){
			int linNum = cu.getLineNumber(access.getStartPosition());
			if(linNum == lineNumber){
				Expression arrayExp = access.getArray();
				if(arrayExp instanceof Name){
					Name name = (Name)arrayExp;
					ITypeBinding typeBinding = name.resolveTypeBinding();
					if(typeBinding.isArray()){
						String arrayType = typeBinding.getElementType().getName();
						if(arrayType.equals(typeName)){
							arrayElementName = access.toString();
							return false;
						}
					}
				}
			}
			return true;
		}
		
	}
	
	/**
	 * TODO 
	 * it is possible that two array elements are written in the same line. In this implementation, I do
	 * not handle such case. An improvement is required in the future. 
	 * @author "linyun"
	 *
	 */
	class WrittenArrayElementRetriever extends ASTNodeRetriever{
		String typeName;
		String arrayElementName;
		
		public WrittenArrayElementRetriever(CompilationUnit cu, int lineNumber, String typeName){
			super(cu, lineNumber, "");
			this.typeName = typeName;
		}
		
		public boolean visit(Assignment assignment){
			int linNum = cu.getLineNumber(assignment.getStartPosition());
			if(linNum == lineNumber){
				Expression expr = assignment.getLeftHandSide();
				
				if(expr instanceof ArrayAccess){
					ArrayAccess access = (ArrayAccess)expr;
					Expression arrayExp = access.getArray();
					if(arrayExp instanceof Name){
						Name name = (Name)arrayExp;
						ITypeBinding typeBinding = name.resolveTypeBinding();
						if(typeBinding.isArray()){
							String arrayType = typeBinding.getElementType().getName();
							if(arrayType.equals(typeName)){
								arrayElementName = access.toString();
								return false;
							}
						}
					}
				}
				
			}
			return true;
		}
		
	}

	class ReadFieldRetriever extends ASTNodeRetriever{
		String fullFieldName;
		
		public ReadFieldRetriever(CompilationUnit cu, int lineNumber, String varName){
			super(cu, lineNumber, varName);
		}
		
		public boolean visit(Assignment assignment){
			int linNum = cu.getLineNumber(assignment.getStartPosition());
			if(linNum == lineNumber){
				Expression expr = assignment.getLeftHandSide();
				
				if(expr instanceof QualifiedName){
					QualifiedName qName = (QualifiedName)expr;
					fullFieldName = qName.getFullyQualifiedName();
				}
				else if(expr instanceof SimpleName){
					SimpleName sName = (SimpleName)expr;
					fullFieldName = sName.getFullyQualifiedName();
				}
				
				return false;
			}
			return true;
		}
		
		public boolean visit(FieldDeclaration fd){
			int linNum = cu.getLineNumber(fd.getStartPosition());
			if(linNum == lineNumber){
				fullFieldName = varName;
				return false;
			}
			return true;
		}
	}
	
	class WrittenFieldRetriever extends ASTNodeRetriever{
		String fullFieldName;
		
		public WrittenFieldRetriever(CompilationUnit cu, int lineNumber, String varName){
			super(cu, lineNumber, varName);
		}
		
		public boolean visit(QualifiedName name){
			int linNum = cu.getLineNumber(name.getStartPosition());
			if(linNum == lineNumber){
				String qualifedName = name.getFullyQualifiedName();
				String namePart = name.getName().getIdentifier();
				
				if(namePart.equals(varName)){
					fullFieldName = qualifedName;
					return false;
				}
			}
			return true;
		}
		
		public boolean visit(SimpleName name){
			int linNum = cu.getLineNumber(name.getStartPosition());
			if(linNum == lineNumber){
				String namePart = name.getIdentifier();
				if(namePart.equals(varName)){
					fullFieldName = namePart;
				}
			}
			return false;
		}
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
