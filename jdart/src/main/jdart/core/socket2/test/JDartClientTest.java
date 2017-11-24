/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core.socket2.test;

import java.io.IOException;

import jdart.core.JDartParams;
import jdart.core.socket2.JDartClient;

/**
 * @author LLT
 *
 */
public class JDartClientTest extends AbstractJDartTest {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 8) {
			String classpathStr, mainEntry, className, methodName, paramString, app, site;
			classpathStr = args[0];
			mainEntry = args[1];
			className = args[2];
			methodName = args[3];
			paramString = args[4];
			app = args[5];
			site = args[6];
			int port = Integer.parseInt(args[7]);
			new JDartClient().run(JDartParams.constructJDartParams(classpathStr, mainEntry, className, methodName,
					paramString, app, site), port);
		} else {
			new JDartClient().run(defaultJDartParams(), 8989);
		}
	}

}
