package cfgextractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

import sav.strategies.dto.AppJavaClassPath;

public class CFGCreator {
	private static CFGCreator INSTANCE;
	
	public CFG parsingCFG(AppJavaClassPath appClassPath, String className, String methodName, int lineNumber) throws Exception {
		
		String originalSystemClassPath = System.getProperty("java.class.path");
		String[] paths = originalSystemClassPath.split(File.pathSeparator);
		List<String> pathList = new ArrayList<>();
		for(String path: paths){
			pathList.add(path);
		}
		StringBuffer buffer = new StringBuffer(originalSystemClassPath);
		for(String classPath: appClassPath.getClasspaths()){
			if(!pathList.contains(classPath)){
				buffer.append(File.pathSeparator + classPath);				
			}
		}
		System.setProperty("java.class.path", buffer.toString());
		String s = System.getProperty("java.class.path");
		
		/** current evaluation does not change line number, so we can keep the cache to speed up the progress */
		Repository.clearCache();				
		ClassPath classPath = new ClassPath(s);
		Repository.setRepository(SyntheticRepository.getInstance(classPath));
		
		
		JavaClass clazz = Repository.lookupClass(className);
		Method method = findMethod(clazz, methodName, lineNumber);
		
		Code code = method.getCode();
		
		if(code == null){
			return null;
		}
		
		CFG cfg = new CFGConstructor().buildCFGWithControlDomiance(code);
		return cfg;
	}

	private Method findMethod(JavaClass clazz, String methodName, int lineNumber) {
		for(Method method: clazz.getMethods()){
			if (!method.getName().equals(methodName)) {
				continue;
			}
			for(LineNumber lineNum: method.getLineNumberTable().getLineNumberTable()){
				if(lineNum.getLineNumber()==(lineNumber + 1) || lineNum.getLineNumber()==(lineNumber + 2)){
					return method;
				}
			}
		}
		return null;
	}

	public static CFGCreator getINSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new CFGCreator();
		}
		return INSTANCE;
	}
}
