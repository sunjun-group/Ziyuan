/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LineCoverage;
import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.commons.data.MethodRuntimeInfo;
import learntest.plugin.commons.event.AnnotationChangeEvent;
import learntest.plugin.commons.event.IJavaGentestCompleteListener;
import learntest.plugin.commons.event.IJavaModelRuntimeInfo;
import learntest.plugin.commons.event.JavaGentestEvent;
import learntest.plugin.view.report.actions.GroupByHandler;
import learntest.plugin.view.report.actions.UpdateAnnotationHandler;
import learntest.plugin.view.report.annotation.CoverageAnnotationModel;
import sav.common.core.Pair;
import sav.common.core.pattern.IDataProvider;
import sav.common.core.utils.AlphanumComparator;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class ReportView extends ViewPart implements IJavaGentestCompleteListener {
	public static final String ID = "learntest.plugin.view.reportView"; //$NON-NLS-1$
	
	private List<IHandler> handlers = new ArrayList<IHandler>();
	private ReportTreeViewer viewer;
	private ViewSettings settings = new ViewSettings();
	private UpdateAnnotationHandler updateAnnotationHandler;
	private IDataProvider<IJavaModelRuntimeInfo> dataProvider;
	private Text textArea;

	@Override
	public void createPartControl(Composite parent) {
		SashForm content = new SashForm(parent, SWT.VERTICAL);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer = new ReportTreeViewer(content, settings);
		textArea = new Text(content, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		textArea.setEditable(false);
		getSite().setSelectionProvider(viewer);
		addSelectionAction();
		registerContextMenu();
		addActionHandlers();
		LearntestPlugin.getJavaGentestEventManager().addJavaGentestCompleteListener(this);
		dataProvider = LearntestPlugin.getJavaModelRuntimeInfoProvider();
		if (dataProvider.getData() != null) {
			viewer.setInput(dataProvider.getData());
		}
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
		
		updateAnnotationHandler = new UpdateAnnotationHandler(viewer);
		addHandler(UpdateAnnotationHandler.ID, updateAnnotationHandler);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}
	
	private void addSelectionAction() {
		OpenAction openAction = new OpenAction(getViewSite());
		openAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
		openAction.setEnabled(false);
	    viewer.addOpenListener(new IOpenListener() {
			
			@Override
			public void open(OpenEvent event) {
				openAction.run((IStructuredSelection)event.getSelection());
				CoverageAnnotationModel.attachCoverageAnnotation();
			}
		});
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				Object[] elements = ((StructuredSelection) selection).toArray();
				Pair<IModelRuntimeInfo, List<String>> selectionInfo = getValidSelection(elements);
				if (selectionInfo == null) {
					textArea.setText("");
					return;
				}
				updateCoverage(selectionInfo);
				updateTextArea(selectionInfo);
				ReportView.this.setFocus();
			}
		});
	}
	
	/**
	 * @param selectionInfo
	 */
	protected void updateTextArea(Pair<IModelRuntimeInfo, List<String>> selectionInfo) {
		StringBuilder sb = new StringBuilder();
		List<String> selectedTcs = selectionInfo.b;
		if (selectedTcs == null) {
			List<RunTimeInfo> runtimeInfos = new ArrayList<>(selectionInfo.a.getRuntimeInfo().values());
			AlphanumComparator comparator = new AlphanumComparator();
			Collections.sort(runtimeInfos, new Comparator<RunTimeInfo>() {

				@Override
				public int compare(RunTimeInfo o1, RunTimeInfo o2) {
					return comparator.compare(o1.getMethodInfo().getMethodFullName(), o2.getMethodInfo().getMethodFullName());
				}
			});
			for (RunTimeInfo info : runtimeInfos) {
				sb.append("Target method: ").append(info.getMethodInfo().getMethodId()).append("\n")
				.append("------------------------------------").append("\n");
				sb.append(info.getCoverageInfo()).append("\n").append("==========================").append("\n");
				List<String> allTcs = new ArrayList<>(info.getLineCoverageResult().getTestCoverageMap().keySet());
				StringUtils.sortAlphanumericStrings(allTcs);
				sb.append(getCoverageText(allTcs, info)).append("\n");
			}
			textArea.setText(sb.toString());
		} else {
			RunTimeInfo runtimeInfo = ((MethodRuntimeInfo) selectionInfo.a).getRawRuntimeInfo();
			sb.append("Target method: ").append(runtimeInfo.getMethodInfo().getMethodId()).append("\n")
				.append("------------------------------------").append("\n");
			sb.append(getCoverageText(selectedTcs, runtimeInfo));
		}
		textArea.setText(sb.toString());
	}

	private String getCoverageText(List<String> selectedTcs, RunTimeInfo runtimeInfo) {
		StringBuilder sb = new StringBuilder();
		Map<String, LineCoverage> testCoverageMap = runtimeInfo
				.getLineCoverageResult().getTestCoverageMap();
		for (String tc : selectedTcs) {
			LineCoverage tcCoverageInfo = testCoverageMap.get(tc);
			sb.append("Testcase: ").append(tc).append("(): Lines").append(tcCoverageInfo.getCoveredLineNums()).append("\n");
			sb.append(tcCoverageInfo.getBranchCoverageText()).append("\n").append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * @param selectionInfo
	 */
	protected void updateCoverage(Pair<IModelRuntimeInfo, List<String>> selectionInfo) {
		if (!CollectionUtils.existIn(selectionInfo.a.getJavaElementType(), IJavaElement.COMPILATION_UNIT,
				IJavaElement.TYPE, IJavaElement.METHOD)) {
			return;
		}
		try {
			JavaUI.openInEditor(selectionInfo.a.getJavaElement());
			CoverageAnnotationModel.attachCoverageAnnotation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (selectionInfo.b == null) {
			Map<IMethod, RunTimeInfo> runtimeInfo = selectionInfo.a.getRuntimeInfo();
			for (IMethod method : runtimeInfo.keySet()) {
				LearntestPlugin.getJavaGentestEventManager()
						.fireAnnotationChange(new AnnotationChangeEvent(method, null));
			}
		} else {
			LearntestPlugin.getJavaGentestEventManager()
					.fireAnnotationChange(new AnnotationChangeEvent((IMethod) selectionInfo.a.getJavaElement(), selectionInfo.b));
		}
	}

	/**
	 * for single selection: always valid.
	 * for multi selection: only seletion of testcases of same target methods is allowed.
	 */
	protected Pair<IModelRuntimeInfo, List<String>> getValidSelection(Object[] elements) {
		if (CollectionUtils.isEmpty(elements)) {
			return null;
		}
		IModelRuntimeInfo methodRtInfo = null;
		if (elements.length == 1) {
			IJavaElement element = (IJavaElement) elements[0];
			methodRtInfo = dataProvider.getData().getCorrespondingRuntimeInfo(element);
			if (methodRtInfo == null) {
				return null;
			}
			if (methodRtInfo.getJavaElement() == elements[0] || (element.getElementType() != IJavaElement.METHOD)) {
				return Pair.of(methodRtInfo, null);
			}
		}
		List<String> selectedTcs = new ArrayList<String>();
		for (Object obj : elements) {
			IJavaElement element = (IJavaElement) obj;
			if (element.getElementType() != IJavaElement.METHOD) {
				return null; // invalid
			}
			IModelRuntimeInfo targetMethodInfo = dataProvider.getData().getCorrespondingRuntimeInfo(element);
			if (targetMethodInfo == null || targetMethodInfo.getJavaElement() == element) {
				return null; // invalid
			}
			if (methodRtInfo == null) {
				methodRtInfo = targetMethodInfo;
			}
			if (methodRtInfo != targetMethodInfo) {
				return null; // invalid
			}
		}
		selectedTcs = dataProvider.getData().getTestcaseStrings(elements);
		return Pair.of(methodRtInfo, selectedTcs);
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
	}

}
