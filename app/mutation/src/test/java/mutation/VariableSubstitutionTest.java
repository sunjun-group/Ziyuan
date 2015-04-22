package mutation;

import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;

import java.util.List;

import mutation.mutator.VariableSubstitution;
import mutation.mutator.VariableSubstitutionImpl;
import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.IJavaParser;
import mutation.parser.JParser;
import mutation.parser.VariableDescriptor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.CollectionUtils;

public class VariableSubstitutionTest {
	private List<ClassDescriptor> descriptors;
	private VariableSubstitution variableSubstitution;
	
	@Before
	public void setup() {
		String sourceFolder = "./src/test/resources";
		String className = "mutation.mutator.VariableSubstitutionClass";
		
		IJavaParser javaParser = new JParser(sourceFolder , CollectionUtils.listOf(className));
		ClassAnalyzer analyzer = new ClassAnalyzer(sourceFolder, javaParser);
		descriptors = analyzer.analyzeCompilationUnit(javaParser.parse(className));
	}

	@Test
	public void whenLocalVariableInSameScopeMatch(){
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Int), 15, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertTrue(containsVar(candidates, "x1"));
		Assert.assertTrue(containsVar(candidates, "x2"));
	}
	
	@Test
	public void whenlocalVariableInSameScopeNotVisible() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Int), 13, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertFalse(containsVar(candidates, "x2"));
	}
	
	@Test
	public void whenlocalVariableInSameScopeNotMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Int), 13, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertFalse(containsVar(candidates, "x0"));
	}

	@Test
	public void whenLocalVariableInParentScopeMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Double), 25, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertTrue(containsVar(candidates, "y2"));
	}

	@Test
	public void whenLocalVariableInParentScopeNotMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Double), 25, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertFalse(containsVar(candidates, "y1"));
	}

	@Test
	public void whenLocalVariableInChildScopeMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Double), 22, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertTrue(containsVar(candidates, "y2"));
		Assert.assertFalse(containsVar(candidates, "y3"));
	}
	
	@Test
	public void whenLocalVariableInChildScopeButSameLineMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Int), 38, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertFalse(containsVar(candidates, "p2"));
	}
	
	@Test
	public void whenClassFieldMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Int), 15, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertTrue(containsVar(candidates, "a"));
		Assert.assertTrue(containsVar(candidates, "c"));
		
	}

	@Test
	public void whenClassFieldNotMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Int), 15, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertFalse(containsVar(candidates, "b"));
		Assert.assertFalse(containsVar(candidates, "d"));
	}

	@Test
	public void whenOuterClassFieldMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Double), 33, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertTrue(containsVar(candidates, "z2"));
		Assert.assertTrue(containsVar(candidates, "z3"));
		Assert.assertTrue(containsVar(candidates, "b"));
	}

	@Test
	public void whenOuterClassFieldNotMatch() {
		variableSubstitution = new VariableSubstitutionImpl(new PrimitiveType(Primitive.Double), 33, descriptors.get(0));
		List<VariableDescriptor> candidates = variableSubstitution.find();
		
		Assert.assertFalse(containsVar(candidates, "a"));
	}

	private boolean containsVar(List<VariableDescriptor> candidates, String varName){
		for(VariableDescriptor candidate: candidates){
			if(candidate.getName().equals(varName)){
				return true;
			}
		}
		
		return false;
	}
}
