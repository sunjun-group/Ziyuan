package tzuyu.plugin.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * Provides methods for various JDT and Java-related tasks that are often
 * performed by the Randoop plug-in
 * 
 * @author LLT
 * @author Peter Kalauskas [Randoop, RandoopCoreUtil]
 */
public class ResourcesUtils {

	/**
	 * Returns a method signature in which every type is uses its
	 * fully-qualified name and is written using the identifier for unresolved
	 * types.
	 */
	public static String getUnresolvedFullyQualifiedMethodSignature(
			IMethod method, String typeSignature) throws JavaModelException {
		IType type = method.getDeclaringType();

		int arrayCount = Signature.getArrayCount(typeSignature);
		String typeSignatureWithoutArray = typeSignature.substring(arrayCount);

		String typeName = Signature.toString(typeSignatureWithoutArray);
		if (method.getTypeParameter(typeName).exists()
				|| type.getTypeParameter(typeName).exists()) {
			String typeSig = Signature.C_TYPE_VARIABLE + typeName
					+ Signature.C_SEMICOLON;
			return Signature.createArraySignature(typeSig, arrayCount);
		}
		String[][] types = type.resolveType(typeName);

		StringBuilder fqname = new StringBuilder();
		if (types != null) {
			// Write the first type that was resolved
			fqname.append(types[0][0]); // the package name
			fqname.append('.');
			fqname.append(types[0][1]); // the class name

			String typeSig = Signature.createTypeSignature(fqname.toString(),
					false);
			return Signature.createArraySignature(typeSig, arrayCount);
		} else {
			// Otherwise this is a primitive type, return the signature as it is
			return typeSignature;
		}

	}

	/**
	 * Returns the package name of the given fully-qualified name. The expected
	 * enclosing type separator is <code>'$'</code>.
	 */
	public static String getPackageName(String fullyQualifiedName) {
		int lastDelimiter = fullyQualifiedName.lastIndexOf('.');

		if (lastDelimiter == -1) {
			return ""; //$NON-NLS-1$
		} else {
			return fullyQualifiedName.substring(0, lastDelimiter);
		}
	}

	/**
	 * Returns the class name of the given fully-qualified name. The expected
	 * enclosing type separator is <code>'$'</code>. The class name will return
	 * all parent types as well. For example,
	 * <code>'com.example.Graph$Node'</code> will return
	 * <code>'Graph$Node'</code>
	 * 
	 */
	public static String getClassName(String fullyQualifiedName) {
		int lastDelimiter = fullyQualifiedName.lastIndexOf('.');

		if (lastDelimiter == -1) {
			return fullyQualifiedName;
		} else {
			return fullyQualifiedName.substring(lastDelimiter + 1);
		}
	}

	/**
	 * Creates a fully-qualified type name for the given package name and class
	 * name separated by a <code>'.'</code>. If the package name is empty, only
	 * the class name is returned.
	 */
	private static String getFullyQualifiedName(String packageName,
			String className) {
		if (packageName.length() == 0) {
			return className;
		} else {
			return packageName + '.' + className;
		}
	}

