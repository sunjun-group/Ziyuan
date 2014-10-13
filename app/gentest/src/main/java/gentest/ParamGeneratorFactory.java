/**
 * Copyright TODO
 */
package gentest;


import gentest.commons.utils.Randomness;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author LLT
 */
public class ParamGeneratorFactory {
	private ParamGeneratorConfig config;
	private LoadingCache<Class<?>, Generator<?>> generatorCache;
	private static ParamGeneratorFactory instance;
	
	public ParamGeneratorFactory() {
		resetConfig(ParamGeneratorConfig.getDefault());
	}
	
	public void resetConfig(ParamGeneratorConfig config) {
		if (!config.equals(this.config)) {
			this.config = config;
			// reset cache
			if (generatorCache == null) {
				generatorCache = CacheBuilder.newBuilder()
						.build(getCacheLoader());
			} else {
				generatorCache.cleanUp();
			}
		}
	}
	
	private CacheLoader<Class<?>, Generator<?>> getCacheLoader() {
		return new CacheLoader<Class<?>, Generator<?>>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Generator<?> load(Class<?> key) throws Exception {
				if (key == boolean.class || key == Boolean.class) {
					return PrimitiveGenerators.booleans();
				}
				if (key == byte.class || key == Byte.class) {
					return PrimitiveGenerators.bytes();
				}
				if (key == char.class || key == Character.class) {
					return PrimitiveGenerators.characters();
				}
				if (key == double.class || key == Double.class) {
					return PrimitiveGenerators.doubles();
				}
				if (key == float.class || key == Float.class) {
					return new Generator<Float>() {

						public Float next() {
							return Randomness.nextFloat();
						}
					};
				}
				if (key == int.class || key == Integer.class) {
					return PrimitiveGenerators.integers();
				}
				if (key == long.class || key == Long.class) {
					return PrimitiveGenerators.longs();
				}
				if (key == short.class || key == Short.class) {
					return PrimitiveGenerators.integers(Short.MIN_VALUE, Short.MAX_VALUE);
				}
				if (key == String.class) {
					return PrimitiveGenerators.printableStrings();
				}
				if (key == Date.class) {
					return PrimitiveGenerators.dates();
				}
				if (key == Object.class) {
					return PrimitiveGenerators.objects();
				}
				if (key.isEnum()) {
					return PrimitiveGenerators.enumValues((Class)key);
				}
				if (key == List.class) {
					return CombinedGenerators.lists(
							getGeneratorFor(key.getComponentType()));
				}
				if (key.isArray()) {
					return CombinedGenerators.arrays(
							(Generator)getGeneratorFor(key.getComponentType()), (Class)key);
				}
				return PrimitiveGenerators.nulls();
			}
			
		};
	}

	public Generator<?> getGeneratorFor(final Class<?> type) throws SavException {
		try {
			return generatorCache.get(type);
		} catch (ExecutionException e) {
			throw new SavException(ModuleEnum.TESTCASE_GENERATION, e);
		}
	}
	
	public static ParamGeneratorFactory getInstance() {
		if (instance == null) {
			instance = new ParamGeneratorFactory();
		}
		return instance;
	}
}
