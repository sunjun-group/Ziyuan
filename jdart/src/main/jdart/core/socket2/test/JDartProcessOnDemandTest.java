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
import jdart.core.socket2.JDartProcessOnDemand;
import jdart.model.TestInput;

/**
 * @author LLT
 *
 */
public class JDartProcessOnDemandTest extends AbstractJDartTest {
	public static void main(String[] args) {
		JDartProcessOnDemand process = new JDartProcessOnDemand(PathConfiguration.getInstance());
		List<TestInput> result = process.run(defaultOnDemandJDartParams(), "");
		System.out.println(result);
	}
}
