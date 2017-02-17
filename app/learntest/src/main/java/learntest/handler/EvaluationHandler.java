package learntest.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import icsetlv.common.utils.PrimitiveUtils;
import learntest.io.excel.ExcelReader;
import learntest.io.excel.ExcelWriter;
import learntest.io.excel.Trial;
import learntest.io.txt.IgnoredMethodFiles;
import learntest.main.LearnTestConfig;
import learntest.main.RunTimeInfo;
import learntest.util.LearnTestUtil;
import sav.settings.SAVTimer;

public class EvaluationHandler extends AbstractHandler {

	class FieldAccessChecker extends ASTVisitor{
		boolean isFieldAccess = false;
		
		public boolean visit(SimpleName name){
			IBinding binding = name.resolveBinding();
			if(binding instanceof IVariableBinding){
				IVariableBinding vb = (IVariableBinding)binding;
				if(vb.isField() && !vb.getType().isPrimitive()){
					isFieldAccess = true;
				}
			}
			
			return false;
		}
	}
	
	class ChildJudgeStatementChecker extends ASTVisitor {

		boolean containJudge = false;
		boolean containIfJudge = false;

		private Statement parentStatement;

		public ChildJudgeStatementChecker(Statement parentStat) {
			this.parentStatement = parentStat;
		}

