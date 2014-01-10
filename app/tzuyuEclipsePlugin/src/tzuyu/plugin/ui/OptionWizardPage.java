/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;

import tzuyu.plugin.core.dto.TzPreferences;
import tzuyu.plugin.ui.option.classSelector.Option;

/**
 * @author LLT
 * 
 */
public abstract class OptionWizardPage<T extends TzPreferences> extends WizardPage {
	private List<Option<T>> options;
	
	protected OptionWizardPage(String pageName) {
		super(pageName);
		options = new ArrayList<Option<T>>();
	}
	
	// extend initPageFrom() instead.
	public final void initFrom(T config) {
		initPageFrom(config);
		for (Option<T> opt : options) {
			opt.initFrom(config);
		}
	}

	protected void initPageFrom(T config) {
		// do nothing at this moment.
	}

	public abstract boolean isValid(T config);

	/**
	 * Adds the specified option to this page if it is not already present.
	 * 
	 * @param option
	 *            option to be added to this tab
	 * @return <code>true</code> if this page did not already contain the
	 *         specified option
	 */
	protected boolean addOption(Option<T> option) {
		return options.add(option);
	}
}
