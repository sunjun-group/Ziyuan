/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.constants;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 *
 */
public final class Messages {
	private static final String MESSAGES_PATH = "tzuyu.plugin.messages";
	private static final String ENUM_CONSTANTS_PATH = "tzuyu.plugin.enumConstants";
	private ResourceBundle messagesResourceBundle;
	private ResourceBundle enumConstantResourceBundle;

	public Messages() {
		try {
			messagesResourceBundle = ResourceBundle.getBundle(MESSAGES_PATH);
			enumConstantResourceBundle = ResourceBundle.getBundle(ENUM_CONSTANTS_PATH);
		} catch (MissingResourceException ex) {
			PluginLogger.logEx(ex);
		}
	}
	
	public String getMessage(Enum<?> val) {
		String key = StringUtils.dotJoinStr(
				val.getClass().getSimpleName(), val.name());
		return enumConstantResourceBundle.getString(key);
	}
	
	private String getMessage(String key) {
		return messagesResourceBundle.getString(key); 
	}
	
	/* see GenerateMessagesClass in Tzuyu.tools for generation detail*/
	//	Generated part

	public String gentest_prefs_output_testcaseType_pass() {
		return getMessage("gentest.prefs.output.testcaseType.pass");
	}

	public String gentest_prefs_param() {
		return getMessage("gentest.prefs.param");
	}

	public String gentest_prefs_output_package() {
		return getMessage("gentest.prefs.output.package");
	}

	public String gentest_prefs_param_debugCheck() {
		return getMessage("gentest.prefs.param.debugCheck");
	}

	public String gentest_prefs_param_classMaxDepth() {
		return getMessage("gentest.prefs.param.classMaxDepth");
	}

	public String gentest_prefs_output() {
		return getMessage("gentest.prefs.output");
	}

	public String gentest_prefs_output_testcaseType_fail() {
		return getMessage("gentest.prefs.output.testcaseType.fail");
	}

	public String gentest_prefs_param_testPerQuery() {
		return getMessage("gentest.prefs.param.testPerQuery");
	}

	public String gentest_prefs_output_folder() {
		return getMessage("gentest.prefs.output.folder");
	}

	public String gentest_prefs_output_className() {
		return getMessage("gentest.prefs.output.className");
	}

	public String gentest_prefs_output_testcaseType_question() {
		return getMessage("gentest.prefs.output.testcaseType.question");
	}

	public String gentest_prefs_input() {
		return getMessage("gentest.prefs.input");
	}

	public String gentest_selection_empty() {
		return getMessage("gentest.selection.empty");
	}

	public String gentest_config_wizard_title() {
		return getMessage("gentest.config.wizard.title");
	}

	//	End generated part
}
