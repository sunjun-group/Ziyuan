package gentest;

public class FindElement {
	public static int getMaxValue(int[] numbers) {
		int maxValue = numbers[0];
		for (int i = 1; i < numbers.length; i++)
			if (maxValue < numbers[i])
				maxValue = numbers[i];
		return maxValue;
	}

	public static int getMinValue(int[] numbers) {
		int minValue = numbers[0];
		for (int i = 1; i < numbers.length; i++)
			if (minValue > numbers[i])
				minValue = numbers[i];
		return minValue;
	}

}
