/**
 * Copyright TODO
 */
package sav.common.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author LLT
 * for centralization.
 */
public final class Randomness {
	private Randomness() {}

	public static final long SEED = 0;
	public static int totalCallsToRandom = 0;
	static Random random = new Random(SEED);
	
	private static Random getRandom() {
		totalCallsToRandom++;
		return random;
	}

	public static void reset(long newSeed) {
		random = new Random(newSeed);
	}

	public static boolean nextRandomBool() {
		return getRandom().nextBoolean();
	}

	public static float nextFloat() {
		return getRandom().nextFloat();
	}
	
	/**
	 * return random value from 0 to i 
	 */
	public static int nextRandomInt(int i) {
		return getRandom().nextInt(i);
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

	public static <T> List<T> randomSubList(List<T> allList) {
		return randomSubList(allList, nextRandomInt(allList.size()));
	}
	
	public static <T> List<T> randomSubList(T[] allArr) {
		return randomSubList(Arrays.asList(allArr));
	}
	
	public static <T> List<T> randomSubListKeepOder(List<T> allList, int subSize) {
		List<T> sublist = new ArrayList<T>(allList);
		for (int i = 0; i < subSize; i++) {
			sublist.remove(nextRandomInt(sublist.size()));
		}
		return sublist;
	}
	
	public static <T> List<T> randomSequence(T[] allArr, int seqSize) {
		return randomSequenceFixSize(Arrays.asList(allArr), seqSize);
	}
	
	public static <T> List<T> randomSequenceFixSize(List<T> allList, int seqSize) {
		List<T> seq = new ArrayList<T>(seqSize);
		for (int i = 0; i < seqSize; i++) {
			seq.add(allList.get(nextRandomInt(allList.size())));
		}
		return seq;
	}
	
	public static <T> List<T> randomSequence(List<T> allList, int maxSeqSize) {
		return randomSequenceFixSize(allList, nextRandomInt(maxSeqSize + 1));
	}
	
	public static <T> List<T> randomSubList(List<T> allList, int subSize) {
		List<T> sublist = new ArrayList<T>();
		int n = allList.size();
		int[] swaps = new int[allList.size()];
		for (int i = 0; i < subSize; i++) {
			int nextIdx = nextRandomInt(n);
			int realIdx = swaps[nextIdx];
			while(realIdx != 0) {
				nextIdx = realIdx - 1;
				realIdx = swaps[realIdx - 1];
			}
			sublist.add(allList.get(nextIdx));
			swaps[nextIdx] = n;
			n--;
		}
		return sublist;
	}
	
	public static <T> List<T> randomSubList1(List<T> allList, int subSize) {
		List<T> sublist = new ArrayList<T>();
		int n = allList.size();
		for (int i = 0; i < subSize; i++) {
			int nextIdx = nextRandomInt(n);
			T ele = allList.get(nextIdx);
			int eIdx = sublist.indexOf(ele);
			while (eIdx >= 0) {
				ele = allList.get(allList.size() - 1 - eIdx);
				eIdx = sublist.indexOf(ele);
			}
			sublist.add(ele);
			n--;
		}
		return sublist;
	}

	public static boolean randomBoolFromDistribution(double trueProb_, double falseProb_) {
		double falseProb = falseProb_ / (falseProb_ + trueProb_);
		return (Randomness.getRandom().nextDouble() >= falseProb);
	}

}
