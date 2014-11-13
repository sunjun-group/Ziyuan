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
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Median {
	private static DecimalFormat df = new DecimalFormat("###.#");
	
	/*
	 * has the same or one more than maxHeap
	 */
	private static PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>();
	private static PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(1, new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
        	return o2.compareTo(o1);
        }
    });
	private static int count = 0;
	
	public static void main(String[] args){

		Scanner in = new Scanner(System.in);
		
		int K = in.nextInt(); in.nextLine();
		
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < K; i++){
			String action = in.next();
			int value = in.nextInt();
			if(action.equals("r")){
				boolean result = remove(value);
				if(result){
					output.append(getMedian());
					output.append("\n");
				}
				else{
					output.append("Wrong!\n");
				}
			}
			else{
				add(value);;
				output.append(getMedian());
				output.append("\n");
			}
			if(i < K-1){
				in.nextLine();
			}
		}
		
		System.out.println(output.toString());
		
	}
	
	public static String getMedian(){
		if(count == 0){
			return "Wrong!";
		}
		else if(count % 2 == 0){
			double minValue = minHeap.peek();
			double maxValue = maxHeap.peek();
			double median = (minValue + maxValue) / 2;
			return df.format(median);
		}
		else{
			return String.valueOf(minHeap.peek());
		}
	}
	
	public static void add(int value){
		if(count % 2 == 0){
			if(minHeap.isEmpty()){
				minHeap.add(value);
			}
			else{
				int maxValue = maxHeap.peek();
				if(value < maxValue){
					maxHeap.poll();
					maxHeap.add(value);
					minHeap.add(maxValue);
				}
				else{
					minHeap.add(value);
				}
				
			}
		}
		else{
			int minValue = minHeap.peek();
			if(value > minValue){
				minHeap.poll();
				minHeap.add(value);
				maxHeap.add(minValue);
			}
			else{
				maxHeap.add(value);
			}
		}
		
		count++;
	}
	
	public static boolean remove(int value){
		boolean removeMin = false, removeMax = false;
		
		if(count == 0){
			return false;
		}
		else if(count == 1){
			removeMin = minHeap.remove(value);
			if(removeMin){
				count--;
			}
			return removeMin;
		}
		
		int maxValue = maxHeap.peek();
		int minValue = minHeap.peek();
		if(value <= maxValue){
			removeMax = maxHeap.remove(value);
		}
		else if(value >= minValue){
			removeMin = minHeap.remove(value);
		}
		else{
			return false;
		}
		
		if(removeMin || removeMax){
			int minCount = minHeap.size();
			int maxCount = maxHeap.size();
			
			if(minCount - maxCount == -1){
				maxValue = maxHeap.poll();
				minHeap.add(maxValue);
			}
			else if(minCount - maxCount == 2){
				minValue = minHeap.poll();
				maxHeap.add(minValue);
			}
			
			count--;
			return true;
		}
		else{
			return false;
		}
	}
}