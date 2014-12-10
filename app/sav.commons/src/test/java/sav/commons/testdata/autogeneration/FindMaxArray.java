package sav.commons.testdata.autogeneration;

public class FindMaxArray implements IFindMax{
	private int[] numbers;
	
	public FindMaxArray(int[] num){
		this.numbers = num;
	}
	
	public int Max()
	{
		int result = numbers[0];
		for(int i = 1; i < numbers.length; i++){
			if(result < numbers[i]){
				result = numbers[i];
			}
		}
		
		return result;
	}

	public boolean check(int result){
		
		for(int i = 0; i < numbers.length; i++){
			if(result < numbers[i]){
				return false;
			}
		}
		
		for(int i = 0; i < numbers.length; i++){
			if(result == numbers[i]){
				return true;
			}
		}
		return false;
	}
}
