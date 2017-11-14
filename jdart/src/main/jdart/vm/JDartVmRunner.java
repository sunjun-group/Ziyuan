/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartParams;
import jdart.model.TestInput;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class JDartVmRunner extends VMRunner {
	private static final Logger log = LoggerFactory.getLogger(JDartVmRunner.class);
	private static final String JDART_JAR = "/jdart.rt.jar";
	private static File JDART_FILE;

	public static List<TestInput> run(JDartParams jdartParams, String javaHome) throws SavException {
		try {
			JDartParameters paramters = new JDartParameters();
			paramters.setJdartParams(jdartParams);
			// result file
			File resultFile;
			resultFile = File.createTempFile("jdartResult", ".txt");
			paramters.setResultFile(resultFile.getAbsolutePath());

			VMConfiguration config = new VMConfiguration();
			config.setJavaHome(javaHome);
			config.addClasspath(extractToTemp().getAbsolutePath());
			config.setProgramArgs(JDartParameters.buildVmProgramArgument(paramters));
			config.setLaunchClass(JDartMain.class.getName());
			startVmRunner(config, jdartParams.getTimeLimit());
			return JDartResult.readFrom(resultFile);
		} catch (Exception e) {
			log.debug(e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}

	private static void startVmRunner(VMConfiguration config, long timeout) throws SavException {
		JDartVmRunner vmRunner = new JDartVmRunner();
		vmRunner.setTimeout(timeout);
		vmRunner.startAndWaitUntilStop(config);
	}
	
	@Override
	protected void buildVmOption(CollectionBuilder<String, ?> builder, VMConfiguration config) {
		super.buildVmOption(builder, config);
		// TODO LLT: fix hardcode!
		builder.append("-Xms1024m")
			.append("-Xmx9000m");
		
	}

	public static File extractToTemp() throws SavRtException {
		try {
			if (JDART_FILE == null) {
				File jar;
				jar = File.createTempFile("jdart.rt", ".jar");
				jar.deleteOnExit();
				extractTo(jar);
				JDART_FILE = jar;
			}
		} catch (Exception e) {
			throw new SavRtException(e);
		}
		return JDART_FILE;
	}

	public static void extractTo(File destFile) throws FileNotFoundException, IOException {
		InputStream inStream = JDartVmRunner.class.getResourceAsStream(JDART_JAR);
		IOUtils.copy(inStream, new FileOutputStream(destFile));
	}
}
