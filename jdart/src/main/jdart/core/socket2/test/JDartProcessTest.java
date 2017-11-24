/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core.socket2.test;

import java.util.List;

import config.PathConfiguration;
import jdart.core.JDartParams;
import jdart.core.socket2.JDartProcess;
import jdart.model.TestInput;

/**
 * @author LLT
 *
 */
public class JDartProcessTest extends AbstractJDartTest {

	public static void main(String[] args) {
		JDartProcess process = new JDartProcess(PathConfiguration.getInstance());
		List<TestInput> result = process.run(defaultJDartParams());		
		System.out.println(result);
		System.out.println("solve count : " + process.getSolveCount());
	}
}
