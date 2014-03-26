/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.constants;

import java.text.MessageFormat;
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
		String key = StringUtils.dotJoin(val.getClass().getSimpleName(),
				val.name());
		return enumConstantResourceBundle.getString(key);
	}
	
	public String getMessage(String key, Object... args) {
		String msg = getMessage(key);
		Object[] convertedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof Enum<?>) {
				convertedArgs[i] = getMessage((Enum<?>) arg);
			} else {
				convertedArgs[i] = arg;
			}
		}
		return MessageFormat.format(msg, convertedArgs);
	}
	
	private String getMessage(String key) {
		return messagesResourceBundle.getString(key); 
	}
	
	/* see Tzuyu.tools.GenerateMessagesClass for generation detail*/
	//	Generated part

	public String inputWizardPage_name() {
		return getMessage("inputWizardPage_name");
	}

	public String gentest_prefs_output_error_testCaseType_empty() {
		return getMessage("gentest_prefs_output_error_testCaseType_empty");
	}

	public String gentest_prefs_param_generic_table_duplicate_confirm(Object arg0) {
		return getMessage("gentest_prefs_param_generic_table_duplicate_confirm", arg0);
	}

	public String sourceFolderEditor_errorMessage() {
		return getMessage("sourceFolderEditor_errorMessage");
	}

	public String gentest_prefs_param() {
		return getMessage("gentest_prefs_param");
	}

	public String gentest_prefs_param_group_primitive() {
		return getMessage("gentest_prefs_param_group_primitive");
	}

	public String message_dialog_title() {
		return getMessage("message_dialog_title");
	}

	public String editDialog_new_title(Object arg0) {
		return getMessage("editDialog_new_title", arg0);
	}

	public String gentest_prefs_output_testcaseType_pass() {
		return getMessage("gentest_prefs_output_testcaseType_pass");
	}

	public String gentest_prefs_output_className() {
		return getMessage("gentest_prefs_output_className");
	}

	public String packageEditor_selection_popup_desc() {
		return getMessage("packageEditor_selection_popup_desc");
	}

	public String gentest_prefs_tab_param() {
		return getMessage("gentest_prefs_tab_param");
	}

	public String gentest_prefs_tab_output() {
		return getMessage("gentest_prefs_tab_output");
	}

	public String packageEditor_selection_popup_title() {
		return getMessage("packageEditor_selection_popup_title");
	}

	public String gentest_prefs_param_inheritMethod() {
		return getMessage("gentest_prefs_param_inheritMethod");
	}

	public String genTestWizard_title() {
		return getMessage("genTestWizard_title");
	}

	public String typeScopeDialog_desc() {
		return getMessage("typeScopeDialog_desc");
	}

	public String gentest_prefs_param_group_generic() {
		return getMessage("gentest_prefs_param_group_generic");
	}

	public String gentest_pkg_out_files_conflict_delete_first() {
		return getMessage("gentest_pkg_out_files_conflict_delete_first");
	}

	public String gentest_prefs_output() {
		return getMessage("gentest_prefs_output");
	}

	public String common_editButton() {
		return getMessage("common_editButton");
	}

	public String sourceFolderEditor_description() {
		return getMessage("sourceFolderEditor_description");
	}

	public String common_select() {
		return getMessage("common_select");
	}

	public String gentest_prefs_output_testcaseType_fail() {
		return getMessage("gentest_prefs_output_testcaseType_fail");
	}

	public String gentest_prefs_output_maxMethodsPerClass() {
		return getMessage("gentest_prefs_output_maxMethodsPerClass");
	}

	public String gentest_prefs_param_group_learning() {
		return getMessage("gentest_prefs_param_group_learning");
	}

	public String gentest_prefs_output_maxLinesPerClass() {
		return getMessage("gentest_prefs_output_maxLinesPerClass");
	}

	public String common_openBrowse() {
		return getMessage("common_openBrowse");
	}

	public String intText_error_parse(Object arg0) {
		return getMessage("intText_error_parse", arg0);
	}

	public String packageEditor_errorMessage() {
		return getMessage("packageEditor_errorMessage");
	}

	public String typeScopeDialog_title() {
		return getMessage("typeScopeDialog_title");
	}

	public String gentest_pkg_out_files_conflict_allow_override() {
		return getMessage("gentest_pkg_out_files_conflict_allow_override");
	}

	public String gentest_prefs_param_classMaxDepth() {
		return getMessage("gentest_prefs_param_classMaxDepth");
	}

	public String typeScopeDialog_type() {
		return getMessage("typeScopeDialog_type");
	}

	public String typeScopeDialog_scope() {
		return getMessage("typeScopeDialog_scope");
	}

	public String gentest_prefs_param_arrayMaxDepth() {
		return getMessage("gentest_prefs_param_arrayMaxDepth");
	}

	public String gentest_selection_empty() {
		return getMessage("gentest_selection_empty");
	}

	public String gentest_prefs_param_generic_table_value_col() {
		return getMessage("gentest_prefs_param_generic_table_value_col");
	}

	public String gentest_prefs_param_stringMaxLength() {
		return getMessage("gentest_prefs_param_stringMaxLength");
	}

	public String gentest_warning_output_package_open() {
		return getMessage("gentest_warning_output_package_open");
	}

	public String gentest_prefs_output_folder() {
		return getMessage("gentest_prefs_output_folder");
	}

	public String gentest_prefs_param_objectTypeTitle() {
		return getMessage("gentest_prefs_param_objectTypeTitle");
	}

	public String gentest_prefs_param_objectToInteger() {
		return getMessage("gentest_prefs_param_objectToInteger");
	}

	public String intText_error_not_positive(Object arg0) {
		return getMessage("intText_error_not_positive", arg0);
	}

	public String error_not_defined(Object arg0) {
		return getMessage("error_not_defined", arg0);
	}

	public String gentest_prefs_param_prettyPrint() {
		return getMessage("gentest_prefs_param_prettyPrint");
	}

	public String common_addButton() {
		return getMessage("common_addButton");
	}

	public String gentest_prefs_output_testClassFormat() {
		return getMessage("gentest_prefs_output_testClassFormat");
	}

	public String gentest_prefs_param_testPerQuery() {
		return getMessage("gentest_prefs_param_testPerQuery");
	}

	public String error_mandatory(Object arg0) {
		return getMessage("error_mandatory", arg0);
	}

	public String gentest_prefs_input_error_empty_selection() {
		return getMessage("gentest_prefs_input_error_empty_selection");
	}

	public String gentest_prefs_output_error_className_empty() {
		return getMessage("gentest_prefs_output_error_className_empty");
	}

	public String gentest_pkg_out_files_conflict_save_selection() {
		return getMessage("gentest_pkg_out_files_conflict_save_selection");
	}

	public String gentest_prefs_output_testcaseType_question() {
		return getMessage("gentest_prefs_output_testcaseType_question");
	}

	public String typeScopeDialog_impl_type() {
		return getMessage("typeScopeDialog_impl_type");
	}

	public String gentest_prefs_param_generic_table_name_col() {
		return getMessage("gentest_prefs_param_generic_table_name_col");
	}

	public String gentest_config_wizard_title() {
		return getMessage("gentest_config_wizard_title");
	}

	public String gentest_prefs_output_warning_className_lowercase() {
		return getMessage("gentest_prefs_output_warning_className_lowercase");
	}

	public String common_removeButton() {
		return getMessage("common_removeButton");
	}

	public String gentest_prefs_output_paramDeclarationFormat() {
		return getMessage("gentest_prefs_output_paramDeclarationFormat");
	}

	public String gentest_prefs_output_output_files_conflict_handle() {
		return getMessage("gentest_prefs_output_output_files_conflict_handle");
	}

	public String gentest_prefs_param_debugCheck() {
		return getMessage("gentest_prefs_param_debugCheck");
	}

	public String gentest_prefs_param_forbitNull() {
		return getMessage("gentest_prefs_param_forbitNull");
	}

	public String gentest_prefs_input() {
		return getMessage("gentest_prefs_input");
	}

	public String gentest_prefs_param_description() {
		return getMessage("gentest_prefs_param_description");
	}

	public String editDialog_edit_title(Object arg0) {
		return getMessage("editDialog_edit_title", arg0);
	}

	public String gentest_prefs_output_package() {
		return getMessage("gentest_prefs_output_package");
	}

	//	End generated part
}
