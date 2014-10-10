/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation.sample;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class SimplePrograms {
	

	/**
	 * Check whether a number is a palindrome
	 * 121 is a palindrome but 123 is not
	 * convert to list of digits and check
	 * @param number
	 * @return
	 */
	public boolean isPalindrome1(int number){
		List<Integer> digits = new ArrayList<Integer>();
		
		while(number > 0){
			int digit = number % 10;
			digits.add(digit);
			number = number / 10;
		}
		
		for(int i = 0; i < digits.size()/ 2; i++){
			if(digits.get(i) != digits.get(digits.size() - 1 - i)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Check whether a number is a palindrome
	 * 121 is a palindrome but 123 is not
	 * build the reverse number and check they are the same
	 * @param number
	 * @return
	 */
	public boolean isPalindrome2(int number){
		int reverseNumber = 0;
		
		while(number > 0){
			int digit = number % 10;
			reverseNumber = reverseNumber * 10 + digit;
			number = number / 10;
		}
		
		return (reverseNumber == number);
	}
	
	/**
	 * array length n of numbers from 0-n-1
	 * return number which appears more than once
	 * @param numbers
	 * @return
	 */
	public int duplicatedNumber(int[] numbers){
		
		for(int i = 0; i < numbers.length; i++){
			while(numbers[i] != i){
				int temp = numbers[numbers[i]];
				
				if(numbers[i] == temp){
					return numbers[i];
				}
				
				numbers[numbers[i]] = numbers[i];
				numbers[i] = temp;
				
			}
		}
		
		return -1;
	}
	
	/**
	 * the matrix is sorted as 
	 * 1 3 5
		7 9 11
		13 15 17
		left to right, and the last number in each
		row is not greater than the first number of the next row
	 * @param matrix
	 * @param number
	 * @return
	 */
	public boolean searchInSortingMatrix1(int[][] matrix, int number){
		int row = matrix.length;
		int col = matrix[0].length;
		
		int start = 0;
		int end = row * col - 1;
		
		while(start <= end){
			int middle = start + (end - start) / 2;
			
			int c = middle % col;
			int r = middle / col;
			
			if(matrix[r][c] == number){
				return true;
			}
			else if(matrix[r][c] > number){
				end = middle - 1;
			}
			else{
				start = middle + 1;
			}
			
		}
		
		
		return false;
	}
}
