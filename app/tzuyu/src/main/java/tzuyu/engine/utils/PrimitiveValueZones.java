/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;


/**
 * @author LLT
 * 
 */
public abstract class PrimitiveValueZones<T extends Object> {
	public static final IntegerValueZones INTEGER_ZONES;
	public static final LongValueZones LONG_ZONES;
	
	static {
		INTEGER_ZONES = (IntegerValueZones) new IntegerValueZones(4)
							.point(Integer.MIN_VALUE, 1)
							.point(-1000, 100)
							.point(1000, 1)
							.finishPoint(Integer.MAX_VALUE);
		LONG_ZONES = (LongValueZones) new LongValueZones(8)
							.point(Long.MIN_VALUE + 1, 1)
							.point((long) -1e10, 2)
							.point(-1000l, 10)
							.point(0l, 1)
							.point(0l, 10)
							.point(1000l, 2)
							.point((long) 1e10, 1)
							.finishPoint(Long.MAX_VALUE);
	}
	
	public static class LongValueZones extends PrimitiveValueZones<Long> {
		public LongValueZones(int length) {
			super(new Long[length]);
		}

		@Override
		protected Long nextRandom(Long lower, Long upper) {
			return Randomness.nextRandomLong(lower, upper);
		}
	}
	
	public static class IntegerValueZones extends PrimitiveValueZones<Integer> {
		public IntegerValueZones(int length) {
			super(new Integer[length]);
		}

		@Override
		protected Integer nextRandom(Integer lower, Integer upper) {
			return Randomness.nextRandomInt(lower, upper);
		}
	}
	
	protected T[] nPoints;
	protected int[] factors;
	int i = -1;
	
	public PrimitiveValueZones(T[] points) {
		nPoints = points;
		factors = new int[points.length - 1];
	}
	
	private PrimitiveValueZones<T> finishPoint(T val) {
		nPoints[i + 1] = val;
		factors = refreshFactors(factors);
		i = -1;
		return this;
	}

	public static int[] refreshFactors(int... factors) {
		for (int i = 1; i < factors.length; i++) {
			factors[i] += factors[i - 1];
		}
		return factors;
	}
	
	public T nextRandom() {
		int p = randomPoint();
		// this is safe, because we always have: the length of points = length of factors + 1 
		return nextRandom(nPoints[p], nPoints[p + 1]);
	}
	
	protected abstract T nextRandom(T lower, T upper);

	protected int randomPoint() {
		return Randomness.randomRange(factors);
	}

	protected PrimitiveValueZones<T> point(T val, int f) {
		i ++;
		nPoints[i] = val;
		factors[i] = f;
		return this;
	}
	
}
