package learntest.handler;

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
import org.eclipse.jdt.core.dom.ArrayType;
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
import learntest.io.excel.Trial;
import learntest.io.excel.TrialExcelHandler;
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
						
						FieldAccessChecker checker2 = new FieldAccessChecker();
						md.accept(checker2);
						
						//if(checker2.isFieldAccess){
							//if(containsAtLeastOnePrimitiveType(md.parameters())){
								mdList.add(md);								
							//}
						//}
					}

				}
			}

			return false;
		}
		
		public boolean containsAtLeastOnePrimitiveType(List parameters){
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
		
		public RunTimeCananicalInfo(int validNum, int totalNum, int totalLen) {
			super();
			this.validNum = validNum;
			this.totalNum = totalNum;
			this.totalLen = totalLen;
		}
		int validNum;
		int totalNum;
		int totalLen;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final List<IPackageFragmentRoot> roots = LearnTestUtil.findMainPackageRootInProjects();

		Job job = new Job("Do evaluation") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				TrialExcelHandler excelHandler = null;
				try {
					excelHandler = new TrialExcelHandler(LearnTestConfig.projectName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				if (excelHandler == null) {
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
								overalInfo.totalNum += info.totalNum;
								overalInfo.validNum += info.validNum;
								overalInfo.totalLen += info.totalLen;
							}
						}
					}
					
					System.out.println("total valid methods: " + overalInfo.validNum );
					System.out.println("total methods: " + overalInfo.totalNum );
					System.out.println("total LOC: " + overalInfo.totalLen );
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

				
			private RunTimeCananicalInfo runEvaluation(IPackageFragment pack, TrialExcelHandler excelHandler) throws JavaModelException {
				int validSum = 0;
				int totalSum = 0;
				
				int totalLength = 0;
				
//				ExcelWriter2 writer2 = new ExcelWriter2();
				
				for (IJavaElement javaElement : pack.getChildren()) {
					if (javaElement instanceof IPackageFragment) {
						runEvaluation((IPackageFragment) javaElement, excelHandler);
					} else if (javaElement instanceof ICompilationUnit) {
						ICompilationUnit icu = (ICompilationUnit) javaElement;
						CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);

						int length0 = cu.getLineNumber(cu.getStartPosition()+cu.getLength()-1);
						totalLength += length0;
						
						if(cu.types().isEmpty()){
							continue;
						}
						
						AbstractTypeDeclaration type = (AbstractTypeDeclaration) cu.types().get(0);
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

						
//						for(MethodDeclaration md: collector.mdList){
//							String className = LearnTestUtil.getFullNameOfCompilationUnit(cu);
//							String simpleMethodName = md.getName().getIdentifier();
//							String fullName = className + "." + simpleMethodName;
//							
//							int end = cu.getLineNumber(md.getStartPosition()+md.getLength()); 
//							int start = cu.getLineNumber(md.getName().getStartPosition());
//							int length = end-start+1;
//							
//							ExcelReader reader = new ExcelReader();
//							try {
//								reader.readXLSX();
//							} catch (IOException e1) {
//								e1.printStackTrace();
//							}
//							HashMap<String, List<Integer>> readMethodSet = reader.getParsedMethodSet();
//							
//							List<Integer> rowList = readMethodSet.get(fullName);
//							if(rowList!=null && !rowList.isEmpty()){
//								int row = rowList.get(0);
//								writer2.export(row, length, fullName);
//							}
//						}
						
						validSum += collector.mdList.size();
						totalSum += collector.totalMethodNum;
						evaluateForMethodList(excelHandler, cu, collector.mdList);
					}
				}

				
				return new RunTimeCananicalInfo(validSum, totalSum, totalLength);
			}


			private void evaluateForMethodList(TrialExcelHandler excelHandler, CompilationUnit cu,
					List<MethodDeclaration> validMethods) {
				
				if (!validMethods.isEmpty()) {
					String className = LearnTestUtil.getFullNameOfCompilationUnit(cu);
					LearnTestConfig.targetClassName = className;

					for (MethodDeclaration method : validMethods) {
						String simpleMethodName = method.getName().getIdentifier();
						LearnTestConfig.targetMethodName = simpleMethodName;
						
						int lineNumber = cu.getLineNumber(method.getName().getStartPosition());
						LearnTestConfig.targetMethodLineNum = String.valueOf(lineNumber);

//						String methodName = className + "." + simpleMethodName;

						System.out.println("working method: " + LearnTestConfig.targetClassName + "."
								+ LearnTestConfig.targetMethodName);

						try {
							int times = 1;
							RunTimeInfo l2tAverageInfo = new RunTimeInfo(0, 0, 0);
							RunTimeInfo ranAverageInfo = new RunTimeInfo(0, 0, 0);
							
							for(int i=0; i<times; i++){
								LearnTestConfig.isL2TApproach = true;
								RunTimeInfo l2tInfo = new GenerateTestHandler().generateTest(true);
								
								if(l2tAverageInfo !=null && l2tInfo != null){
									l2tAverageInfo.coverage += l2tInfo.coverage;
									l2tAverageInfo.time += l2tInfo.time;
									l2tAverageInfo.testCnt += l2tInfo.testCnt;									
								}
								else{
									l2tAverageInfo = null;
								}
								
								Thread.sleep(5000);
								
								LearnTestConfig.isL2TApproach = false;
								RunTimeInfo ranInfo = new GenerateTestHandler().generateTest(false);
								
								if(ranAverageInfo!=null && ranInfo!=null){
									ranAverageInfo.coverage += ranInfo.coverage;
									ranAverageInfo.time += ranInfo.time;
									ranAverageInfo.testCnt += ranInfo.testCnt;									
								}
								else{
									ranAverageInfo = null;
								}
								
								Thread.sleep(5000);
							}
							
							if (l2tAverageInfo != null && ranAverageInfo != null) {
								l2tAverageInfo.coverage /= times;
								l2tAverageInfo.time /= times;
								l2tAverageInfo.testCnt /= times;
								
								ranAverageInfo.coverage /= times;
								ranAverageInfo.time /= times;
								ranAverageInfo.testCnt /= times;
								
								String fullMN = LearnTestConfig.targetClassName + "."
										+ LearnTestConfig.targetMethodName;
								
								int start = cu.getLineNumber(method.getStartPosition());
								int end = cu.getLineNumber(method.getStartPosition()+method.getLength());
								int length = end-start+1;
								
								
								Trial trial = new Trial(fullMN, l2tAverageInfo.getTime(), l2tAverageInfo.getCoverage(),
										l2tAverageInfo.getTestCnt(), ranAverageInfo.getTime(), ranAverageInfo.getCoverage(),
										ranAverageInfo.getTestCnt(), length, start);
								excelHandler.export(trial);
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.currentTimeMillis();
						} catch (java.lang.NoClassDefFoundError error){
							error.printStackTrace();
						}

					}

				}
				
			}
		};

		job.schedule();

		return null;
	}

}
