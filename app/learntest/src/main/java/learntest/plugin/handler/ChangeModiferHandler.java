package learntest.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import learntest.util.LearnTestUtil;
import sav.settings.SAVTimer;

public class ChangeModiferHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		final IPackageFragmentRoot root = LearnTestUtil.findMainPackageRootInProject();
		final List<IPackageFragmentRoot> roots = LearnTestUtil.findAllPackageRootInProject();
		Job job = new Job("Do evaluation") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				SAVTimer.enableExecutionTimeout = true;
				SAVTimer.exeuctionTimeout = 100000;

				try {
					for(IPackageFragmentRoot root: roots){
						for (IJavaElement element : root.getChildren()) {
							if (element instanceof IPackageFragment) {
								changeModifier((IPackageFragment) element);
							}
						}						
					}
					
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

				
			private int changeModifier(IPackageFragment pack) throws JavaModelException {
				int sum = 0;
				
				for (IJavaElement javaElement : pack.getChildren()) {
					if (javaElement instanceof IPackageFragment) {
						changeModifier((IPackageFragment) javaElement);
					} else if (javaElement instanceof ICompilationUnit) {
						ICompilationUnit icu = (ICompilationUnit) javaElement;
						CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);

						if(cu.types().isEmpty()){
							continue;
						}
						
						AbstractTypeDeclaration td = (AbstractTypeDeclaration) cu.types().get(0);
						if(!(td instanceof TypeDeclaration)){
							continue;
						}
						
						MethodModifier collector = new MethodModifier(cu);
						cu.accept(collector);
						
						ASTRewrite rewrite = ASTRewrite.create(cu.getAST());
						
						for(MethodDeclaration md: collector.nonpublicMethods){
							Modifier modifier = null;
							for(Object obj: md.modifiers()){
								if(obj instanceof Modifier){
									Modifier mod = (Modifier)obj;
									
									if(mod.getKeyword()==ModifierKeyword.PRIVATE_KEYWORD || mod.getKeyword()==ModifierKeyword.PROTECTED_KEYWORD){
										modifier = mod;
									}
								}
							}
							
							if(modifier == null){
								continue;
							}
							
							ASTNode newModifier = modifier.getAST().newModifier(ModifierKeyword.PUBLIC_KEYWORD);
							rewrite.replace(modifier, newModifier, null);
							
						}
						
						if(!collector.nonpublicMethods.isEmpty()){
							ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); // get the buffer manager
							IPath path = cu.getJavaElement().getPath(); // unit: instance of CompilationUnit
							try {
								bufferManager.connect(path, null); // (1)
								ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
								// retrieve the buffer
								IDocument document = textFileBuffer.getDocument(); 
								
								// ... edit the document here ...
								IProject project = LearnTestUtil.getSpecificJavaProjectInWorkspace();
								TextEdit edit = rewrite.rewriteAST(document, JavaCore.create(project).getOptions(true));
								edit.apply(document);
								// commit changes to underlying file
								textFileBuffer.commit(null, false); // (3)

								System.currentTimeMillis();
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									bufferManager.disconnect(path, null);
								} catch (CoreException e) {
									e.printStackTrace();
								} 
							}
						}
						
						
					}
				}

				
				return sum;
			}


			
		};

		job.schedule();

		return null;
	}

	class MethodModifier extends ASTVisitor{
		
		List<MethodDeclaration> nonpublicMethods = new ArrayList<MethodDeclaration>();
		
		CompilationUnit cu;
		
		public MethodModifier(CompilationUnit cu2) {
			this.cu = cu2;
		}

		public boolean visit(MethodDeclaration md){
			
			
			for(Object obj: md.modifiers()){
				if(obj instanceof Modifier){
					Modifier modifier = (Modifier)obj;
					
					if(modifier.getKeyword()==ModifierKeyword.PRIVATE_KEYWORD || modifier.getKeyword()==ModifierKeyword.PROTECTED_KEYWORD){
						nonpublicMethods.add(md);
					}
				}
			}
			
			return true;
		}
	}
}
