/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import tester.ITCGStrategy;
import tzuyu.engine.algorithm.iface.Refiner;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.algorithm.iface.Tester;
import tzuyu.engine.model.dfa.Alphabet;

/**
 * @author LLT
 *
 */
public interface ITzManager<A extends Alphabet> {

	Tester getTester();

	Refiner<A> getRefiner();

	Teacher<A> getTeacher();

	ITCGStrategy getTCGStrategy();

	IReferencesAnalyzer getRefAnalyzer();

	IPrintStream getOutStream();
	
	ILogger<?> getLogger();

	void checkProgress() throws InterruptedException;
}
