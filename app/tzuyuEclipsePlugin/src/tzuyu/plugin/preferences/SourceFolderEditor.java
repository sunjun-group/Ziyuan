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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.utils.IProjectUtils;
import tzuyu.plugin.ui.AppEvent;
import tzuyu.plugin.ui.AppEventManager;
import tzuyu.plugin.ui.ValueChangedEvent;

/**
 * @author LLT
 *
 */
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
        setChangeButtonText(msg.common_openBrowse());
        createControl(parent);
        // init reference data
		this.project = project;
		this.shell = shell;
		workspaceRoot = project.getProject().getWorkspace().getRoot();
		setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				fireEvent(new ValueChangedEvent<IPackageFragmentRoot>(SourceFolderEditor.this,
						null, outSourceFolder));
			}
		});
	}
	
	protected void fireEvent(AppEvent event) {
		if (eventManager != null) {
			eventManager.fireEvent(event);
		}
	}

	@Override
	protected boolean doCheckState() {
		updateSourceFolderFromText();
		return outSourceFolder != null;
	}
	
	@SuppressWarnings("restriction")
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
