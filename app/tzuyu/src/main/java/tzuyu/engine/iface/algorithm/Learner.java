/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface.algorithm;

import org.apache.log4j.Logger;

import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.dfa.DFA;


/**
 * @author LLT
 * 
 */
public interface Learner {
	static final Logger logger = Logger.getRootLogger();
	/**
	 * main function of the algorithm
	 */
	public DFA startLearning();
	
	/**
	 * report all useful output from the process.
	 * this function will  
	 * we put this separate to make the flow clearer.
	 */
	public void report(TzReportHandler reporter);
	
}
