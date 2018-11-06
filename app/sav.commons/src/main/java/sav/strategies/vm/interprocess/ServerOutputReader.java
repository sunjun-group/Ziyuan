/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm.interprocess;

import java.io.BufferedReader;

/**
 * @author LLT
 *
 */
public abstract class ServerOutputReader extends AbstractStatefulStream {

	public abstract boolean isMatchCommand(String line);

	public synchronized void read(BufferedReader br) {
		synchronized (this) {
			waiting();
			readData(br);
		}
	}

	protected abstract void readData(BufferedReader br);
}
