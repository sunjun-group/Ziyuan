package microbat.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class JavaUtil {
	public static ICompilationUnit findCompilationUnitInProject(String qualifiedName){
		
		IJavaProject project = JavaCore.create(getSpecificJavaProjectInWorkspace());
		try {
			IType type = project.findType(qualifiedName);
			if(type != null){
				return type.getCompilationUnit();
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static IProject getSpecificJavaProjectInWorkspace(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		for(int i=0; i<projects.length; i++){
			if(Settings.projectName.equals(projects[i].getName())){
				return projects[i];
				//return JavaCore.create(projects[i]);
			}
		}
		
		return null;
	}
}
