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
 * TODO [LLT]: consider to make cache for services if needed.
 */
public class TzuyuAlgorithmFactory {

	public static Learner getLearner(TzProject project) {
		return lstar(project);
	}

	/**
	 * initialize an instant of Lstar algorithm implementation.
	 */
	private static LStar lstar(TzProject project) {
		LStar lStar = new LStar();
		lStar.setAlphabet(getTzuyuInitAlphabet(project));
		lStar.setTeacher(getTeacherImpl(project));
		return lStar;
	}
	
	public static Teacher getTeacherImpl(TzProject project) {
		TeacherImplV2 teacher = new TeacherImplV2();
		teacher.setInitAlphabet(getTzuyuInitAlphabet(project));
		Refiner refiner = getRefiner();
		refiner.setProject(project); 
		teacher.setRefiner(refiner);
		Tester tester = getTester();
		tester.setProject(project);
		teacher.setTester(tester);
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

	private static TzuYuAlphabet getTzuyuInitAlphabet(TzProject project) {
		return TzuYuAlphabet.constructInitialAlphabet(project);
	}

	public static Teacher getTeacher() {
		return new TeacherImplV2();
	}
	
	
}
