/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.commons.utils.IProjectUtils;
import tzuyu.plugin.tester.reporter.PluginLogger;
import tzuyu.plugin.tester.ui.AppEventManager;
import tzuyu.plugin.tester.ui.ValueChangedEvent;

/**
 * @author LLT
 * 
 */
public class PackageEditor extends StringButtonFieldEditor {
	private Messages msg = TzuyuPlugin.getMessages();
	private IPackageFragment selectedPackage;
	private IPackageFragmentRoot root;

	public PackageEditor(Composite parent, final AppEventManager eventManager) {
		super("packageEditor", "", parent);
		setChangeButtonText(msg.common_openBrowse());
		
		setPropertyChangeListener(new IPropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if (eventManager != null) {
					eventManager.fireEvent(new ValueChangedEvent<IPackageFragment>(PackageEditor.this, 
							null, selectedPackage));
				}
			}
		});
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
			return selectedPackage.getElementName();
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
			PluginLogger.getLogger().logEx(e);
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
		setStringValue(selectedPackage.getElementName());
	}

	public void setEventManager(AppEventManager eventManager) {
		eventManager.register(FolderSelectionEvent.TYPE,
				new FolderSelectionEvent.Listener() {

					public void onFolderChange(IPackageFragmentRoot packageRoot) {
						root = packageRoot;
					}

				});
	}

	public IPackageFragment getValue() {
		return selectedPackage;
	}
}
