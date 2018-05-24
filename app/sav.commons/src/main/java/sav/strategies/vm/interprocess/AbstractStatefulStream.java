/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm.interprocess;

/**
 * @author LLT
 * a marker stream used to communicate with server.
 */
public class AbstractStatefulStream {
	protected volatile StreamState state = StreamState.CLOSED;
	
	protected void ready() {
		synchronized (this) {
			this.state = StreamState.READY;
		}
	}
	
	protected void waiting() {
		synchronized (this) {
			this.state = StreamState.WAITING;
		}
	}
	
	public void close() {
		synchronized (this) {
			this.state = StreamState.CLOSED;
		}
	}
	
	public boolean isClosed() {
		synchronized (this) {
			return this.state == StreamState.CLOSED;
		}
	}

	public boolean isReady() {
		synchronized (this) {
			return this.state == StreamState.READY;
		}
	}
	
	public boolean isWaiting() {
		synchronized (this) {
			return this.state == StreamState.WAITING;
		}
	}
	
	public void open() {
		synchronized (this) {
			if (this.state == StreamState.CLOSED) {
				this.state = StreamState.WAITING;
			}
		}
	}
}
