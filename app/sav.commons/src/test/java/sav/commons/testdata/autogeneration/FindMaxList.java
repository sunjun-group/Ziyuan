package sav.commons.testdata.autogeneration;

import java.util.List;

public class FindMaxList implements IFindMax {
	private List<Integer> numbers;

	public FindMaxList(List<Integer> num) {
		this.numbers = num;
	}

	public int Max() {
		int result = Integer.MIN_VALUE;
		for (int i = 0; i < numbers.size(); i++) {
			if (result < numbers.get(i)) {
				result = numbers.get(i);
			}
		}

		return result;
	}

	public boolean check(int result) {

		for (int i = 0; i < numbers.size(); i++) {
			if (result < numbers.get(i)) {
				return false;
			}
		}

		for (int i = 0; i < numbers.size(); i++) {
			if (result == numbers.get(i)) {
				return true;
			}
		}
		
		
		return false;
	}
}