	/**
	 * Returns the IProject by the specified name in the workspace. To convert
	 * the project to a Java project use JavaCore.create(project)
	 */
	public static IProject getProjectFromName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);

		if (status.isOK()) {
			IProject project = workspace.getRoot().getProject(projectName);

			if (!project.exists())
				return null;

			return project;
		}
		return null;
	}

	/**
	 * Searches for and returns a list of types found in the given java element
	 * that may be used for testing. This is simply a <code>switch</code>
	 * statement that checks the element's type and calls the appropriate
	 * method.
	 */
	public static List<IType> findTestableTypes(IJavaElement element,
			boolean ignoreJUnitTestCases) {
		switch (element.getElementType()) {
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			IPackageFragmentRoot pfr = (IPackageFragmentRoot) element;
			return findTestableTypes(pfr, ignoreJUnitTestCases);
		case IJavaElement.PACKAGE_FRAGMENT:
			IPackageFragment pf = (IPackageFragment) element;
			return findTestableTypes(pf, ignoreJUnitTestCases);
		case IJavaElement.COMPILATION_UNIT:
			ICompilationUnit cu = (ICompilationUnit) element;
			return findTestableTypes(cu, ignoreJUnitTestCases);
		case IJavaElement.CLASS_FILE:
			IClassFile cf = (IClassFile) element;
			return findTestableTypes(cf, ignoreJUnitTestCases);
		}
		return null;
	}
	
	public static List<IType> findTestableTypes(IPackageFragmentRoot pfr,
			boolean ignoreJUnitTestCases) {
		List<IType> types = new ArrayList<IType>();
		try {
			IJavaElement[] children = pfr.getChildren();
			for (IJavaElement e : children) {
				Assert.isTrue(e instanceof IPackageFragment);
				IPackageFragment pf = (IPackageFragment) e;
				types.addAll(findTestableTypes(pf, ignoreJUnitTestCases));
			}
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
		}

		return types;
	}

	public static List<IType> getTypes(ICompilationUnit cu, boolean ignoreJUnitTestCases) {
		List<IType> validTypes = new ArrayList<IType>();
		if (cu != null && cu.exists()) {
			try {
				IType[] allTypes = cu.getAllTypes();
				for (IType t : allTypes) {
					if (isValidTestInput(t, ignoreJUnitTestCases)) {
						validTypes.add(t);
					}
				}
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e);
			}
		}

		return validTypes;
	}

	public static boolean isValidTestInput(IType type,
			boolean ignoreJUnitTestCases) {
		return true;
	}

	/**
	 * TODO nice to have
	 * Find out why IJavaProject.findPackageFragmentRoots returns an empty
	 * list for IClasspathEntrys of kind CPE_PROJECT.
	 * 
	 * This is a workaround that find the actual project that is referenced and
	 * iterates through its raw classpath, searching for classpath entries that
	 * are exported.
	 * 
	 */
	public static IPackageFragmentRoot[] findPackageFragmentRoots(
			IJavaProject javaProject, IClasspathEntry classpathEntry)
			throws JavaModelException {

		if (classpathEntry == null) {
			return null;
		}

		if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
			IWorkspace workspace = javaProject.getProject().getWorkspace();
			IProject project = workspace.getRoot().getProject(
					classpathEntry.getPath().toString());

			if (project.exists()) {
				IJavaProject referencedJavaProject = JavaCore.create(project);
				List<IPackageFragmentRoot> roots = new ArrayList<IPackageFragmentRoot>();
				for (IClasspathEntry cpe : referencedJavaProject
						.getRawClasspath()) {
					if (cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE
							|| cpe.isExported()) {
						roots.addAll(Arrays.asList(findPackageFragmentRoots(
								referencedJavaProject, cpe)));
					}
				}
				return (IPackageFragmentRoot[]) roots
						.toArray(new IPackageFragmentRoot[roots.size()]);
			}
			return null;
		} else {
			return javaProject.findPackageFragmentRoots(classpathEntry);
		}

	}

	/**
	 * Checks whether the given resource is a Java artifact (i.e. either a Java
	 * source file or a Java class file).
	 */
	public static boolean isJavaArtifact(IResource resource) {
		if (resource == null || (resource.getType() != IResource.FILE)) {
			return false;
		}
		String ex = resource.getFileExtension();
		if ("java".equalsIgnoreCase(ex) || "class".equalsIgnoreCase(ex)) {
			return true;
		}
		String name = resource.getName();
		return ArchiveUtils.isArchiveFileName(name);
	}

	public static boolean isJavaProject(IProject project) {
		try {
			return project != null && project.isAccessible()
					&& project.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			PluginLogger.getLogger().logEx(e, "couldn't determine project nature");
			return false;
		}
	}
	
    public static IPath relativeToAbsolute(IPath relativePath) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(relativePath);
        if (resource != null) {
            return resource.getLocation();
        }
        return relativePath;
    }
    
	public static List<IPath> getSourcePaths(IJavaProject project)
			throws PluginException {
		List<IPath> sourcePaths = new ArrayList<IPath>();
		try {
			for (IClasspathEntry entry : project.getResolvedClasspath(true)) {
				IPath path = entry.getPath();
				if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
					sourcePaths.add(ResourcesUtils.relativeToAbsolute(path));
				}
			}
		} catch (JavaModelException e) {
			PluginException.wrapEx(e);
		}
		return sourcePaths;
	}

	public static IPath getCorrespondingSource(IPath path,
			List<IPath> sourcePaths) {
		for (IPath scr : sourcePaths) {
			IPath filePath = path;
			if (scr.isPrefixOf(filePath)) {
				return scr;
			}
		}
		return null;
	}

	public static String getFullQualifiedName(IPath filePath, IPath scr) {
		int classFirstSegment = filePath.matchingFirstSegments(scr);
		// from relative path to package string
		return StringUtils.dotJoin((Object[])filePath
				.removeFirstSegments(classFirstSegment)
				.removeFileExtension().segments());
	}
	
	public static String getFullName(IType type) {
		if (type == null) {
			return StringUtils.EMPTY;
		}
		return StringUtils.dotJoin(getTypeContainerName(type), type.getElementName());
	}
	
	public static String getTypeContainerName(IType type) {
		IType outerType = type.getDeclaringType();
		if (outerType != null) {
			return outerType.getFullyQualifiedName('.');
		} else {
			return type.getPackageFragment().getElementName();
		}
	}
	
	public static boolean isPublicNotInterfaceOrAbstract(IType type) {
		int flags;
		try {
			flags = type.getFlags();
			return Flags.isPublic(flags) && !Flags.isInterface(flags)
					&& !Flags.isAbstract(flags);
		} catch (JavaModelException e) {
			return false;
		}
	}
	
	/**
	 * Returns a method signature in which every type is uses its
	 * fully-qualified name and is written using the identifier for unresolved
	 * types.
	 */
	public static String getParamTypeName(IMethod method, String typeSignature)
			throws JavaModelException {
		IType type = method.getDeclaringType();

		int arrayCount = Signature.getArrayCount(typeSignature);
		String typeSignatureWithoutArray = typeSignature.substring(arrayCount);

		String typeName = Signature.toString(typeSignatureWithoutArray);
		if (method.getTypeParameter(typeName).exists()
				|| type.getTypeParameter(typeName).exists()) {
			String typeSig = Signature.C_TYPE_VARIABLE + typeName
					+ Signature.C_SEMICOLON;
			return Signature.createArraySignature(typeSig, arrayCount);
		}
		String[][] types = type.resolveType(typeName);

		StringBuilder fqname = new StringBuilder();
		if (types != null) {
			// Write the first type that was resolved
			fqname.append(types[0][0]); // the package name
			fqname.append('.');
			fqname.append(types[0][1]); // the class name

			String typeSig = Signature.createTypeSignature(fqname.toString(),
					false);
			return Signature.createArraySignature(typeSig, arrayCount);
		} else {
			// Otherwise this is a primitive type, return the signature as it is
			return typeSignature;
		}

	}
}
