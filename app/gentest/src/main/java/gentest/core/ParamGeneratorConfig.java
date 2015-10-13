/**
 * Copyright TODO
 */
package gentest.core;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 */
public class ParamGeneratorConfig {
	public static final int DEFAULT_STRING_MAX_LENGTH = 10;
	public static final Ranges<Integer> DEFAULT_INT_RANGES = Ranges.of(0, 1);
	public static final Ranges<Long> DEFAULT_LONG_RANGES = Ranges.of(-1000l, 1000l);
	public static final Ranges<Float> DEFAULT_FLOAT_RANGES = Ranges.of(-1000f, 1000f);
	public static final Ranges<Double> DEFAULT_DOUBLE_RANGES = Ranges.of(-1000d, 1000d);
	
	private Ranges<Integer> intRanges;
	private Ranges<Long> longRanges;
	private Ranges<Float> floatRanges;
	private Ranges<Double> doubleRanges;
	private int stringMaxLength;

	public static ParamGeneratorConfig getDefault() {
		ParamGeneratorConfig config = new ParamGeneratorConfig();
		config.setStringMaxLength(DEFAULT_STRING_MAX_LENGTH);
		config.setIntRanges(DEFAULT_INT_RANGES);
		config.setLongRanges(DEFAULT_LONG_RANGES);
		config.setFloatRanges(DEFAULT_FLOAT_RANGES);
		config.setDoubleRanges(DEFAULT_DOUBLE_RANGES);
		return config;
	}

	public int getStringMaxLength() {
		return stringMaxLength;
	}

	public void setStringMaxLength(int stringMaxLength) {
		this.stringMaxLength = stringMaxLength;
	}

	public Ranges<Integer> getIntRanges() {
		return intRanges;
	}

	public void setIntRanges(Ranges<Integer> intRanges) {
		this.intRanges = intRanges;
	}

	public Ranges<Long> getLongRanges() {
		return longRanges;
	}

	public void setLongRanges(Ranges<Long> longRanges) {
		this.longRanges = longRanges;
	}

	public Ranges<Float> getFloatRanges() {
		return floatRanges;
	}

	public void setFloatRanges(Ranges<Float> floatRanges) {
		this.floatRanges = floatRanges;
	}

	public Ranges<Double> getDoubleRanges() {
		return doubleRanges;
	}

	public void setDoubleRanges(Ranges<Double> doubleRanges) {
		this.doubleRanges = doubleRanges;
	}

	public static class Ranges<E> {
		private List<Pair<E, E>> ranges = new ArrayList<Pair<E, E>>();

		public void add(E from, E to) {
			ranges.add(Pair.of(from, to));
		}

		public static <E> Ranges<E> of(E from, E to) {
			Ranges<E> ranges = new Ranges<E>();
			ranges.add(from, to);
			return ranges;
		}

		public Pair<E, E> randomRange() {
			return Randomness.randomMember(ranges);
		}
	}
}
