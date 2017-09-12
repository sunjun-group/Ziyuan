package cfgextractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

import sav.strategies.dto.AppJavaClassPath;

public class CFGBuilder {
	private static CFGBuilder INSTANCE;
	
	public CFG parsingCFG(AppJavaClassPath appClassPath, String className, String methodName, int lineNumber, String signature) throws Exception {
		
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
		Method method = findMethod(clazz, methodName, lineNumber, signature);
		System.currentTimeMillis();
		Code code = method.getCode();
		
		if(code == null){
			return null;
		}
		
		CFG cfg = new CFGConstructor().buildCFGWithControlDomiance(code);
		return cfg;
	}

	private Method findMethod(JavaClass clazz, String methodName, int lineNumber, String signature) {
		for(Method method: clazz.getMethods()){
			String mString = method.getSignature();
			String name =method.getName();
			methodName = methodName.substring(methodName.lastIndexOf('.')+1);
			if (name.equals(methodName) && mString.equals(signature)) {
				return method;
			}
			for(LineNumber lineNum: method.getLineNumberTable().getLineNumberTable()){
				if(lineNum.getLineNumber()==lineNumber){
					return method;
				}
			}
		}
		return null;
	}

	public static CFGBuilder getINSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new CFGBuilder();
		}
		return INSTANCE;
	}
}
