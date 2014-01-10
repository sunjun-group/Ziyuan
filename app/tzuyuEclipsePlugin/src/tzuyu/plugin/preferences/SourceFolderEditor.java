/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.refactoring.nls.SourceContainerDialog;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IProjectUtils;
import tzuyu.plugin.ui.AppEventManager;

/**
 * @author LLT
 *
 */
@SuppressWarnings("restriction")
public class SourceFolderEditor extends StringButtonFieldEditor {
	private final Messages msg = TzuyuPlugin.getMessages();
	private IJavaProject project;
	private Shell shell;
	private IPackageFragmentRoot outSourceFolder;
	private IWorkspaceRoot workspaceRoot;
	private AppEventManager eventManager;
	
	public SourceFolderEditor(Composite parent, IJavaProject project, Shell shell) {
		init("sourceFolderEditor", "");
		setEmptyStringAllowed(false);
        setErrorMessage(msg.sourceFolderEditor_errorMessage());
        setChangeButtonText(msg.common_openBrowse());
        setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
        createControl(parent);
        // init reference data
		this.project = project;
		this.shell = shell;
		workspaceRoot = project.getProject().getWorkspace().getRoot();
	}
	
	@Override
	protected boolean doCheckState() {
		updateSourceFolderFromText();
		return outSourceFolder != null;
	}
	
	@Override
	protected String changePressed() {
		updateSourceFolderFromText();
		if (outSourceFolder != null) {
			outSourceFolder = SourceContainerDialog.getSourceContainer(shell,
					workspaceRoot, outSourceFolder);

		} else {
			outSourceFolder = SourceContainerDialog.getSourceContainer(shell,
					workspaceRoot, project);
		}
		if (eventManager != null) {
			eventManager.fireEvent(new FolderSelectionEvent(outSourceFolder));
		}
		if (outSourceFolder == null) {
			return null;
		}
		return IProjectUtils.toRelativePath(outSourceFolder, project);
	}
	
	private void updateSourceFolderFromText() {
		outSourceFolder = IProjectUtils.toPackageFragmentRoot(project, getStringValue());
	}
	
	public void setOutSourceFolder(IPackageFragmentRoot outSourceFolder) {
		this.outSourceFolder = outSourceFolder;
		if (outSourceFolder == null) {
			setStringValue(null);
		} else {
			setStringValue(IProjectUtils.toRelativePath(outSourceFolder, project));
		}
		
		if (eventManager != null) {
			eventManager.fireEvent(new FolderSelectionEvent(outSourceFolder));
		}
	}

	public void setEventManager(AppEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public IPackageFragmentRoot getValue() {
		return outSourceFolder;
	}
}