		public boolean visit(DoStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(EnhancedForStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(ForStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(IfStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				containIfJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(SwitchStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				containIfJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(WhileStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}
	}

	class NestedBlockChecker extends ASTVisitor {
		boolean isNestedJudge = false;

		private void checkChildNestCondition(Statement stat, boolean isParentLoop) {
			ChildJudgeStatementChecker checker = new ChildJudgeStatementChecker(stat);
			stat.accept(checker);
			if ((!isParentLoop && checker.containJudge) || (isParentLoop && checker.containIfJudge)) {
				isNestedJudge = true;
			}
		}

		public boolean visit(SwitchStatement stat) {
			if (isNestedJudge) {
				return false;
			}
			checkChildNestCondition(stat, false);

			return false;
		}

		public boolean visit(IfStatement stat) {
			if (isNestedJudge) {
				return false;
			}
			checkChildNestCondition(stat, false);

			return false;
		}

		public boolean visit(DoStatement stat) {
			if (isNestedJudge) {
				return false;
			}
			checkChildNestCondition(stat, true);
			return false;
		}

		public boolean visit(EnhancedForStatement stat) {
			if (isNestedJudge) {
				return false;
			}
			checkChildNestCondition(stat, true);
			return false;
		}

		public boolean visit(ForStatement stat) {
			if (isNestedJudge) {
				return false;
			}
			checkChildNestCondition(stat, true);
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
			
			
			AbstractTypeDeclaration node = (AbstractTypeDeclaration) cu.types().get(0);
//			String methodName = node.getName().toString() + "." + md.getName().getIdentifier();
			
			if(md.isConstructor()){
				return false;
			}
			
			if (!md.parameters().isEmpty()) {

				boolean isPublic = false;
				for (Object obj : md.modifiers()) {
					if (obj instanceof Modifier) {
						Modifier modifier = (Modifier) obj;
						if (modifier.isPublic()) {
							isPublic = true;
						}
					}
				}

				if (isPublic) {
					totalMethodNum++;
					
					NestedBlockChecker checker = new NestedBlockChecker();
					md.accept(checker);
					if (checker.isNestedJudge) {
						
//						if (containsAllPrimitiveType(md.parameters())) {
//							mdList.add(md);
//						}	
						
						FieldAccessChecker checker2 = new FieldAccessChecker();
						md.accept(checker2);
						
						if(!checker2.isFieldAccess){
							if (containsAllPrimitiveType(md.parameters())) {
								mdList.add(md);
							}							
						}
					}

//					DecisionStructureChecker checker = new DecisionStructureChecker();
//					md.accept(checker);
//					if (checker.isStructured()) {
//						if (containsAllPrimitiveType(md.parameters())) {
//							mdList.add(md);
//						}
//					}
				}
			}

			return false;
		}

		public boolean containsAllPrimitiveType(List parameters){
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
		
		
		@SuppressWarnings("rawtypes")
		public boolean containsArrayOrString(List parameters) {
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
	
	class RunTimeCananicalInfo{
		
		public RunTimeCananicalInfo(int validNum, int totalNum) {
			super();
			this.validNum = validNum;
			this.totalNum = totalNum;
		}
		int validNum;
		int totalNum;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IPackageFragmentRoot root = LearnTestUtil.findMainPackageRootInProject();

		Job job = new Job("Do evaluation") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				ExcelReader reader = null;
				ExcelWriter writer = null;
				try {
					reader = new ExcelReader();
					reader.readXLSX();

					writer = new ExcelWriter(LearnTestConfig.projectName);

				} catch (IOException e1) {
					e1.printStackTrace();
				}

				if (reader == null || writer == null) {
					return Status.CANCEL_STATUS;
				}

				SAVTimer.enableExecutionTimeout = true;
				SAVTimer.exeuctionTimeout = 100000;

				RunTimeCananicalInfo overalInfo = new RunTimeCananicalInfo(0, 0);
				
				try {
					for (IJavaElement element : root.getChildren()) {
						if (element instanceof IPackageFragment) {
							RunTimeCananicalInfo info = runEvaluation((IPackageFragment) element, writer);
							overalInfo.totalNum += info.totalNum;
							overalInfo.validNum += info.validNum;
						}
					}
					
					System.out.println("total valid methods: " + overalInfo.validNum );
					System.out.println("total methods: " + overalInfo.totalNum );
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

				
			private RunTimeCananicalInfo runEvaluation(IPackageFragment pack, ExcelWriter writer) throws JavaModelException {
				int validSum = 0;
				int totalSum = 0;
				
				for (IJavaElement javaElement : pack.getChildren()) {
					if (javaElement instanceof IPackageFragment) {
						runEvaluation((IPackageFragment) javaElement, writer);
					} else if (javaElement instanceof ICompilationUnit) {
						ICompilationUnit icu = (ICompilationUnit) javaElement;
						CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);

						if(cu.types().isEmpty()){
							continue;
						}
						
						AbstractTypeDeclaration type = (AbstractTypeDeclaration) cu.types().get(0);
//						String typeName = type.getName().getIdentifier();
//						if(typeName.contains("OpenIntToDoubleHashMap")){
//							System.currentTimeMillis();
//						}
//						else{
//							continue;
//						}
			
						if (type instanceof TypeDeclaration) {
							TypeDeclaration td = (TypeDeclaration) type;
							if (td.isInterface()) {
								continue;
							}
							
							boolean isAbstract = false;
							for (Object obj : td.modifiers()) {
								if (obj instanceof Modifier) {
									Modifier modifier = (Modifier) obj;
									if (modifier.isAbstract()) {
										isAbstract = true;
									}
								}
							}
							
							if(isAbstract){
								continue;
							}
						}

						MethodCollector collector = new MethodCollector(cu);
						cu.accept(collector);

						
						for(MethodDeclaration md: collector.mdList){
							String className = LearnTestUtil.getFullNameOfCompilationUnit(cu);
							String simpleMethodName = md.getName().getIdentifier();
							System.out.println(className + "." + simpleMethodName);
						}
						validSum += collector.mdList.size();
						totalSum += collector.totalMethodNum;
//						evaluateForMethodList(writer, sum, cu, validMethods);
					}
				}

				
				return new RunTimeCananicalInfo(validSum, totalSum);
			}


			private void evaluateForMethodList(ExcelWriter writer, int sum, CompilationUnit cu,
					List<MethodDeclaration> validMethods) {
				if (!validMethods.isEmpty()) {
					String className = LearnTestUtil.getFullNameOfCompilationUnit(cu);
					LearnTestConfig.testClassName = className;

					for (MethodDeclaration method : validMethods) {
						String simpleMethodName = method.getName().getIdentifier();
						LearnTestConfig.testMethodName = simpleMethodName;

						String methodName = className + "." + simpleMethodName;

						System.out.println("working method: " + LearnTestConfig.testClassName + "."
								+ LearnTestConfig.testMethodName);

						try {
							LearnTestConfig.isL2TApproach = true;
							RunTimeInfo l2tInfo = new GenerateTestHandler().generateTest(true);

							LearnTestConfig.isL2TApproach = false;
							RunTimeInfo ranInfo = new GenerateTestHandler().generateTest(false);

							if (l2tInfo != null && ranInfo != null) {
								String fullMN = LearnTestConfig.testClassName + "."
										+ LearnTestConfig.testMethodName;
								Trial trial = new Trial(fullMN, l2tInfo.getTime(), l2tInfo.getCoverage(),
										l2tInfo.getTestCnt(), ranInfo.getTime(), ranInfo.getCoverage(),
										ranInfo.getTestCnt());
								writer.export(trial);
								sum++;
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.currentTimeMillis();
						}

					}

				}
				
			}
		};

		job.schedule();

		return null;
	}

}
