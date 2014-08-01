package example.MaxFind.test;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import example.MaxFind.MaxFindError;

public class MaxFindFailTest {

	@Test
	public void smfTest0(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest1(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest2(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest3(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest4(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest5(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest6(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest7(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest8(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest10(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest11(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	@Test
	public void smfTest12(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest13(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest14(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest15(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest16(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest17(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest18(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest19(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest20(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest21(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest22(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest23(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest24(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	@Test
	public void smfTest25(){
		ArrayList<Integer> alist = initFailList();
		
		int max = MaxFindError.findArrayMax(alist);
		System.out.println(max);
		checkMax(alist, max);
		
	}
	
	public ArrayList<Integer> initFailList(){
		Random rand = new Random();
		ArrayList<Integer> alist = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++){
			int j = rand.nextInt(1000);
			alist.add(j);
		}
		alist.add(1001);
		return alist;
	}
	
	public ArrayList<Integer> initList(){
		Random rand = new Random();
		ArrayList<Integer> alist = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++){
			int j = rand.nextInt(100);
			alist.add(j);
		}
		return alist;
	}
	
	public static void checkMax(ArrayList<Integer> tList, int max ){
		for(int i : tList){
			assert max >= i : "Not right maximum number";
		}
		
	}
}
