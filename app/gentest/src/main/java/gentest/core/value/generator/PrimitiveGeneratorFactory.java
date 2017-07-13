/**
 * Copyright TODO
 */
package gentest.core.value.generator;


import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import gentest.core.ParamGeneratorConfig;
import gentest.core.commons.utils.TypeUtils;
import gentest.injection.TestcaseGenerationScope;
import japa.parser.ast.type.PrimitiveType.Primitive;
import sav.common.core.SavRtException;

/**
 * @author LLT
 */
@TestcaseGenerationScope
public class PrimitiveGeneratorFactory {
	@Inject
	private ParamGeneratorConfig config;
	
	@Inject
	private IRandomness randomness;
	
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
			generator = getGeneratorIfEnum(key);
		}
		if (generator == null) {
			throw new SavRtException("Primitive value generator for type " + key + " is not defined!");
		}
		return generator;
	}
	
	public PrimitiveGenerator<?> getGeneratorIfEnum(Class<?> key) {
		if (TypeUtils.isEnumType(key)) {
			PrimitiveGenerator<?> generator = generators.get(Enum.class);
			if (generator == null) {
				generator = new EnumGenerator();
				generators.put(Enum.class, generator);
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
		abstract T next();
		
		public T next(Class<?> type) {
			return next();
		}
	}
	
	private class BooleanGenerator extends PrimitiveGenerator<Boolean> {
		
		@Override
		public Boolean next() {
			return randomness.randomBoolean();
		}
	}
	
	private class ByteGenerator extends PrimitiveGenerator<Byte> {

		@Override
		public Byte next() {
			// copy from the old implementation 
			byte[] retByte = new byte[1];
			randomness.randomBytes(retByte);
			return retByte[0];
		}
		
	}
	
	// copy from the old implementation 
	private class CharGenerator extends PrimitiveGenerator<Character> {
		
		@Override
		public Character next() {
			return randomness.randomChar();
		}
	}
	
	private class DoubleGenerator extends PrimitiveGenerator<Double> {
		@Override
		public Double next() {
			return randomness.randomDouble();
		}
	}
	
	private class FloatGenerator extends PrimitiveGenerator<Float> {
		@Override
		public Float next() {
			return randomness.randomFloat();
		}
	}
	
	private class IntGenerator extends PrimitiveGenerator<Integer> {
		@Override
		public Integer next() {
			return randomness.randomInt();
		}
	}
	
	private class LongGenerator extends PrimitiveGenerator<Long> {
		@Override
		public Long next() {
			return randomness.randomLong();
		}
	}
	
	private class ShortGenerator extends PrimitiveGenerator<Short> {

		@Override
		public Short next() {
			return randomness.randomShort();
		}
	}
	
	private class StringGenerator extends PrimitiveGenerator<String> {

		@Override
		public String next() {
			return randomness.randomAlphabetic(config
					.getStringMaxLength());
		}
	}
	
	private class EnumGenerator extends PrimitiveGenerator<Enum<?>> {
		
		@Override
		public Enum<?> next(Class<?> type) {
			return randomness.randomEnum(type.getEnumConstants());
		}
		
		@Override
		public Enum<?> next() {
			throw new SavRtException("next() function is not supported for enum, call next(Class<?> type) instead");
		}
		
	}
}
