/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.event.IJavaGentestCompleteListener;
import learntest.plugin.commons.event.JavaGentestEvent;
import learntest.plugin.view.report.actions.GroupByHandler;
import learntest.plugin.view.report.actions.UpdateAnnotationHandler;
import learntest.plugin.view.report.annotation.CoverageAnnotationModel;

/**
 * @author LLT
 *
 */
public class ReportView extends ViewPart implements IJavaGentestCompleteListener {
	public static final String ID = "learntest.plugin.view.reportView"; //$NON-NLS-1$
	
	private List<IHandler> handlers = new ArrayList<IHandler>();
	private ReportTreeViewer viewer;
	private ViewSettings settings = new ViewSettings();

	@Override
	public void createPartControl(Composite parent) {
		viewer = new ReportTreeViewer(parent, settings);
		getSite().setSelectionProvider(viewer);
		addSelectionAction();
		registerContextMenu();
		addActionHandlers();
		LearntestPlugin.getJavaGentestEventManager().addJavaGentestCompleteListener(this);
	}

	private void addActionHandlers() {
		addHandler(GroupByHandler.ID, new GroupByHandler(viewer, settings));
	}

	private IHandlerActivation addHandler(String commandId, IHandler handler) {
		handlers.add(handler);
		return getSite().getService(IHandlerService.class).activateHandler(commandId, handler);
	}
	
	public void dispose() {
		for (IHandler handler : handlers) {
			handler.dispose();
		}
		super.dispose();
	}

	private void registerContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		viewer.getTree().setMenu(menuMgr.createContextMenu(viewer.getTree()));
		getSite().registerContextMenu(menuMgr, viewer);
		
		UpdateAnnotationHandler updateAnnotationHandler = new UpdateAnnotationHandler(viewer);
		addHandler(UpdateAnnotationHandler.ID, updateAnnotationHandler);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}
	
	private void addSelectionAction() {
	    OpenAction openAction = new OpenAction(getSite());
	    openAction
	        .setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
	    openAction.setEnabled(false);
	    viewer.addSelectionChangedListener(openAction);
	    viewer.addOpenListener(new IOpenListener() {
			
			@Override
			public void open(OpenEvent event) {
				openAction.run((IStructuredSelection) event.getSelection());
			}
		});
	    viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IWorkbenchPage page= LearntestPlugin.getActiveWorkbenchWindow().getActivePage();
				IEditorPart editor = page != null ? editor= page.getActiveEditor() : null;
				if (editor instanceof ITextEditor) {
					CoverageAnnotationModel.attach((ITextEditor) editor);
				}
			}
		});
	    
	}

	@Override
	public void onChanged(final JavaGentestEvent event) {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (viewer.getControl().isDisposed()) {
					return;
				}
				viewer.setInput(event.getRuntimeInfo());
			}
		});
		try {
			// wait for a while for the console to complete.
			Thread.sleep(200l);
		} catch (InterruptedException e) {
			// do nothing
		}
		getSite().getPage().bringToTop(this);
	}

}
