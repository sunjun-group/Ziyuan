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
import tzuyu.engine.instrument.TzuYuInstrumentor;
import tzuyu.engine.lstar.TeacherImplV2;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT
 *
 */
public class TzuyuAlgorithmFactory {

	public static Learner getLearner() {
		return lstar();
	}

	/**
	 * initialize an instant of Lstar algorithm implementation.
	 */
	private static LStar lstar() {
		LStar lStar = new LStar(getTeacherImpl());
		lStar.setAlphabet(getTzuyuInitAlphabet());
		
		return lStar;
	}
	
	private static Teacher getTeacherImpl() {
		TeacherImplV2 teacher = new TeacherImplV2();
		teacher.setInitAlphabet(getTzuyuInitAlphabet());
		teacher.setRefiner(getRefiner());
		teacher.setTester(getTester());
		return teacher;
	}

	/**
	 * for configuration.
	 * TODO-LLT: consider to support configuration in more flexible way. 
	 */
	private static Tester getTester() {
		return new TzuYuTesterV2(new TzuYuInstrumentor());
	}

	private static Refiner getRefiner() {
		return new TzuYuRefinerV2();
	}

	private static TzuYuAlphabet getTzuyuInitAlphabet() {
		return TzuYuAlphabet.constructInitialAlphabet();
	}
	
	
}
