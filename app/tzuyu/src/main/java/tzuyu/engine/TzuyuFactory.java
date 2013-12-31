/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import lstar.LStar;
import lstar.Teacher;
import refiner.TzuYuRefinerV2;
import tester.TzuYuTesterV2;
import tzuyu.engine.iface.algorithm.Learner;
import tzuyu.engine.iface.algorithm.Refiner;
import tzuyu.engine.iface.algorithm.Tester;
import tzuyu.engine.lstar.TeacherImplV2;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT
 * TODO [LLT]: consider to make cache for services if needed.
 */
public class TzuyuFactory {

	public static Learner<TzuYuAlphabet> getLearner() {
		return new LStar<TzuYuAlphabet>();
	}
	
	public static Teacher<TzuYuAlphabet> getTeacher() {
		return new TeacherImplV2();
	}
	
	public static Tester getTester() {
		return new TzuYuTesterV2();
	}
	
	public static Refiner<TzuYuAlphabet> getRefiner() {
		return new TzuYuRefinerV2();
	}
	
}
