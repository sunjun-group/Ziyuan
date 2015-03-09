/**
 * Copyright TODO
 */
package gentest.core.value.generator;


import gentest.core.ParamGeneratorConfig;
import gentest.core.commons.utils.TypeUtils;
import japa.parser.ast.type.PrimitiveType.Primitive;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import sav.common.core.SavRtException;

import com.google.inject.Inject;

/**
 * @author LLT
 */
public class PrimitiveGeneratorFactory {
	@Inject
	private ParamGeneratorConfig config;
	
	private Map<Object, PrimitiveGenerator<?>> generators;
	
	public PrimitiveGeneratorFactory() {
		generators = new HashMap<Object, PrimitiveGenerator<?>>();
	}
	
	public PrimitiveGenerator<?> getGenerator(Class<?> key) {
		PrimitiveGenerator<?> generator = getGeneratorIfPrimitiveOrWrapper(key);
		if (generator == null) {
			generator = getGeneratorIfString(key);
		}
		if (generator == null) {
			throw new SavRtException("Primitive value generator for type " + key + " is not defined!");
		}
		return generator;
	}
	
	public PrimitiveGenerator<?> getGeneratorIfEnum(Class<?> key) {
		if (TypeUtils.isEnum(key)) {
			PrimitiveGenerator<?> generator = generators.get(Enum.class);
			if (generator == null) {
				generator = new EnumGenerator();
				generators.put(String.class, generator);
			}
			return generator;
		}
		return null;
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
				throw new SavRtException("Missing generator for type " + primitiveType);
			}
			generators.put(primitiveType, generator);
		}
		return generator;
	}
	
	static abstract class PrimitiveGenerator<T> {
		protected Random random;
		public abstract T next();
		
		public PrimitiveGenerator() {
			random = new Random(System.currentTimeMillis());
		}
		
		public T next(Class<?> type) {
			return next();
		}
	}
	
	private class BooleanGenerator extends PrimitiveGenerator<Boolean> {
		
		@Override
		public Boolean next() {
			return random.nextBoolean();
		}
	}
	
	private class ByteGenerator extends PrimitiveGenerator<Byte> {

		@Override
		public Byte next() {
			// copy from the old implementation 
			byte[] retByte = new byte[1];
			random.nextBytes(retByte);
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
			return (char) (random.nextInt(95) + 32);
		}
	}
	
	private class DoubleGenerator extends PrimitiveGenerator<Double> {
		@Override
		public Double next() {
			return random.nextDouble();
		}
	}
	
	private class FloatGenerator extends PrimitiveGenerator<Float> {
		@Override
		public Float next() {
			return random.nextFloat();
		}
	}
	
	private class IntGenerator extends PrimitiveGenerator<Integer> {
		@Override
		public Integer next() {
			return random.nextInt();
		}
	}
	
	private class LongGenerator extends PrimitiveGenerator<Long> {
		@Override
		public Long next() {
			return random.nextLong();
		}
	}
	
	private class ShortGenerator extends PrimitiveGenerator<Short> {

		@Override
		public Short next() {
			return (short) ((random.nextInt(Short.MAX_VALUE - Short.MIN_VALUE) + Short.MIN_VALUE));
		}
	}
	
	private class StringGenerator extends PrimitiveGenerator<String> {

		@Override
		public String next() {
			return RandomStringUtils.randomAlphabetic(random.nextInt(config
					.getStringMaxLength()));
		}
	}
	
	private static class EnumGenerator extends PrimitiveGenerator<Enum<?>> {
		
		@Override
		public Enum<?> next(Class<?> type) {
			Object[] constValues = type.getEnumConstants();
			int index = random.nextInt(constValues.length);
			return (Enum<?>) constValues[index];
		}
		
		@Override
		public Enum<?> next() {
			throw new SavRtException("next() function is not supported for enum, call next(Class<?> type) instead");
		}
		
	}
}
