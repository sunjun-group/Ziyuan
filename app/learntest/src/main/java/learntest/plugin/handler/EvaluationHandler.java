package learntest.plugin.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.io.excel.MultiTrial;
import learntest.io.excel.Trial;
import learntest.io.excel.TrialExcelHandler;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import learntest.plugin.handler.filter.classfilter.ClassNameFilter;
import learntest.plugin.handler.filter.classfilter.TargetClassFilter;
import learntest.plugin.handler.filter.classfilter.TestableClassFilter;
import learntest.plugin.handler.filter.methodfilter.MethodNameFilter;
import learntest.plugin.handler.filter.methodfilter.NestedBlockChecker;
import learntest.plugin.handler.filter.methodfilter.TargetMethodFilter;
import learntest.plugin.handler.filter.methodfilter.TestableMethodFilter;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IProjectUtils;
import learntest.util.LearnTestUtil;
import sav.common.core.utils.PrimitiveUtils;
import sav.common.core.utils.SingleTimer;
import sav.settings.SAVTimer;

public class EvaluationHandler extends AbstractLearntestHandler {
	private static Logger log = LoggerFactory.getLogger(EvaluationHandler.class);
	private static final List<TargetMethodFilter> DEFAULT_METHOD_FILTERS;
	private static final List<TargetClassFilter> DEFAULT_CLASS_FILTERS;
	private static final int EVALUATIONS_PER_METHOD = 5;
	private List<TargetMethodFilter> methodFilters;
	private List<TargetClassFilter> classFilters;
	static {
		DEFAULT_METHOD_FILTERS = Arrays.asList(new TestableMethodFilter(),
				new NestedBlockChecker());
		DEFAULT_CLASS_FILTERS = Arrays.asList(new TestableClassFilter());
	}
	
