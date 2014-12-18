package sav.commons.testdata.autogeneration;

import java.util.Set;

public class FindMaxSet implements IFindMax {
	private Set<Integer> numbers;

	public FindMaxSet(Set<Integer> num) {
		this.numbers = num;
	}

	public int Max() {
		int result = Integer.MIN_VALUE;
		for (Integer num : numbers) {
			if (result < num) {
				result = num;
			}
		}

		return result;
	}

	public boolean check(int result) {
		for (Integer num : numbers) {
			if (result < num) {
				return false;
			}
		}

		for (Integer num : numbers) {
			if (result == num) {
				return true;
			}
		}
		
		
		return false;
	}
}
