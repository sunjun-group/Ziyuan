package learntest.gentest;

import learntest.gentest.TypeUtils;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import learntest.gentest.ParamGeneratorConfig;
import sav.common.core.Pair;
import sav.common.core.SavRtException;

public class PrimitiveParameterGenerator {

	private ParamGeneratorConfig config = ParamGeneratorConfig.getDefault();
	private Map<Object, PrimitiveGenerator<?>> generators = new HashMap<Object, PrimitiveGenerator<?>>();;

	public void f() throws ParseException, IOException {
		CompilationUnit cu = JavaParser
				.parse(new File(
						"D:\\Ziyuan\\app\\learntest\\src\\main\\java\\learntest\\cfg\\MiniTest.txt"));
		for (TypeDeclaration type : cu.getTypes()) {
			for (BodyDeclaration body : type.getMembers()) {
				if (body instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) body;
					System.out
							.println("---------------------------------------------");
					System.out.println(method.getName()
							+ method.getParameters());
					for (Parameter parameter : method.getParameters()) {
						System.out
								.println(getGenerator(
										convertTypeToClass(parameter.getType()))
										.next());
					}
					System.out
							.println("---------------------------------------------");
				}
			}
		}

	}

	public Class<?> convertTypeToClass(Type type) {
		if (type.toString().equals("int")) {
			return int.class;
		} else if (type.toString().equals("double")) {
			return double.class;
		} else if (type.toString().equals("float")) {
			return float.class;
		} else if (type.toString().equals("long")) {
			return long.class;
		} else if (type.toString().equals("boolean")) {
			return boolean.class;
		} else if (type.toString().equals("short")) {
			return short.class;
		} else if (type.toString().equals("char")) {
			return char.class;
		} else if (type.toString().equals("byte")) {
			return byte.class;
		} else if (type.toString().equals("String")) {
			return String.class;
		}

		return null;

	}

	public PrimitiveGenerator<?> getGenerator(Class<?> key) {
		PrimitiveGenerator<?> generator = getGeneratorIfPrimitiveOrWrapper(key);
		if (generator == null) {
			generator = getGeneratorIfString(key);
		}
		if (generator == null) {
			throw new SavRtException("Primitive value generator for type "
					+ key + " is not defined!");
		}
		return generator;
	}

	private PrimitiveGenerator<?> getGeneratorIfString(Class<?> key) {
		if (TypeUtils.isString(key)) {
			PrimitiveGenerator<?> generator = generators.get(String.class);
			if (generator == null) {
				generator = new StringGenerator();
				generators.put(String.class, generator);
			}
			return generator;
		}
		return null;
	}

	private PrimitiveGenerator<?> getGeneratorIfPrimitiveOrWrapper(Class<?> type) {
		Primitive primitiveType = TypeUtils.getAssociatePrimitiveType(type);
		if (primitiveType == null) {
			return null;
		}

		PrimitiveGenerator<?> generator = generators.get(primitiveType);
		if (generator == null) {
			switch (primitiveType) {
			case Boolean:
				generator = new BooleanGenerator();
				break;
			case Byte:
				generator = new ByteGenerator();
				break;
			case Char:
				generator = new CharGenerator();
				break;
			case Double:
				generator = new DoubleGenerator();
				break;
			case Float:
				generator = new FloatGenerator();
				break;
			case Int:
				generator = new IntGenerator();
				break;
			case Long:
				generator = new LongGenerator();
				break;
			case Short:
				generator = new ShortGenerator();
				break;
			default:
				throw new SavRtException("Missing generator for type "
						+ primitiveType);
			}
			generators.put(primitiveType, generator);
		}
		return generator;
	}

	static abstract class PrimitiveGenerator<T> {
		public abstract T next();

		public T next(Class<?> type) {
			return next();
		}
	}

	private class BooleanGenerator extends PrimitiveGenerator<Boolean> {

		@Override
		public Boolean next() {
			return Randomness.nextBoolean();
		}
	}

	private class ByteGenerator extends PrimitiveGenerator<Byte> {

		@Override
		public Byte next() {
			// copy from the old implementation
			byte[] retByte = new byte[1];
			Randomness.nextBytes(retByte);
			return retByte[0];
		}

	}

	// copy from the old implementation
	private class CharGenerator extends PrimitiveGenerator<Character> {

		@Override
		public Character next() {
			// The set of visible characters in the ASCII staring from the
			// 'space'
			// (code-point: 32) character to the 'tide'(code-point: 126)
			// character.
			return (char) (Randomness.nextInt(95) + 32);
		}
	}

	private class DoubleGenerator extends PrimitiveGenerator<Double> {
		@Override
		public Double next() {
			return Randomness.nextDouble();
		}
	}

	private class FloatGenerator extends PrimitiveGenerator<Float> {
		@Override
		public Float next() {
			return Randomness.nextFloat();
		}
	}

	private class IntGenerator extends PrimitiveGenerator<Integer> {
		@Override
		public Integer next() {
			Pair<Integer, Integer> range = config.getIntRanges().randomRange();
			return Randomness.nextInt(range.a, range.b);
		}
	}

	private class LongGenerator extends PrimitiveGenerator<Long> {
		@Override
		public Long next() {
			return Randomness.nextLong();
		}
	}

	private class ShortGenerator extends PrimitiveGenerator<Short> {

		@Override
		public Short next() {
			return (short) ((Randomness.nextInt(Short.MAX_VALUE
					- Short.MIN_VALUE) + Short.MIN_VALUE));
		}
	}

	private class StringGenerator extends PrimitiveGenerator<String> {

		@Override
		public String next() {
			return RandomStringUtils.randomAlphabetic(Randomness.nextInt(config
					.getStringMaxLength()));
		}
	}

	// private class StringGenerator extends PrimitiveGenerator<String> {
	//
	// @Override
	// public String next() {
	// return RandomStringUtils.randomAlphabetic(Randomness.nextInt(config
	// .getStringMaxLength()));
	// }
	// }
	//
	// private static class EnumGenerator extends PrimitiveGenerator<Enum<?>> {
	//
	// @Override
	// public Enum<?> next(Class<?> type) {
	// Object[] constValues = type.getEnumConstants();
	// int index = Randomness.nextInt(constValues.length);
	// return (Enum<?>) constValues[index];
	// }
	//
	// @Override
	// public Enum<?> next() {
	// throw new
	// SavRtException("next() function is not supported for enum, call next(Class<?> type) instead");
	// }
	//
	// }
}
