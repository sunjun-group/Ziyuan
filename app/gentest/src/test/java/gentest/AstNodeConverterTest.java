/**
 * Copyright TODO
 */
package gentest;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.Rmethod;
import gentest.junit.AstNodeConverter;
import gentest.junit.variable.VariableNamer;
import japa.parser.ast.type.PrimitiveType.Primitive;

/**
 * @author LLT
 */
public class AstNodeConverterTest {
	private AstNodeConverter astConverter;
	
	@Before
	public void setup() {
		VariableNamer varNamer = new VariableNamer();
		Set<String> duplicatedImports = new HashSet<String>();
		duplicatedImports.add(Primitive.class.getCanonicalName());
		astConverter = new AstNodeConverter(varNamer, duplicatedImports);
	}
	
	@Test
	public void testFromRMethod() throws SecurityException, NoSuchMethodException {
		Rmethod method = Rmethod.of(String.class.getMethod(
				"substring", int.class, int.class), 3);
		method.setInVarIds(new int[]{1, 2});
		method.setOutVarId(4);
		astConverter.reset();
		astConverter.visitRmethod(method);
		System.out.println(astConverter.getResult());
	}
	
	@Test
	public void testFromConstructor() {
		convert(String.class.getConstructors()[1], 1);
	}
	
	

	private void convert(Constructor<?> ctor, int... inVar) {
		RConstructor constructor = RConstructor.of(ctor);
		constructor.setInVarIds(inVar);
		astConverter.reset();
		astConverter.visit(constructor);
		System.out.println(astConverter.getResult());
	}

	@Test
	public void testFromRAssignment() {
		convert(int.class, new Integer(2));
		convert(byte.class, 0x20);
		convert(long.class, 123423412l);
		convert(Integer.class, 21);
		convert(String.class, "asfsdf");
		convert(Enum.class, Primitive.Double);
		convert(Primitive.class, Primitive.Byte);
		convert(Primitive.class, null);
	}

	private void convert(Class<?> clazz, Object value) {
		RAssignment assignment = RAssignment.assignmentFor(clazz, value);
		astConverter.reset();
		astConverter.visit(assignment);
		System.out.println(astConverter.getResult());
	}
}
