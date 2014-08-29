/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.gentest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.statushandlers.StatusManager;

import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.plugin.AppAdaptorFactory;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.commons.constants.PluginConstants;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.exception.ErrorType;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.IStatusUtils;
import tzuyu.plugin.tester.command.TzCommandHandler;
import tzuyu.plugin.tester.command.TzJob;
import tzuyu.plugin.tester.preferences.component.MessageDialogs;
import tzuyu.plugin.tester.reporter.GenTestReporter;
import tzuyu.plugin.tester.reporter.PluginLogger;
import tzuyu.plugin.tester.ui.SWTFactory;

/**
 * @author LLT
 *
 */
public class GenTestHandler extends TzCommandHandler<GenTestPreferences> {
	private final static Messages msg = TzuyuPlugin.getMessages();

	@Override
	protected void run(WorkObject workObject, final GenTestPreferences config) {
		if (preProcess(config)) {
			GenTestJob job = new GenTestJob("Generate testcases", workObject,
					config);
			job.scheduleJob();
		}
	}
	
	public boolean preProcess(GenTestPreferences config) {
		/* open package */
		try {
			if (config.getOutputFolder() == null
					|| config.getOutputPackage() == null) {
				// TODO LLT: open preferences page for user to edit
				throw new PluginException(ErrorType.UNDEFINED_OUTPUT_FOLDER);
			}
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
		} catch (Exception e) {
			handleException(e);
		}
		return false;
	}

	/**
	 * find all resources under the output package which can be potential
	 * conflit with the generated files
	 */
	private List<IResource> findConflictResource(GenTestPreferences config)
			throws JavaModelException {
		List<IResource> conflictResource = new ArrayList<IResource>();
		// find dot file
		for (Object child : config.getOutputPackage().getNonJavaResources()) {
			if (child instanceof IFile) {
				IFile file = (IFile) child;
				if (PluginConstants.DFA_FILE_EXTENSION.equals(file
						.getFileExtension())) {
					conflictResource.add(file);
				}
			}
		}
		addAllChildrenToResources(conflictResource, config.getPassPackage());
		addAllChildrenToResources(conflictResource, config.getFailPackage());
		return conflictResource;
	}

	private void addAllChildrenToResources(List<IResource> resources,
			IPackageFragment pkg) throws JavaModelException {
		if (pkg.isOpen()) {
			for (IJavaElement child : pkg.getChildren()) {
				resources.add(child.getResource());
			}
		}
	}
	
	@Override
	protected GenTestPreferences initConfiguration(WorkObject workObject) {
		return TzuyuPlugin.getDefault().getGenTestPreferences(workObject.getProject());
	}
	
	private class GenTestJob extends TzJob {
		private WorkObject workObject;
		private GenTestPreferences config;
		private GenTestReporter reporter;

		public GenTestJob(String name, WorkObject workObject,
				GenTestPreferences config) {
			super(name);
			this.workObject = workObject;
			this.config = config;
			this.reporter = new GenTestReporter(config);
		}
		
		@Override
		protected IStatus doJob(IProgressMonitor monitor) {
			monitor.beginTask("start test generation", 1);
			reporter.setProgressMonitor(monitor);
			try {
//				AppAdaptorFactory.getTzuyuAdaptor().dfaLearning(workObject,
//						config, reporter, monitor);
				runJob(workObject, config, reporter, monitor);
				// refresh output folder
				config.getOutputPackage().getResource().refreshLocal(2, monitor);
			} catch (Exception e) {
				handleException(e);
			}
			
			monitor.done();
			return IStatusUtils.OK_STATUS;
		}

	}
	
	protected void runJob(WorkObject workObject, GenTestPreferences config,
			GenTestReporter reporter, IProgressMonitor monitor)
			throws InterruptedException, TzException {
		AppAdaptorFactory.getTzuyuAdaptor().generateTestcases(workObject,
				config, reporter, monitor);
	}
	
	protected static void handleException(Exception e) {
		if (e instanceof InterruptedException) {
			PluginLogger.getLogger().info("User cancelled the job!!");
			return;
		}
		PluginLogger.getLogger().logEx(e);
		if (e instanceof PluginException) {
			MessageDialogs.showErrorInUI(msg.getMessage(((PluginException)e).getType()));
		} else if (e instanceof TzException) {
			// expected error
			TzException tzEx = (TzException) e;
			MessageDialogs.showWarningInUI(msg.getMessage(tzEx.getType(), tzEx.getParams()));
		} else if (e instanceof TzRuntimeException) {
			// unexpected error
			TzRuntimeException tzRtEx = (TzRuntimeException) e;
			String eMsg = e.getMessage();
			if (tzRtEx.getType() != null) {
				eMsg = msg.getMessage(tzRtEx.getType()) + eMsg;
			}
			StatusManager.getManager().handle(IStatusUtils.error(eMsg),
					StatusManager.BLOCK);
		} else {
			StatusManager.getManager().handle(IStatusUtils.error(e.getMessage()),
					StatusManager.BLOCK);
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
		 * DELETE_IDX
		 * OVERRIDE_IDX
		 * CANCEL_IDX
		 */
		public static int confirm(Shell parentShell, IJavaProject project) {
			return (new PkgConfirmMsgDialog(parentShell, project)).open();
		}
	}
}
