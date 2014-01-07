/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface.algorithm;

import lstar.LStarException;
import lstar.Teacher;

import org.apache.log4j.Logger;

import tzuyu.engine.iface.HasReport;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.dfa.DFA;

/**
 * @author LLT
 * The learner should not know anything about the alphabet instant.
 */
public interface Learner<A extends Alphabet> extends HasReport<A> {
	static final Logger logger = Logger.getRootLogger();

	public void setAlphabet(A sig);
	
	/**
	 * main function of the algorithm
	 */
	public DFA startLearning() throws LStarException;
	
	public void setTeacher(Teacher<A> teacher);
}
