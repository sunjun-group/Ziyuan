package assertion.visitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import mutation.io.DebugLineFileWriter;
import mutation.mutator.VariableSubstitution;
import mutation.mutator.insertdebugline.DebugLineData;
import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.JParser;
import sav.common.core.utils.ClassUtils;
import sav.commons.TestConfiguration;

public class AddAssertStmtVisitorTest {

	@Test
	public void test() throws Exception {
		String srcFolder = TestConfiguration.getTestScrPath("assertion"); 
		String className = TestInput.class.getName();
		
		File file = new File(ClassUtils.getJFilePath(srcFolder, className));
		
		JParser parser = new JParser(srcFolder, new ArrayList<String>());
		ClassAnalyzer classAnalyser = new ClassAnalyzer(srcFolder, parser);
		
		List<ClassDescriptor> classDecriptors = classAnalyser.analyzeJavaFile(file);
		VariableSubstitution subst = new VariableSubstitution(classDecriptors.get(0));
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu = JavaParser.parse(in);
		// AddAssertStmtVisitor visitor = new AddAssertStmtVisitor(subst);
		AddAssertStmtVisitor visitor = new AddAssertStmtVisitor(cu.getImports(), subst);
		
		List<DebugLineData> arg = new ArrayList<DebugLineData>();
		visitor.visit(cu, arg);
		
		DebugLineFileWriter writer = new DebugLineFileWriter(srcFolder);
		File newFile = writer.write(arg, className);
		
		// System.out.println(newFile.toString());
		BufferedReader br = new BufferedReader(new FileReader(newFile));
		
		String s = br.readLine();
		while (s != null) {
			System.out.println(s);
			s = br.readLine();
		}
		
		br.close();
		in.close();
	}

}
