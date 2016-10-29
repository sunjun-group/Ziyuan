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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.main.LearnTestConfig;
import learntest.util.LearnTestUtil;

public class EvaluationHandler extends AbstractHandler {

	class ValidMethodCollector extends ASTVisitor{
		
		List<MethodDeclaration> mdList = new ArrayList<>();
		
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
		IPackageFragmentRoot root = LearnTestUtil.findMainPackageRootInProject();
		
		Job job = new Job("Do evaluation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					for(IJavaElement element: root.getChildren()){
						if(element instanceof IPackageFragment){
							runEvaluation((IPackageFragment)element);			
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				return Status.OK_STATUS;
			}

			private void runEvaluation(IPackageFragment pack) throws JavaModelException {
				for(IJavaElement javaElement: pack.getChildren()){
					if(javaElement instanceof IPackageFragment){
						runEvaluation((IPackageFragment)javaElement);
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
								String methodName = method.getName().getIdentifier();
								LearnTestConfig.testMethodName = methodName;
								
								try{
									new GenerateTestHandler().generateTest();									
								}
								catch(Exception e){
									System.out.println(e);
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
