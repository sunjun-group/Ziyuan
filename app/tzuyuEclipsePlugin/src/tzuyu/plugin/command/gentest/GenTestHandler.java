/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.TzCommandHandler;
import tzuyu.plugin.command.TzJob;
import tzuyu.plugin.core.constants.Constants;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.core.utils.IStatusUtils;
import tzuyu.plugin.proxy.TzuyuEngineProxy;
import tzuyu.plugin.reporter.GenTestReporter;
import tzuyu.plugin.reporter.PluginLogger;
import tzuyu.plugin.ui.SWTFactory;

/**
 * @author LLT
 *
 */
public class GenTestHandler extends TzCommandHandler<GenTestPreferences> {
	private final static Messages msg = TzuyuPlugin.getMessages();

	@Override
	protected void run(WorkObject workObject, final GenTestPreferences config) {
		final GenTestReporter reporter = new GenTestReporter(config);
		if (prepareOutputFolder(config)) {
			GenTestJob job = new GenTestJob("Generate testcases", workObject,
					config, reporter);
			job.scheduleJob();
		}
	}
	
	public boolean prepareOutputFolder(GenTestPreferences config) {
		/* open package */
		try {
			if (!config.getOutputPackage().isOpen()) {
				config.setOutputPackage(config.getOutputFolder()
						.createPackageFragment(
								config.getOutputPackage().getElementName(),
								true, null));
				return true;
			} else {
				List<IResource> conflictResources = findConflictResource(config);
				/* warning if output package exists */
				if (conflictResources.isEmpty()
						|| config.getOutPkgConflictHandleOption() == OutputConflictHandle.ALWAYS_OVERRIDE) { 
					return true;
				}
				
				int confirm;
				if (config.getOutPkgConflictHandleOption() == OutputConflictHandle.ALWAYS_DELETE
						|| (confirm = PkgConfirmMsgDialog.confirm(TzuyuPlugin
								.getActiveWorkbenchWindow().getShell(), config
								.getProject())) == PkgConfirmMsgDialog.DELETE_IDX) {
					for (IResource resouce : conflictResources) {
						resouce.delete(true, null);
					}
					return true;
				}
				return confirm == PkgConfirmMsgDialog.OVERRIDE_IDX;
			}
		} catch (JavaModelException e) {
			PluginLogger.logEx(e);
		} catch (CoreException e) {
			PluginLogger.logEx(e);
		}
		return false;
	}

	/**
	 * find all resources under the output package which can be potential
	 * conflit with the generated files
	 */
	private List<IResource> findConflictResource(GenTestPreferences config)
			throws JavaModelException {
		List<IResource> conflitResource = new ArrayList<IResource>();
		// find dot file
		for (Object child : config.getOutputPackage().getNonJavaResources()) {
			if (child instanceof IFile) {
				IFile file = (IFile) child;
				if (Constants.DFA_FILE_EXTENSION.equals(file
						.getFileExtension())) {
					conflitResource.add(file);
				}
			}
		}
		for (IJavaElement child : config.getPassPackage().getChildren()) {
			conflitResource.add(child.getResource());
		}
		for (IJavaElement child : config.getFailPackage().getChildren()) {
			conflitResource.add(child.getResource());
		}
		return conflitResource;
	}
	
	@Override
	protected GenTestPreferences initConfiguration(WorkObject workObject) {
		return TzuyuPlugin.getDefault().getGenTestPreferences(workObject.getProject());
	}
	
	private static class GenTestJob extends TzJob {
		private WorkObject workObject;
		private GenTestPreferences config;
		private GenTestReporter reporter;

		public GenTestJob(String name, WorkObject workObject,
				GenTestPreferences config, GenTestReporter reporter) {
			super(name);
			this.workObject = workObject;
			this.config = config;
			this.reporter = reporter;
		}

		@Override
		protected IStatus doJob(IProgressMonitor monitor) {
			TzuyuEngineProxy.generateTestCases(workObject, config, reporter);
			// refresh output folder
			try {
				config.getOutputPackage().getResource().refreshLocal(2, null);
			} catch (CoreException e) {
				PluginLogger.logEx(e);
			}
			monitor.done();
			return IStatusUtils.OK_STATUS;
		}
	}

	private static class PkgConfirmMsgDialog extends MessageDialog {
		public static final int DELETE_IDX = 0;
		public static final int OVERRIDE_IDX = 1;
		public static final int CANCEL_IDX = 2;
		private Button donotAskAgain;
		private IJavaProject project;

		public PkgConfirmMsgDialog(Shell parentShell, IJavaProject project) {
			super(parentShell, msg.message_dialog_title(), null, msg
					.gentest_warning_output_package_open(),
					MessageDialog.WARNING, new String[] {
							msg.gentest_pkg_out_files_conflict_delete_first(),
							msg.gentest_pkg_out_files_conflict_allow_override(),
							IDialogConstants.CANCEL_LABEL }, 0);
			this.project = project;
		}
		
		@Override
		protected Control createCustomArea(Composite parent) {
			donotAskAgain = SWTFactory.createCheckbox(parent,
					msg.gentest_pkg_out_files_conflict_save_selection());
			return donotAskAgain;
		}
		
		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId != CANCEL_IDX && donotAskAgain.getSelection()) {
				// save to the project preferences
				OutputConflictHandle option = OutputConflictHandle.ALWAYS_DELETE;
				if (buttonId == OVERRIDE_IDX) {
					option = OutputConflictHandle.ALWAYS_OVERRIDE;
				}
				TzuyuPlugin.getDefault().persistPreferences(project.getProject(), 
						GenTestPreferences.OUT_PKG_CONFLICT_HANDLE.a, 
						option.name(), "");
			}
			super.buttonPressed(buttonId);
		}

		/**
		 * @param project 
		 * @return:
		 * 0: delete
		 * 1: continue
		 * 2: cancel
		 */
		public static int confirm(Shell parentShell, IJavaProject project) {
			return (new PkgConfirmMsgDialog(parentShell, project)).open();
		}
	}
}
