/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IProjectUtils;
import tzuyu.plugin.reporter.PluginLogger;
import tzuyu.plugin.ui.AppEventManager;

/**
 * @author LLT
 * 
 */
public class PackageEditor extends StringButtonFieldEditor {
	private Messages msg = TzuyuPlugin.getMessages();
	private IPackageFragment selectedPackage;
	private IPackageFragmentRoot root;

	public PackageEditor(Composite parent) {
		super("packageEditor", "", parent);
		setErrorMessage(msg.packageEditor_errorMessage());
		setChangeButtonText(msg.common_openBrowse());
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
	}

	@Override
	protected boolean doCheckState() {
		updatePackageFromText();
		return true;
	}

	@Override
	protected String changePressed() {
		updatePackageFromText();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), new JavaElementLabelProvider(
						JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(msg.packageEditor_selection_popup_title());
		dialog.setMessage(msg.packageEditor_selection_popup_desc());
		dialog.setElements(getPackages());
		dialog.setHelpAvailable(false);

		if (selectedPackage != null) {
			dialog.setInitialSelections(new Object[] { selectedPackage });
		}

		if (dialog.open() == Window.OK) {
			selectedPackage = (IPackageFragment) dialog.getFirstResult();
			return selectedPackage.getPath().makeRelativeTo(root.getPath())
					.toString();
		}
		return null;
	}

	private IJavaElement[] getPackages() {
		IJavaElement[] packages = null;
		try {
			if (root != null && root.exists()) {
				packages = root.getChildren();
			}
		} catch (JavaModelException e) {
			PluginLogger.logEx(e);
		}
		if (packages == null) {
			packages = new IJavaElement[0];
		}
		return packages;
	}

	private void updatePackageFromText() {
		selectedPackage = IProjectUtils.toPackageFragment(root, getStringValue());
	}
	
	public void setSelectedPackage(IPackageFragment selectedPackage) {
		this.selectedPackage = selectedPackage;
		if (selectedPackage == null) {
			setStringValue(null);
			return;
		}
		if (root != null) {
			setStringValue(IProjectUtils.toRelativePath(selectedPackage, root));
		} else {
			setStringValue(selectedPackage.getElementName());
		}
		
	}

	public void setEventManager(AppEventManager eventManager) {
		eventManager.register(FolderSelectionEvent.TYPE,
				new FolderSelectionEvent.Listener() {

					@Override
					public void onFolderChange(IPackageFragmentRoot packageRoot) {
						root = packageRoot;
					}

				});
	}

	public IPackageFragment getValue() {
		return selectedPackage;
	}
}
