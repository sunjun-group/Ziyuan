/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.python;

import java.io.OutputStream;
import java.io.PrintWriter;

import sav.strategies.vm.interprocess.ServerInputWriter;

/**
 * @author LLT
 *
 */
public class InputDataWriter extends ServerInputWriter {
	private InputData inputData;
	private PrintWriter pw;
	
	public InputDataWriter() {
		waiting();
	}
	
	@Override
	public void open() {
		inputData = null;
		pw = null;
		waiting();
	}
	
	public void send(InputData input) {
		while(!isWaiting()) {
			// wait for the old data to be written.
		}
		synchronized (state) {
			if (isClosed()) {
				throw new IllegalStateException("InputWriter is closed!");
			}
			this.inputData = input;
			ready(); // ready to write
		}
	}
	
	@Override
	protected void writeData() {
		synchronized (state) {
			if (inputData == null) {
				return;
			}
			inputData.writeData(pw);
			inputData = null;
		}
	}
	
	public void setOutputStream(OutputStream outputStream) {
		this.pw = new PrintWriter(outputStream, true);
	}

	@Override
	public void close() {
		super.close();
		if (pw != null) {
			pw.close();
		}
	}
	
}
