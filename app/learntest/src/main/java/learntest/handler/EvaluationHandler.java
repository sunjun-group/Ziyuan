package learntest.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import learntest.io.excel.ExcelReader;
import learntest.io.excel.ExcelWriter;
import learntest.io.excel.Trial;
import learntest.io.txt.IgnoredMethodFiles;
import learntest.main.LearnTestConfig;
import learntest.main.RunTimeInfo;
import learntest.util.LearnTestUtil;
import sav.settings.SAVTimer;

public class EvaluationHandler extends AbstractHandler {

	class ChildJudgeStatementChecker extends ASTVisitor{
		
		boolean containJudge = false;
		boolean containIfJudge = false;

		private Statement parentStatement;
		public ChildJudgeStatementChecker(Statement parentStat){
			this.parentStatement = parentStat;
		}
		
		public boolean visit(DoStatement stat){
			if(stat != parentStatement){
				containJudge = true;				
				return false;
			}
			else{
				return true;
			}
		}
		
		public boolean visit(EnhancedForStatement stat){
			if(stat != parentStatement){
				containJudge = true;				
				return false;
			}
			else{
				return true;
			}
		}
		
		public boolean visit(ForStatement stat){
			if(stat != parentStatement){
				containJudge = true;				
				return false;
			}
			else{
				return true;
			}
		}
		
		public boolean visit(IfStatement stat){
			if(stat != parentStatement){
				containJudge = true;	
				containIfJudge = true;
				return false;
			}
			else{
				return true;
			}
		}
		
		public boolean visit(SwitchStatement stat){
			if(stat != parentStatement){
				containJudge = true;		
				containIfJudge = true;
				return false;
			}
			else{
				return true;
			}
		}
		
		public boolean visit(WhileStatement stat){
			if(stat != parentStatement){
				containJudge = true;				
				return false;
			}
			else{
				return true;
			}
		}
	}
	
	class NestedBlockChecker extends ASTVisitor{
		boolean isNestedJudge = false;
		private void checkChildNestCondition(Statement stat, boolean isParentLoop) {
			ChildJudgeStatementChecker checker = new ChildJudgeStatementChecker(stat);
			stat.accept(checker);
			if((!isParentLoop && checker.containJudge) ||
				(isParentLoop && checker.containIfJudge)){
				isNestedJudge = true;
			}
		}
		
		public boolean visit(SwitchStatement stat){
			if(isNestedJudge){
				return false;
			}
			checkChildNestCondition(stat, false);
			
			return false;
		}
		
		public boolean visit(IfStatement stat){
			if(isNestedJudge){
				return false;
			}
			checkChildNestCondition(stat, false);
			
			return false;
		}

		
		public boolean visit(DoStatement stat){
			if(isNestedJudge){
				return false;
			}
			checkChildNestCondition(stat, true);
			return false;
		}
		
		public boolean visit(EnhancedForStatement stat){
			if(isNestedJudge){
				return false;
			}
			checkChildNestCondition(stat, true);
			return false;
		}
		
		public boolean visit(ForStatement stat){
			if(isNestedJudge){
				return false;
			}
			checkChildNestCondition(stat, true);
			return false;
		}
	}
	
	class ValidMethodCollector extends ASTVisitor{
		
		List<MethodDeclaration> mdList = new ArrayList<MethodDeclaration>();
		
		public boolean visit(MethodDeclaration md){
			
			if(!md.parameters().isEmpty()){
				
				boolean isPublic = false;
				for(Object obj: md.modifiers()){
					if(obj instanceof Modifier){
						Modifier modifier = (Modifier)obj;
						if(modifier.isPublic()){
							isPublic = true;
						}						
					}
				}
				
				if(isPublic){
					/*NestedBlockChecker checker = new NestedBlockChecker();
					md.accept(checker);
					if(checker.isNestedJudge){*/
						mdList.add(md);			
						System.currentTimeMillis();
					//}
					
				}
			}
			
			return false;
		}
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IPackageFragmentRoot root = LearnTestUtil.findMainPackageRootInProject();
		
