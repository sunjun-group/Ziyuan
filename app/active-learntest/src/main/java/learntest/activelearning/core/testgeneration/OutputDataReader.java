/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.testgeneration;

import java.io.BufferedReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.utils.SingleTimer;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.vm.interprocess.ServerOutputReader;
import sav.strategies.vm.interprocess.python.PythonVmRunner;

/**
 * @author LLT
 *
 */
public class OutputDataReader extends ServerOutputReader {
	private static Logger log = LoggerFactory.getLogger(OutputDataReader.class);
	private volatile Message readOutput;
	private RequestType requestType;

	public OutputDataReader() {
		waiting();
	}

	public boolean isMatched(String line) {
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
		switch (requestType) {
		case $BOUNDARY_REMAINING:
			readOutput = Message.boundaryRemainingOuput(br);
			ready();
			break;
		case $REQUEST_LABEL:
			readOutput = Message.parseUnlabeledDataPoints(br);
			ready();
			break;
		case $TRAINING_FINISH:
			readOutput = Message.parseTrainingFinish(br);
			ready();
			break;
		case $BOUNDARY_EXPLORATION:
			readOutput = Message.parseBoundaryExplorationPoints(br);
			ready();
			break;
		default:
			break;
		}
	}

	@Override
	public void open() {
		readOutput = null;
		waiting();
	}

	public Message readOutput(long timeout, PythonVmRunner vmRunner) {
		
		if(!vmRunner.getProcess().isAlive()){
			return null;
		}
		
		System.currentTimeMillis();
		
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
				checkState();
				if(!vmRunner.getProcess().isAlive()){
					return null;
				}
			}
		}
		Message result = readOutput;
		readOutput = null;
		waiting();
		return result;
	}

	private void checkState() {
		if (isClosed()) {
			throw new IllegalStateException("OutputReader is already closed!");
		}
	}

	private void waitingWithTimeout(SingleTimer timer, long timeout) throws SAVExecutionTimeOutException {
		while (isWaiting()) {
			checkState();
			if (timer.getExecutionTime() > timeout) {
				System.out.println("timeout!");
				throw new SAVExecutionTimeOutException();
			}
		}
	}
}