	private int curMethodIdx = 0;
	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		SingleTimer timer = SingleTimer.start("Evaluation all methods");
		curMethodIdx = 0;
		final List<IPackageFragmentRoot> roots = IProjectUtils.findTargetSourcePkgRoots(LearnTestUtil.getJavaProject());
		TrialExcelHandler excelHandler = null;
		try {
			excelHandler = new TrialExcelHandler(LearnTestConfig.projectName);
			initFilters(excelHandler.readOldTrials());
		} catch (Exception e1) {
			handleException(e1);
			return Status.CANCEL_STATUS;
		}
		SAVTimer.enableExecutionTimeout = true;
		SAVTimer.exeuctionTimeout = 300000;
		RunTimeCananicalInfo overalInfo = new RunTimeCananicalInfo(0, 0, 0);
		try {
			for(IPackageFragmentRoot root: roots){
				for (IJavaElement element : root.getChildren()) {
					if (element instanceof IPackageFragment) {
						RunTimeCananicalInfo info = runEvaluation((IPackageFragment) element, excelHandler);
						overalInfo.add(info);
					}
				}
			}
			log.info(overalInfo.toString());
		} catch (JavaModelException e) {
			handleException(e);
		}
		timer.logResults(log);
		return Status.OK_STATUS;
	}

	private void initFilters(Collection<Trial> oldTrials) {
		methodFilters = new ArrayList<TargetMethodFilter>(DEFAULT_METHOD_FILTERS);
		classFilters = new ArrayList<TargetClassFilter>(DEFAULT_CLASS_FILTERS);
		classFilters.add(new ClassNameFilter(getExcludedClasses()));
//		addMethodFilter(oldTrials);
	}

	private void addMethodFilter(Collection<Trial> oldTrials) {
		if (oldTrials.isEmpty()) {
			return;
		}
		Set<String> methods = new HashSet<String>(oldTrials.size());
		for (Trial trial : oldTrials) {
			methods.add(IMethodUtils.getMethodId(trial.getMethodName(), trial.getMethodStartLine()));
		}
		methodFilters.add(new MethodNameFilter(methods));
	}

	private List<String> getExcludedClasses() {
		/* TODO - temporary hard code */
		return Arrays.asList("org.apache.tools.ant.Main");
	}

	private RunTimeCananicalInfo runEvaluation(IPackageFragment pkg, TrialExcelHandler excelHandler)
			throws JavaModelException {
		RunTimeCananicalInfo info = new RunTimeCananicalInfo();
		for (IJavaElement javaElement : pkg.getChildren()) {
			if (javaElement instanceof IPackageFragment) {
				runEvaluation((IPackageFragment) javaElement, excelHandler);
			} else if (javaElement instanceof ICompilationUnit) {
				ICompilationUnit icu = (ICompilationUnit) javaElement;
				CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);
				boolean valid = true;
				for (TargetClassFilter classFilter : classFilters) {
					if (!classFilter.isValid(cu)) {
						valid = false;
						continue;
					}
				}
				if (!valid) {
					continue;
				}
				MethodCollector collector = new MethodCollector(cu);
				cu.accept(collector);
				updateRuntimeInfo(info, cu, collector);
				
				evaluateForMethodList(excelHandler, cu, collector.mdList);
			}
		}
		return info;
	}

	private void updateRuntimeInfo(RunTimeCananicalInfo info, CompilationUnit cu, MethodCollector collector) {
		int length0 = cu.getLineNumber(cu.getStartPosition() + cu.getLength() - 1);
		info.addTotalLen(length0);
		info.validNum += collector.mdList.size();
		info.totalNum += collector.totalMethodNum;
	}

	private void evaluateForMethodList(TrialExcelHandler excelHandler, CompilationUnit cu,
			List<MethodDeclaration> validMethods) {
		if (validMethods.isEmpty()) {
			return;
		}
		
		String className = LearnTestUtil.getFullNameOfCompilationUnit(cu);
		LearnTestConfig.targetClassName = className;
		for (MethodDeclaration method : validMethods) {
			TargetMethod targetMethod = initTargetMethod(className, cu, method);

			log.info("-----------------------------------------------------------------------------------------------");
			log.info("Method {}", ++curMethodIdx);
			try{
			    PrintWriter writer = new PrintWriter("latest_working_method.txt", "UTF-8");
			    writer.println("working method: " + targetMethod.getMethodFullName());
			    writer.close();
			} catch (IOException e) {
			}
			MultiTrial multiTrial = new MultiTrial();
			for (int i = 0; i < EVALUATIONS_PER_METHOD; i++) {
				try {
					LearnTestParams params = initLearntestParams(targetMethod);
					Trial trial = evaluateLearntestForSingleMethod(params);
					if (trial != null) {
						multiTrial.addTrial(trial);
					}
				} catch (Exception e) {
					handleException(e);
				}
			}
			if (!multiTrial.isEmpty()) {
				try {
					excelHandler.export(multiTrial);
				} catch (Exception e) {
					handleException(e);
				}
			}
		}
	}

	private LearnTestParams initLearntestParams(TargetMethod targetMethod) {
		LearnTestParams params = new LearnTestParams(targetMethod);
		setSystemConfig(params);
		return params;
	}
	
	class FieldAccessChecker extends ASTVisitor {
		boolean isFieldAccess = false;
		
		public boolean visit(SimpleName name){
			IBinding binding = name.resolveBinding();
			if(binding instanceof IVariableBinding){
				IVariableBinding vb = (IVariableBinding)binding;
				if(vb.isField()){
					if(vb.getType().isPrimitive()){
						isFieldAccess = true;						
					}
					
					if(vb.getType().isArray()){
						if(vb.getType().getElementType().isPrimitive()){
							isFieldAccess = true;
						}
					}
				}
			}
			
			return false;
		}
	}
	
	
	
	class DecisionStructureChecker extends ASTVisitor {

		private boolean isStructured = false;

		public boolean visit(IfStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean visit(DoStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean visit(EnhancedForStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean visit(ForStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean isStructured() {
			return isStructured;
		}

		public void setStructured(boolean isStructured) {
			this.isStructured = isStructured;
		}
	}
	
	class MethodCollector extends ASTVisitor {
		List<MethodDeclaration> mdList = new ArrayList<MethodDeclaration>();
		CompilationUnit cu;
		int totalMethodNum = 0;
		
		public MethodCollector(CompilationUnit cu){
			this.cu = cu;
		}
		
		public boolean visit(MethodDeclaration md) {
			boolean shouldTest = true;
			for (TargetMethodFilter filter : methodFilters) {
				if (!filter.isValid(cu, md)) {
					shouldTest = false;
					break;
				}
			}
			if (shouldTest) {
				mdList.add(md);
			}
			return false;
		}
		
		@SuppressWarnings("unused")
		private boolean containsAtLeastOnePrimitiveType(List<?> parameters){
			for (Object obj : parameters) {
				if (obj instanceof SingleVariableDeclaration) {
					SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
					Type type = svd.getType();
					
					if(type.isPrimitiveType()){
						return true;
					}
					
					if(type.isArrayType()){
						ArrayType aType = (ArrayType)type;
						if(aType.getElementType().isPrimitiveType()){
							return true;
						}
					}
				}

			}

			return false;
		}

		@SuppressWarnings("unused")
		private boolean containsAllPrimitiveType(List<?> parameters){
			for (Object obj : parameters) {
				if (obj instanceof SingleVariableDeclaration) {
					SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
					Type type = svd.getType();
					String typeString = type.toString();
					
					if(!PrimitiveUtils.isPrimitive(typeString) || svd.getExtraDimensions() > 0){
						return false;
					}
				}
			}

			return true;
		}
		
		
		@SuppressWarnings({ "rawtypes", "unused" })
		private boolean containsArrayOrString(List parameters) {
			for (Object obj : parameters) {
				if (obj instanceof SingleVariableDeclaration) {
					SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
					Type type = svd.getType();
					if (type.isArrayType() || type.toString().contains("String")) {
						return true;
					}
				}

			}

			return false;
		}
	}
	
	class RunTimeCananicalInfo {
		int validNum;
		int totalNum;
		int totalLen;
		
		public RunTimeCananicalInfo() {
			
		}
		
		public RunTimeCananicalInfo(int validNum, int totalNum, int totalLen) {
			this.validNum = validNum;
			this.totalNum = totalNum;
			this.totalLen = totalLen;
		}
		public void add(RunTimeCananicalInfo info) {
			totalNum += info.totalNum;
			validNum += info.validNum;
			totalLen += info.totalLen;
		}
		
		public void addTotalLen(int totalLen) {
			this.totalLen += totalLen;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("total valid methods: ").append(validNum).append("\n")
				.append("total methods: ").append(totalNum).append("\n")
				.append("total LOC: ").append(totalLen);
			return sb.toString();
		}
	}

	@Override
	protected String getJobName() {
		return "Do evaluation";
	}

}
