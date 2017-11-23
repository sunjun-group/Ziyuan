package learntest.core.gentest.generator;

import java.util.Arrays;

import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.generator.ArrayRandomness;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.PrimitiveUtils;
import sav.common.core.utils.Randomness;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ArrayValue.ArrValueElement;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.IntegerValue;
import sav.strategies.dto.execute.value.ReferenceValue;

public class FixLengthArrayValueGenerator {
	
	public GeneratedVariable generate(IType type, int firstVarId, int[] arrayLength) {
		IType contentType = type;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}
		GeneratedVariable variable = new GeneratedVariable(firstVarId);
		RArrayConstructor arrayConstructor = new RArrayConstructor(arrayLength, type.getRawType(),
				contentType.getRawType());
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		return variable;
	}
	
	public static int[] customizeArrayAndDetermineLength(ArrayValue value, Class<?> contentType, int dimension) {
		int[] lengths = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			lengths[i] = -1;
		}
		getArrayLength(value, lengths, 0);
		/*
		 * check lengths of all dimension, if it's not specified, assign a reasonable value for it.
		 */
		int maxEles = calculateMaxEles(lengths);
		for (int i = 0; i < dimension; i++) {
			if (lengths[i] == -1) {
				if (i == (dimension - 1)) {
					lengths[i] = randomLastDimensionLength(contentType, maxEles);
				} else {
					lengths[i] = 1;
				}
			}
		}
		if (value.getLengthValue() != null && CollectionUtils.isEmpty(value.getElements())) {
			int limits[] = narrowDownArrayLimit(lengths);
			/*
			 * Check the last dimension, if there is no any data, try to fakely
			 * assign some for randomly generating purpose.
			 */
			int[] location = ArrayRandomness.next(null, limits);
			while (location != null) {
				LocationHolderExecValue fakeEle = new LocationHolderExecValue(getArrayElementId(value, location));
				value.add(fakeEle);
				location = ArrayRandomness.next(location, limits);
			}
		}
		return lengths;
	}
	
	public static int randomLastDimensionLength(Class<?> contentType, int curMaxEles) {
		boolean isSimpleType = PrimitiveUtils.isSimpleType(contentType.getName());
		int maxLength = 2;
		if (isSimpleType) {
			maxLength = 10;
		}
		if (curMaxEles > 0) {
			maxLength = maxLength / curMaxEles;
			if (maxLength < 1) {
				maxLength = 1;
			}
		}
		return Randomness.nextInt(maxLength);
	}
	
	private static String getArrayElementId(ArrayValue value, int[] location) {
		StringBuilder sb = new StringBuilder(value.getVarId());
		for (int i = 0; i < location.length; i++) {
			sb.append("[").append(location[i]).append("]");
		}
		return sb.toString();
	}

	private static int[] narrowDownArrayLimit(int[] lengths) {
		int maxEles = calculateMaxEles(lengths);
		if (maxEles < 10) {
			return Arrays.copyOf(lengths, lengths.length);
		}
		int[] maxLimits = new int[lengths.length];
		int total = 1;
		int maxTotal = Randomness.nextInt(5, 10);
		int i = lengths.length - 1;
		boolean stop = false;
		for (; i >= 0 && !stop; i--) {
			total *= lengths[i];
			if (total <= maxTotal) {
				maxLimits[i] = lengths[i];
			} else {
				maxLimits[i] = (maxTotal * lengths[i]) / total;
				stop = true;
			}
		}
		for (; i >= 0; i--) {
			maxLimits[i] = Math.min(lengths[i], 1);
		}
		return maxLimits;
	}

	/**
	 * for length[i] < 0: excluded!.
	 * */
	private static int calculateMaxEles(int[] lengths) {
		if (lengths == null || (lengths.length == 0)) {
			return 0;
		}
		int total = 1;
		for (int i = 0; i < lengths.length; i++) {
			if (lengths[i] >= 0) {
				total *= lengths[i];
			}
		}
		return total;
	}

	private static void getArrayLength(ArrayValue value, int[] lengths, int idx) {
		int length = -1;
		IntegerValue lengthValue = value.getLengthValue();
		if (lengthValue != null) {
			length = lengthValue.getIntegerVal();
		}
		for (ArrValueElement element : CollectionUtils.nullToEmpty(value.getElements())) {
			int minLength = element.getIdx() + 1;
			if (length < minLength) {
				length = minLength;
			}
			if (element.getValue().getType() == ExecVarType.ARRAY) {
				getArrayLength((ArrayValue) element.getValue(), lengths, idx);
			}
		}
		lengths[idx] = Math.max(lengths[idx], length); 
	}
	
	public static class LocationHolderExecValue extends ReferenceValue {

		public LocationHolderExecValue(String id) {
			super(id, false);
		}
		
	}
}
