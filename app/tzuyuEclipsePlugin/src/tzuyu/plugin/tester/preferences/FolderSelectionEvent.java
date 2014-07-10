/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import org.eclipse.jdt.core.IPackageFragmentRoot;

import tzuyu.plugin.tester.ui.AppEvent;
import tzuyu.plugin.tester.ui.AppListener;

/**
 * @author LLT
 *
 */
public class FolderSelectionEvent implements AppEvent {
	public static String TYPE = "FolderSelection";
	public interface Listener extends AppListener {
		public void onFolderChange(IPackageFragmentRoot packageRoot);
	}
	private IPackageFragmentRoot root;

	public FolderSelectionEvent(IPackageFragmentRoot root) {
		this.root = root;
	}

	public String getType() {
		return TYPE;
	}

	public void execute(AppListener listener) {
		if (listener instanceof Listener) {
			((Listener)listener).onFolderChange(root);
		}
		
	}
	
}
