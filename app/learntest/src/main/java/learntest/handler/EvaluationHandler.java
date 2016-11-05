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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.io.excel.ExcelReader;
import learntest.io.excel.ExcelWriter;
import learntest.io.excel.Trial;
import learntest.main.LearnTestConfig;
import learntest.main.RunTimeInfo;
import learntest.util.LearnTestUtil;
import sav.settings.SAVTimer;

public class EvaluationHandler extends AbstractHandler {

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
					mdList.add(md);
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
				SAVTimer.exeuctionTimeout = 100000;
				
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
								
								if(!parsedMethods.contains(methodName)){
									System.out.println("working method: " + LearnTestConfig.testClassName 
											+ "." + LearnTestConfig.testMethodName);
									
									try{
										RunTimeInfo l2tInfo = new GenerateTestHandler().generateTest(true);
										RunTimeInfo ramInfo = new GenerateTestHandler().generateTest(false);
										
										String fullMN = LearnTestConfig.testClassName + "." + LearnTestConfig.testMethodName;
										Trial trial = new Trial(fullMN, l2tInfo.getTime(), l2tInfo.getCoverage(), 
												ramInfo.getTime(), ramInfo.getCoverage());
										writer.export(trial);
									}
									catch(Exception e){
										System.out.println(e);
									}
									
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
