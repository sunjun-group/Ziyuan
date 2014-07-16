package tzuyu.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sav.common.core.utils.CollectionUtils;
import tzuyu.engine.model.exception.TzRuntimeException;

public final class Randomness {

	private Randomness() {
		throw new IllegalStateException("no instances");
	}

	public static final long SEED = 0;

	/**
	 * The random number used any test time a random choice is made. (Developer
	 * note: do not declare new Random objects; use this one instead).
	 * only get random via the function getRandom();
	 */
	@Deprecated 
	static Random random = new Random(SEED);
	
	public static Random getRandom() {
		totalCallsToRandom++;
		return random;
	}

	public static void reset(long newSeed) {
		random = new Random(newSeed);
	}

	public static int totalCallsToRandom = 0;

	public static boolean nextRandomBool() {
		return getRandom().nextBoolean();
	}

	/**
	 * Uniformly random int from [0, i)
	 */
	public static int nextRandomInt(int i) {
		return getRandom().nextInt(i);
	}
	
	public static int nextRandomInt(int lower, int upper) {
		return nextRandomInt(upper - lower) + lower;
	}
	
	public static long nextRandomLong(long lower, long upper) {
		double r = getRandom().nextDouble();
		return (long) ((r * upper) + ((1.0 - r) * lower) + r);
	}
	
	public static <T> T randomMember(T[] arr) {
		if (CollectionUtils.isEmpty(arr)) {
			return null;
		}
		return arr[nextRandomInt(arr.length)];
	}

	public static <T> T randomMember(List<T> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(nextRandomInt(list.size()));
	}

	public static <T> T randomMember(SimpleList<T> list) {
		if (list == null || list.size() == 0) {
			throw new IllegalArgumentException("Expected non-empty list");
		}
		return list.get(nextRandomInt(list.size()));
	}
	
	public static <T> List<T> randomSubList(List<T> allList, int subSize) {
		ArrayList<T> shuffleList = new ArrayList<T>(allList);
		Collections.shuffle(shuffleList, getRandom());
		return shuffleList.subList(0, subSize);
	}

	// Warning: iterates through the entire list twice (
	// once to compute interval length, once to select element).
	public static <T extends WeightedElement> T randomMemberWeighted(
			SimpleList<T> list) {

		// Find interval length.
		double max = 0;
		for (int i = 0; i < list.size(); i++) {
			double weight = list.get(i).getWeight();
			if (weight <= 0) {
				throw new IllegalArgumentException("weight was " + weight);
			}
			max += weight;
		}
		assert max > 0;

		// Select a random point in interval and find its corresponding element.

		double randomPoint = Randomness.getRandom().nextDouble() * max;
		double currentPoint = 0;
		for (int i = 0; i < list.size(); i++) {
			currentPoint += list.get(i).getWeight();
			if (currentPoint >= randomPoint) {
				return list.get(i);
			}
		}
		throw new IllegalArgumentException("");
	}

	public static <T> T randomSetMember(Collection<T> set) {
		int randIndex = Randomness.nextRandomInt(set.size());
		return CollectionsExt.getNthIteratedElement(set, randIndex);
	}

	public static boolean weighedCoinFlip(double trueProb) {
		if (trueProb < 0 || trueProb > 1) {
			throw new IllegalArgumentException("arg must be between 0 and 1.");
		}
		double falseProb = 1 - trueProb;
		return (Randomness.getRandom().nextDouble() >= falseProb);
	}

	public static boolean randomBoolFromDistribution(double falseProb_,
			double trueProb_) {
		double falseProb = falseProb_ / (falseProb_ + trueProb_);
		return (Randomness.getRandom().nextDouble() >= falseProb);
	}
	
	public static boolean nextBoolean(int denominatorForTrueProbability) {
		int nextInt = nextRandomInt(denominatorForTrueProbability);
		return nextInt == 0;
	}
	
	public static int randomRange(int[] factors) {
		int r = nextRandomInt(factors[factors.length - 1]);
		for (int i = 0; i < factors.length; i++) {
			if (factors[i] > r) {
				return i;
			}
		}
		throw new TzRuntimeException("random Range fail");
	}
}
