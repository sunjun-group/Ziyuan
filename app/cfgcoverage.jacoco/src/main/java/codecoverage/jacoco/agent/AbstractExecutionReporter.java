/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public abstract class AbstractExecutionReporter implements IExecutionReporter {
	protected Logger log = LoggerFactory.getLogger(AbstractExecutionReporter.class);
	
	/* target folder of testing project */
	protected List<String> targetFolders;
	protected List<String> testMethods;
	
	public AbstractExecutionReporter(String[] targetFolders) {
		reset(targetFolders);
	}

	protected void reset(String[] targetFolders) {
		this.targetFolders = new ArrayList<String>();
		CollectionUtils.addIfNotNullNotExist(this.targetFolders, targetFolders);
	}
	
	public void setTestcases(List<String> testMethods) {
		this.testMethods = testMethods;
	}
	
	protected InputStream getTargetClass(String className) throws IOException {
		if (!targetFolders.isEmpty()) {
			for (String target : targetFolders) {
				File file = new File(ClassUtils.getClassFilePath(target, className));
				if (file.exists()) {
					return new FileInputStream(file);
				}
			}
		} else {
			String resource = JaCoCoUtils.getClassResourceStr(className);
			return getClass().getResourceAsStream(resource);
		}
		throw new SavRtException("Cannot find .class file for class " + className);
	}
	
	protected Map<String, List<ExecutionData>> read(final String file)
			throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("read exec file ", file);
		}
		final ExecutionDataIntStore dataStore = new ExecutionDataIntStore();
		FileInputStream in = new FileInputStream(file);
		ExecutionDataReader reader = new ExecutionDataReader(in);
		reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
			public void visitSessionInfo(SessionInfo info) {
				dataStore.newSession(info.getId());
			}
		});
		reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
			public void visitClassExecution(ExecutionData data) {
				dataStore.curData.add(data);
			}
		});
		reader.read();
		in.close();
		return dataStore.execDataMap;
	}
	
	private static class ExecutionDataIntStore {
		List<ExecutionData> curData;
		Map<String, List<ExecutionData>> execDataMap;
		
		public ExecutionDataIntStore() {
			execDataMap = new LinkedHashMap<String, List<ExecutionData>>();
		}
		
		public void newSession(String sessionId) {
			curData = new ArrayList<ExecutionData>();
			execDataMap.put(sessionId, curData);
		}
	}
}
