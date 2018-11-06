/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan.vm;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.gan.vm.GanInputWriter.RequestType;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.StringUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.vm.interprocess.ServerOutputReader;

/**
 * @author LLT
 *
 */
public class GanOutputReader extends ServerOutputReader {
	private static Logger log = LoggerFactory.getLogger(GanOutputReader.class);
	private volatile GanOutput ganOutput;
	private RequestType requestType;

	public GanOutputReader() {
		waiting();
	}

	public boolean isMatchCommand(String line) {
		try {
			log.debug("read data: {}", line);
			requestType = RequestType.valueOf(line);
			return true;
		} catch (Exception ex) {
			// do nothing
		}
		return false;
	}

	@Override
	protected void readData(BufferedReader br) {
		try {
			switch (requestType) {
			case GENERATE_DATA:
				readGenerateRequestOutput(br);
				ready();
				break;
			default:
				break;
			}
		} catch (IOException e) {
			log.debug("Error when reading output: {}", e.getMessage());
		}
	}

	private void readGenerateRequestOutput(BufferedReader br) throws IOException {
		readWithJsonFormat(br);
	}

	private void readWithJsonFormat(BufferedReader br) throws IOException {
		String line = br.readLine();
		if (StringUtils.isEmpty(line)) {
			line = br.readLine();
		}
		log.debug(line);
		ganOutput = new GanOutput(requestType);
		try {
			ganOutput.parseJson(line);
		} catch(Throwable e) {
			log.debug(e.getMessage());
		}
	}
	
	@Override
	public void open() {
		ganOutput = null;
		waiting();
	}
	
	public GanOutput readOutput() {
		return readOutput(-1);
	}

	public GanOutput readOutput(long timeout) {
		SingleTimer timer = SingleTimer.start("read output");
		if (timeout > 0) {
			try {
				waitingWithTimeout(timer, timeout);
			} catch (SAVExecutionTimeOutException e) {
				log.debug("Timeout!");
				return null;
			}
		} else {
			while (isWaiting()) {
				// do nothing
			}
		}
		GanOutput output = ganOutput;
		ganOutput = null;
		waiting();
		return output;
	}

	private void waitingWithTimeout(SingleTimer timer, long timeout) throws SAVExecutionTimeOutException {
		while (isWaiting()) {
			if (timer.getExecutionTime() > timeout) {
				System.out.println("timeout!");
				throw new SAVExecutionTimeOutException();
			}
		}
	}
}
