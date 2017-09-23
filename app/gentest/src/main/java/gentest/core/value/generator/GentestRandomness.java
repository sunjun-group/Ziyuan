/**
 * Copyright TODO
 */
package gentest.core.value.generator;

import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import gentest.injection.TestcaseGenerationScope;
import sav.common.core.utils.RandomUtils;

/**
 * @author LLT
 * for centralization
 */
@TestcaseGenerationScope
public class GentestRandomness implements IRandomness {
	private static final double PROB_VALUE_IN_LIST = 0.4;
	private Random random = new Random();
	
	private Random getRandom() {
		return random;
	}
	
	@Override
	public float randomFloat() {
		if (RandomUtils.weighedCoinFlip(PROB_VALUE_IN_LIST, random)) {
			return RandomUtils.randomMember(new Float[]{0.0f, 0.01f, -0.01f}, random);
		}
		
		float min = -100;
		float max = 100;
		float f = (min + (max - min) * getRandom().nextFloat());
		return f;
	}

	@Override
	public Boolean randomBoolean() {
		return getRandom().nextBoolean();
	}

	@Override
	public void randomBytes(byte[] bytes) {
		getRandom().nextBytes(bytes);
	}
	
	/**
	 * return random value from 0 to i - 1 
	 */
	@Override
	public int randomInt(int i) {
		return getRandom().nextInt(i);
	}

	@Override
	public Double randomDouble() {
		if (RandomUtils.weighedCoinFlip(PROB_VALUE_IN_LIST, random)) {
			return RandomUtils.randomMember(new Double[]{0.0, 0.01, -0.01}, random);
		}
		
		double min = -100;
		double max = 100;
		
		double d = (min + (max - min) * getRandom().nextDouble());
		return d;
	}

	@Override
	public int randomInt() {
		if (RandomUtils.weighedCoinFlip(PROB_VALUE_IN_LIST, random)) {
			return RandomUtils.randomMember(new Integer[]{-1 ,0, 1}, getRandom());
		}
		
		return RandomUtils.nextInt(-200, 200, getRandom());
	}

	@Override
	public Long randomLong() {
		if (RandomUtils.weighedCoinFlip(PROB_VALUE_IN_LIST, random)) {
			return RandomUtils.randomMember(new Long[]{0l, 1l, -1l}, random);
		}
		
		return RandomUtils.nextLong(-1000, 1000, getRandom());
	}

	@Override
	public Short randomShort() {
		if (RandomUtils.weighedCoinFlip(PROB_VALUE_IN_LIST, random)) {
			return RandomUtils.randomMember(new Short[]{-1 ,0, 1}, getRandom());
		}
		
		return (short) RandomUtils.nextInt(-200, 200, getRandom());
	}

	@Override
	public String randomAlphabetic(int stringMaxLength) {
		return RandomStringUtils.randomAlphabetic(getRandom().nextInt(stringMaxLength));
	}

	@Override
	public Enum<?> randomEnum(Object[] constValues) {
		return (Enum<?>) RandomUtils.randomMember(constValues, getRandom());
	}

	@Override
	public char randomChar() {
		// The set of visible characters in the ASCII staring from the
		// 'space'
		// (code-point: 32) character to the 'tide'(code-point: 126)
		// character.
		return (char) (randomInt(95) + 32);
	}
	
	@Override
	public boolean randomBoolFromDistribution(double trueProb_, double falseProb_) {
		double falseProb = falseProb_ / (falseProb_ + trueProb_);
		return (getRandom().nextDouble() >= falseProb);
	}

	/* (non-Javadoc)
	 * @see gentest.core.value.generator.IRandomness#weightFlipCoin(double)
	 */
	@Override
	public boolean weighedCoinFlip(double trueProb) {
		return RandomUtils.weighedCoinFlip(trueProb, getRandom());
	}
}