		Job job = new Job("Do evaluation") {
			
			private HashSet<String> parsedMethods = new HashSet<String>();
			private IgnoredMethodFiles ignoredMethods = new IgnoredMethodFiles();
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				ExcelReader reader = null;
				ExcelWriter writer = null;
				try {
					reader = new ExcelReader();
					reader.readXLSX();
					parsedMethods = reader.getParsedMethodSet();
					
					writer = new ExcelWriter(LearnTestConfig.projectName);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if(reader==null || writer==null){
					return Status.CANCEL_STATUS;
				}
				
				SAVTimer.enableExecutionTimeout = true;
				SAVTimer.exeuctionTimeout = 120000;
				
				try {
					for(IJavaElement element: root.getChildren()){
						if(element instanceof IPackageFragment){
							runEvaluation((IPackageFragment)element, writer);			
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				return Status.OK_STATUS;
			}

			private void runEvaluation(IPackageFragment pack, ExcelWriter writer) throws JavaModelException {
				for(IJavaElement javaElement: pack.getChildren()){
					if(javaElement instanceof IPackageFragment){
						runEvaluation((IPackageFragment)javaElement, writer);
					}
					else if(javaElement instanceof ICompilationUnit){
						ICompilationUnit icu = (ICompilationUnit)javaElement;
						CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);
						
						AbstractTypeDeclaration type = (AbstractTypeDeclaration) cu.types().get(0);
						if(type instanceof TypeDeclaration){
							TypeDeclaration td = (TypeDeclaration)type;
							if(td.isInterface()){
								continue;
							}
							
							for(Object obj: td.modifiers()){
								if(obj instanceof Modifier){
									Modifier modifier = (Modifier)obj;
									if(modifier.isAbstract()){
										continue;
									}
								}
								
								System.currentTimeMillis();
							}
						}
						
						List<MethodDeclaration> validMethods = findValidMethod(cu); 
						if(!validMethods.isEmpty()){
							String className = LearnTestUtil.getFullNameOfCompilationUnit(cu);
							LearnTestConfig.testClassName = className;
							
							for(MethodDeclaration method: validMethods){
								String simpleMethodName = method.getName().getIdentifier();
								LearnTestConfig.testMethodName = simpleMethodName;
								
								String methodName = className + "." + simpleMethodName;
								
								if(!parsedMethods.contains(methodName) && !ignoredMethods.contains(methodName)){
									System.out.println("working method: " + LearnTestConfig.testClassName 
											+ "." + LearnTestConfig.testMethodName);
									
									try{
										SAVTimer.exeuctionTimeout = 600000;
										LearnTestConfig.isL2TApproach = true;
										RunTimeInfo l2tInfo = new GenerateTestHandler().generateTest(true);
										
										if (l2tInfo != null && l2tInfo.getTime() > 0) {
											SAVTimer.exeuctionTimeout = l2tInfo.getTime();											
										}
										LearnTestConfig.isL2TApproach = false;
										RunTimeInfo ranInfo = new GenerateTestHandler().generateTest(false);
										
										if(l2tInfo!=null && ranInfo!=null){
											String fullMN = LearnTestConfig.testClassName + "." + LearnTestConfig.testMethodName;
											Trial trial = new Trial(fullMN, l2tInfo.getTime(), l2tInfo.getCoverage(), l2tInfo.getTestCnt(),
													ranInfo.getTime(), ranInfo.getCoverage(), ranInfo.getTestCnt());
											writer.export(trial);											
										}
									}
									catch(Exception e){
										System.out.println(e);
									}
									
								}
								
								if(!ignoredMethods.contains(methodName)){
									ignoredMethods.addMethod(methodName);									
								}
							}
							
						}
					}
				}
				
			}

			private List<MethodDeclaration> findValidMethod(CompilationUnit cu) {
				ValidMethodCollector collector = new ValidMethodCollector();
				cu.accept(collector);
				
				return collector.mdList;
			}
		};
		
		job.schedule();
		
		return null;
	}

}
