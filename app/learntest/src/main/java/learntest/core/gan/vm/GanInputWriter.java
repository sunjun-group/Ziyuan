/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan.vm;

import java.io.OutputStream;
import java.io.PrintWriter;

import sav.strategies.vm.interprocess.ServerInputWriter;

/**
 * @author LLT
 *
 */
public class GanInputWriter extends ServerInputWriter {
	private IGanInput ganInput;
	private PrintWriter pw;
	
	public GanInputWriter() {
		waiting();
	}
	
	@Override
	public void open() {
		ganInput = null;
		pw = null;
		waiting();
	}
	
	public void request(IGanInput input) {
		if (isClosed()) {
			throw new IllegalStateException("InputWriter is closed!");
		}
		this.ganInput = input;
		ready();
	}
	
	@Override
	protected void writeData() {
		pw.println(String.valueOf(ganInput.getRequestType()));
		ganInput.writeData(pw);
		ganInput = null;
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
	
	public static interface IGanInput {
		
		void writeData(PrintWriter pw);

		RequestType getRequestType();
	}
	
	public static enum RequestType { 
		START_TRAINING_FOR_METHOD,
		TRAIN,
		GENERATE_DATA
	}
}
