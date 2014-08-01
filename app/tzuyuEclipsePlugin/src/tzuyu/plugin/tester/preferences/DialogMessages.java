/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.DialogPage;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class DialogMessages {
	private DialogPage page;
	private Map<Integer, List<String>> msgs = new HashMap<Integer, List<String>>();
	
	public DialogMessages(DialogPage page) {
		this.page = page;
	}
	
	public void addMsg(String msg, int type) {
		List<String> msgsByType = getMsgs(type);
		if (!msgsByType.contains(msg)) {
			msgsByType.add(msg);
		}
	}
	
	public void removeMsg(String msg, int type) {
		getMsgs(type).remove(msg);
	}
	
	private List<String> getMsgs(int type) {
		List<String> msgsByType = msgs.get(type);
		if (msgsByType == null) {
			msgsByType = new ArrayList<String>();
			msgs.put(type, msgsByType);
		}
		return msgsByType;
	}
	
	public void display() {
		for(Entry<Integer, List<String>> entry : msgs.entrySet()) {
			page.setMessage(StringUtils.newLineJoin(entry.getValue()), entry.getKey());
		}
	}
}
