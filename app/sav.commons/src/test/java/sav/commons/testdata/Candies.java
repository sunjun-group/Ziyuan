/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata;

/**
 * @author khanh
 *
 */
import java.util.Scanner;

public class Candies {
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		
		int N = in.nextInt();
		int[] ratings = ReadArray(in, N);
		
		System.out.println(computeMinCandies(ratings));
	}
	
	public static int[] ReadArray(Scanner in, int size){
		int[] result = new int[size];
		for(int i = 0; i < size; i++){
			result[i] = in.nextInt();
		}
		
		return result;
	}
	public static long computeMinCandies(int[] ratings){
		int length = ratings.length;
		int[] candies = new int[length];
		candies[0] = 1;
		
		int run = 0;
		while(run + 1 < length){
			if(ratings[run] < ratings[run+1]){
				while(run+1 < length && ratings[run] < ratings[run+1]){
					candies[run+1] = candies[run] + 1;
					run++;
				}
			}
			else if(ratings[run] == ratings[run+1]){
				while(run+1 < length && ratings[run] == ratings[run+1]){
					candies[run+1] = 1;
					run++;
				}
			}
			else{
				int count = 0;
				int backupRun = run;
				while(run+1 < length && ratings[run] > ratings[run+1]){
					count++;
					run++;
				}
				candies[backupRun] = Math.max(candies[backupRun], count+1);
				
				for(int i = backupRun+1; i <= run; i++){
					candies[i] = run - i + 1;
				}
			}
		}
		
		//
		long sum = 0;
		for(int num : candies){
			sum += num;
		}
		
		return sum;
	}
}
