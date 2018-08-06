package learntest.evaluation.jdart;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gentest.junit.FileCompilationUnitPrinter;
import japa.parser.ast.CompilationUnit;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.SignatureUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.ClassLocation;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class MainClassGenerator {
	private JavaCompiler javaCompiler;
	private AppJavaClassPath appClasspath;
	
	public MainClassGenerator(AppJavaClassPath appClasspath) {
		javaCompiler = new JavaCompiler(new VMConfiguration(appClasspath));
		this.appClasspath = appClasspath;
	}
	
	public String generate(List<String> junitMethods, String pkgName, String classPrefix,
			String srcFolderPath) throws SavException {
		MainClassJWriter jwriter = new MainClassJWriter(pkgName, classPrefix);
		List<ClassLocation> methodLocs = toMethodLocations(junitMethods);
		CompilationUnit cu = jwriter.write(methodLocs);
		FileCompilationUnitPrinter printer = new FileCompilationUnitPrinter(srcFolderPath);
		printer.print(Arrays.asList(cu));
		List<File> generatedFiles = printer.getGeneratedFiles();
		javaCompiler.compile(appClasspath.getTestTarget(), generatedFiles);
		return ClassUtils.getCanonicalName(cu.getPackage().getName().getName(), cu.getTypes().get(0).getName());
	}
	
	private static List<ClassLocation> toMethodLocations(List<String> methods) {
		List<ClassLocation> locs = new ArrayList<>(methods.size());
		for (String methodFullName : methods) {
			Pair<String, String> pair = JunitUtils.toPair(methodFullName);
			locs.add(new ClassLocation(pair.a, SignatureUtils.extractMethodName(pair.b), -1));
		}
		return locs;
	}
}
