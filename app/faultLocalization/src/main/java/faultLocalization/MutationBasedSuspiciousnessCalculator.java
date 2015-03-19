/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocalization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Compute the suspiciousness for each line according to the paper "Ask the Mutants"
 * @author khanh
 *
 */
public class MutationBasedSuspiciousnessCalculator implements SuspiciousCalculator{

	/**
	 * Set of passed test case indexes of the program
	 */
	private Set<Integer> passTests;
	
	/**
	 * Set of failed test case indexed of the program
	 */
	private Set<Integer> failTests;
	
	/**
	 * Set of passed test case indexed covering the line we want to compute the suspiciousness
	 */
	private Set<Integer> passCoverTests;
	
	/**
	 * Set of failed test case indexed covering the line we want to compute the suspiciousness
	 */
	private Set<Integer> failCoverTest;
	
	/**
	 * passTestsInMutants[i] is the set of passed test case indexes after changing this line
	 */
	private List<Set<Integer>> passTestsInMutants;
	
	/**
	 * failTestsInMutants[i] is the set of failed test case indexes after changing this line
	 */
	private List<Set<Integer>> failTestsInMutants;
	
	public MutationBasedSuspiciousnessCalculator(Set<Integer> passTests, Set<Integer> failTests,
			Set<Integer> passCoverTests, Set<Integer> failCoverTest,
			List<Set<Integer>> passTestsInMutants, List<Set<Integer>> failTestsInMutants){
		this.passTests = passTests;
		this.failTests = failTests;
		this.passCoverTests = passCoverTests;
		this.failCoverTest = failCoverTest;
		this.passTestsInMutants = passTestsInMutants;
		this.failTestsInMutants = failTestsInMutants;
	}
	
	public double compute(){
		double alpha = computeAlpha();
		
		double result = 0;
		int numberOfMutants = passTestsInMutants.size();
		for(int mutantIndex = 0; mutantIndex < numberOfMutants; mutantIndex++){
			Set<Integer> failCoverToPassTests = new HashSet<Integer>(failCoverTest);
			failCoverToPassTests.retainAll(passTestsInMutants.get(mutantIndex));
			
			double numberOfFailCoverToPass = failCoverToPassTests.size();
			
			Set<Integer> passCoverToFailTests = new HashSet<Integer>(passCoverTests);
			passCoverToFailTests.retainAll(failTestsInMutants.get(mutantIndex));
			
			double numberOfPassCoverToFail = passCoverToFailTests.size();
			
			result += (numberOfFailCoverToPass / failTests.size()) - alpha * (numberOfPassCoverToFail / passTests.size());
		}
		
		return result / numberOfMutants;
	}
	private double computeAlpha(){
		double numberFailToPass = getNumberOfFailToPass();
		double numberPassToFail = getNumberOfPassToFail();
		
		int numberOfMutants = passTestsInMutants.size();
		
		return (numberFailToPass / (numberOfMutants * failTests.size())) *
				(numberOfMutants * passTests.size()) / numberPassToFail;
	}
	private int getNumberOfFailToPass(){
		int count = 0;
		for(Set<Integer> passTestsInMutant: passTestsInMutants){
			for(int failTest: failTests){
				if(passTestsInMutant.contains(failTest)){
					count++;
				}
			}
		}
		
		return count;
	}
	
	private int getNumberOfPassToFail(){
		int count = 0;
		for(Set<Integer> failTestsInMutant: failTestsInMutants){
			for(int passTest: passTests){
				if(failTestsInMutant.contains(passTest)){
					count++;
				}
			}
		}
		
		return count;
	}
	
	public double getSuspiciousness(){
		return 0;
	}
}
