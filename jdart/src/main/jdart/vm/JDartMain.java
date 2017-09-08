/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.vm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.model.TestInput;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class JDartMain {

	public static void main(String[] args) throws Exception {
		JDartParameters params = JDartParameters.parse(args);
		runAndSaveResultToFile(params.getJdartParams(), params.getResultFile());
	}

	public static void runAndSaveResultToFile(JDartParams jdartParams, String filepath) throws IOException {
		List<TestInput> result = new JDartCore().run(jdartParams);
		File file = new File(filepath);
		if (!file.exists()) {
			file.createNewFile();
		}
		if (CollectionUtils.isEmpty(result)) {
			return;
		}
		JDartResult.saveToFile(result, file);
		while (true) {
			// do nothing
			System.out.println("abc");
		}
	}
}
