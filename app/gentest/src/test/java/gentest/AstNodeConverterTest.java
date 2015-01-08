/**
 * Copyright TODO
 */
package gentest;

import java.lang.reflect.Constructor;

import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.statement.Rmethod;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.PrimitiveType.Primitive;
import junit.AstNodeConverter;
import junit.variable.VariableNamer;

import org.junit.Before;
import org.junit.Test;

/**
 * @author LLT
 */
public class AstNodeConverterTest {
	private AstNodeConverter astConverter;
	
	@Before
	public void setup() {
		VariableNamer varNamer = new VariableNamer();
		astConverter = new AstNodeConverter(varNamer);
	}
	
	@Test
	public void testFromRMethod() throws SecurityException, NoSuchMethodException {
		Rmethod method = Rmethod.of(String.class.getMethod(
				"substring", int.class, int.class), 3);
		method.setInVarIds(new int[]{1, 2});
		method.setOutVarId(4);
		Statement stmt = astConverter.fromRMethod(method);
		System.out.println(stmt.toString());
	}
	
	@Test
	public void testFromConstructor() {
		convert(String.class.getConstructors()[1], 1);
	}
	
	

	private void convert(Constructor<?> ctor, int... inVar) {
		RConstructor constructor = RConstructor.of(ctor);
		constructor.setInVarIds(inVar);
		Statement stmt = astConverter.fromRConstructor(constructor);
		System.out.println(stmt.toString());
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
		Statement stmt = astConverter.fromRAssignment(assignment);
		System.out.println(stmt);
	}
}
