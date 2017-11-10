/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm.interprocess.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.strategies.vm.interprocess.ServerInputWriter;
import sav.strategies.vm.interprocess.ServerOutputReader;

/**
 * @author LLT
 *
 */
public abstract class AbstractSocketClient {
	private static Logger log = LoggerFactory.getLogger(AbstractSocketClient.class);
	private Socket client;
	private ServerInputWriter inputWriter;
	private ServerOutputReader outputReader;
	
	public AbstractSocketClient(ServerInputWriter inputWriter, ServerOutputReader outputReader) {
		this.inputWriter = inputWriter;
		this.outputReader = outputReader;
	}
	
	public void stop() {
		try {
			client.close();
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		inputWriter.close();
		outputReader.close();
	}
	
	public void connect(int port) throws SavException {
		while (true) {
			try {
				client = new Socket("localhost", port);
				// TODO: set timeout!
				break;
			} catch (UnknownHostException e) {
				throw new SavException(e, ModuleEnum.JVM);
			} catch (IOException e) {
				throw new SavException(e, ModuleEnum.JVM);
			}
		}
		try {
			inputWriter.setOutputStream(client.getOutputStream());
			setuptInputStream(client.getInputStream());
		} catch (IOException e) {
			throw new SavException(e, ModuleEnum.JVM);
		}
	}

	private void setuptInputStream(final InputStream is) {
		new Thread(new Runnable() {
			public void run() {
				final InputStreamReader streamReader = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(streamReader);
				try {
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							if (!outputReader.isClosed() && outputReader.isMatched(line)) {
								outputReader.read(br);
							}
						}
					} catch (IOException e) {
						log.warn(e.getMessage());
					}
				} finally {
					IOUtils.closeQuietly(streamReader);
					IOUtils.closeQuietly(br);
					IOUtils.closeQuietly(is);
				}
				
			}
		}).start();		
	}
	
}
