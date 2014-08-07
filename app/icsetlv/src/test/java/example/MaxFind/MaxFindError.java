package example.MaxFind;

import java.util.ArrayList;

public class MaxFindError {

	public static int findArrayMax(ArrayList<Integer> tlist) {
		int max = tlist.get(0);
		for (int i = 1; i < tlist.size() - 1; i++) {
			if (max < tlist.get(i)) {
				max = tlist.get(i);
			}
		}
		return max;
	}
	
	public static boolean checkMax(ArrayList<Integer> tList, int max ){
		for(int i : tList){
			assert max >= i : "Not right maximum number";
			return true;
		}
		return false;
	}
	
	public static int addup(int a , int b){
		return a + b - 1 ;
	}
	
//	public static int findSecondMax(ArrayList<Integer> tlist){
//		int max1 = findArrayMax(tlist);
//		tlist.remove(findIndex(tlist, max1));
//		int max2 =findArrayMax(tlist);
//		assert max2 < max1;
//		return max2;
//	}
//	
	
	/*
	 * pre-condition: elements in tlist is not equal to each other
	 * 
	 * */
	public static int findIndex(ArrayList<Integer> tlist, int keyvalue){
		for(int i = 0; i < tlist.size(); i++){
			if(tlist.get(i) == keyvalue){
				return i;
			}
		}
		return 0;
	}
}
