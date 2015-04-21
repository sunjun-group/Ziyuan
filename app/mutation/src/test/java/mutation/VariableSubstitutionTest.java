package mutation;

import java.util.List;

import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.IJavaParser;
import mutation.parser.JParser;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.CollectionUtils;

public class VariableSubstitutionTest {



	@Before
	public void setup() {
		String sourceFolder = "./src/test/resources";
		String className = "mutation.mutator.VariableSubstitutionClass";
		
		IJavaParser javaParser = new JParser(sourceFolder , CollectionUtils.listOf(className));
		ClassAnalyzer analyzer = new ClassAnalyzer(sourceFolder, javaParser);
		List<ClassDescriptor> descriptors = analyzer.analyzeCompilationUnit(javaParser.parse(className));
		System.out.print(1);
		
	}

	@Test
	public void whenLocalVariableInSameScopeMatch(){
		
	}
	
	@Test
	public void whenlocalVariableInSameScopeNotMatch() {

	}

	@Test
	public void whenLocalVariableInParentScopeMatch() {

	}

	@Test
	public void whenLocalVariableInParentScopeNotMatch() {

	}

	@Test
	public void whenClassFieldMatch() {

	}

	@Test
	public void whenClassFieldNotMatch() {

	}

	@Test
	public void whenOuterClassFieldMatch() {

	}

	@Test
	public void whenOuterClassFieldNotMatch() {

	}

}
