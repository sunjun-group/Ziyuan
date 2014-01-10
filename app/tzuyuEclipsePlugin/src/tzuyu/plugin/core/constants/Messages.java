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

	public String gentest_prefs_tab_output() {
		return getMessage("gentest_prefs_tab_output");
	}

	public String gentest_selection_empty() {
		return getMessage("gentest_selection_empty");
	}

	public String gentest_prefs_output_error_className_empty() {
		return getMessage("gentest_prefs_output_error_className_empty");
	}

	public String gentest_prefs_param_classMaxDepth() {
		return getMessage("gentest_prefs_param_classMaxDepth");
	}

	public String gentest_prefs_tab_param() {
		return getMessage("gentest_prefs_tab_param");
	}

	public String common_openBrowse() {
		return getMessage("common_openBrowse");
	}

	public String gentest_prefs_param_testPerQuery() {
		return getMessage("gentest_prefs_param_testPerQuery");
	}

	public String gentest_prefs_output_warning_className_lowercase() {
		return getMessage("gentest_prefs_output_warning_className_lowercase");
	}

	public String gentest_prefs_output_package() {
		return getMessage("gentest_prefs_output_package");
	}

	public String sourceFolderEditor_errorMessage() {
		return getMessage("sourceFolderEditor_errorMessage");
	}

	public String gentest_prefs_param() {
		return getMessage("gentest_prefs_param");
	}

	public String gentest_prefs_output_className() {
		return getMessage("gentest_prefs_output_className");
	}

	public String gentest_prefs_output_testcaseType_question() {
		return getMessage("gentest_prefs_output_testcaseType_question");
	}

	public String gentest_prefs_output_testcaseType_pass() {
		return getMessage("gentest_prefs_output_testcaseType_pass");
	}

	public String gentest_prefs_input() {
		return getMessage("gentest_prefs_input");
	}

	public String gentest_prefs_output() {
		return getMessage("gentest_prefs_output");
	}

	public String packageEditor_selection_popup_desc() {
		return getMessage("packageEditor_selection_popup_desc");
	}

	public String gentest_prefs_output_testcaseType_fail() {
		return getMessage("gentest_prefs_output_testcaseType_fail");
	}

	public String gentest_config_wizard_title() {
		return getMessage("gentest_config_wizard_title");
	}

	public String packageEditor_selection_popup_title() {
		return getMessage("packageEditor_selection_popup_title");
	}

	public String packageEditor_errorMessage() {
		return getMessage("packageEditor_errorMessage");
	}

	public String sourceFolderEditor_description() {
		return getMessage("sourceFolderEditor_description");
	}

	public String gentest_prefs_param_debugCheck() {
		return getMessage("gentest_prefs_param_debugCheck");
	}

	public String gentest_prefs_output_folder() {
		return getMessage("gentest_prefs_output_folder");
	}

	//	End generated part
}
