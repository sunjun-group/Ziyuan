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
import jdart.core.socket2.JDartOnDemandClient;

/**
 * @author LLT
 *
 */
public class JDartOnDemainClientTest extends AbstractJDartTest {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 11) {
			String classpathStr, mainEntry, className, methodName, paramString, app, site, onDemandSite;
			int idx = 0;
			classpathStr = args[idx++];
			mainEntry = args[idx++];
			className = args[idx++];
			methodName = args[idx++];
			paramString = args[idx++];
			app = args[idx++];
			site = args[idx++];
			onDemandSite = args[idx++];
			int node = Integer.parseInt(args[idx++]);
			int branch = Integer.parseInt(args[idx++]);
			String jdartInitTc = args[idx++];
			int port = Integer.parseInt(args[idx++]);
			new JDartOnDemandClient().run(JDartParams.constructOnDemandJDartParams(classpathStr, mainEntry, className,
					methodName, paramString, app, site, onDemandSite, node, branch), port, jdartInitTc);
		} else {
			new JDartOnDemandClient().run(defaultOnDemandJDartParams(), 8989, "");
		}
	}
}
