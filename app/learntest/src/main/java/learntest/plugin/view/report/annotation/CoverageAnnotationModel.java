/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.RunTimeInfo;
import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.PluginException;
import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.commons.event.AnnotationChangeEvent;
import learntest.plugin.commons.event.IAnnotationChangeListener;
import learntest.plugin.commons.event.IJavaGentestCompleteListener;
import learntest.plugin.commons.event.JavaGentestEvent;
import learntest.plugin.utils.WorkbenchUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CoverageAnnotationModel
		implements IAnnotationModel, IJavaGentestCompleteListener, IAnnotationChangeListener {
	private static Logger log = LoggerFactory.getLogger(CoverageAnnotationModel.class);
	private static final String KEY = "L2tCoverageAnnotationModelKey"; //$NON-NLS-1$

	private List<IAnnotationModelListener> listeners = new ArrayList<IAnnotationModelListener>();
	private IDocument document;
	private ITextEditor textEditor;
	private List<Annotation> annotations;
	private boolean dirtyData;
	private boolean annotated;
	private Map<IMethod, List<String>> testcases;
	private ICompilationUnit cu;

	public CoverageAnnotationModel(ITextEditor textEditor, IDocument document, ICompilationUnit cu) {
		this.textEditor = textEditor;
		this.document = document;
		this.cu = cu;
		dirtyData = true;
		annotated = false;
		annotations = new ArrayList<Annotation>();
		LearntestPlugin.getJavaGentestEventManager().addJavaGentestCompleteListener(this);
		LearntestPlugin.getJavaGentestEventManager().addAnnotationChangedListener(this);
	}

	@Override
	public void addAnnotationModelListener(IAnnotationModelListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			fireModelChanged(new AnnotationModelEvent(this, true));
		}
	}

	private void fireModelChanged(AnnotationModelEvent event) {
		event.markSealed();
		if (!event.isEmpty()) {
			for (final IAnnotationModelListener l : listeners) {
				if (l instanceof IAnnotationModelListenerExtension) {
					((IAnnotationModelListenerExtension) l).modelChanged(event);
				} else {
					l.modelChanged(this);
				}
			}
		}
	}

	@Override
	public void removeAnnotationModelListener(IAnnotationModelListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void connect(IDocument document) {
		if (this.document != document) {
			log.warn("Cannot connect to document!");
			return;
		}
		updateAnnotationModel();
		for (Annotation annotation : annotations) {
			try {
				document.addPosition(getPosition(annotation));
			} catch (BadLocationException e) {
				log.error(e.getMessage());
			}
		}
	}

	@Override
	public void disconnect(IDocument document) {
		if (this.document != document) {
			log.warn("Cannot connect to document!");
			return;
		}
		for (Annotation annotation : annotations) {
			document.removePosition(getPosition(annotation));
		}
	}

	private void updateAnnotationModel() {
		if (!dirtyData && !textEditor.isDirty()) {
			return;
		}
		IModelRuntimeInfo info = findRuntimeInfo();
		if (info != null) {
			updateAnnotations(info);
			annotated = true;
		} else {
			if (annotated) {
				clear();
				annotated = false;
			}
		}
		dirtyData = false;
	}

	private void updateAnnotations(IModelRuntimeInfo info) {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
	    clear(event);
		Map<IMethod, RunTimeInfo> runtimeInfoMap = info.getRuntimeInfo();
		List<Integer> lines = new ArrayList<Integer>();
		for (IMethod method : runtimeInfoMap.keySet()) {
			List<String> selectedTcs = getSelectedTestcases(method);
			lines.addAll(runtimeInfoMap.get(method).getLineCoverageResult().getCoveredLines(selectedTcs));
		}
		for (Integer line : lines) {
			IRegion region;
			try {
				region = document.getLineInformation(line - 1);
				final CoverageAnnotation annotation = new CoverageAnnotation(region.getOffset(), region.getLength(), line);
				annotations.add(annotation);
				event.annotationAdded(annotation);
			} catch (BadLocationException e) {
				log.warn(e.getMessage());
			}
		}
		fireModelChanged(event);
	}

	private List<String> getSelectedTestcases(IMethod method) {
		if (CollectionUtils.isEmpty(testcases)) {
			return null;
		}
		return testcases.get(method);
	}

	private void clear() {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);
		fireModelChanged(event);
	}

	private void clear(AnnotationModelEvent event) {
		for (final Annotation ca : annotations) {
			event.annotationRemoved(ca, getPosition(ca));
		}
		annotations.clear();
	}

	private IModelRuntimeInfo findRuntimeInfo() {
		if (textEditor.isDirty()) {
			return null;
		}
		final IEditorInput input = textEditor.getEditorInput();
		if (input == null) {
			return null;
		}
		final IJavaElement element = input.getAdapter(IJavaElement.class);
		return element.getAdapter(IModelRuntimeInfo.class);
	}

	public static void attach(ITextEditor textEditor) {
		try {
			ICompilationUnit cu = WorkbenchUtils.getActiveCompilationUnit(textEditor);
			if (cu == null) {
				return;
			}
			IDocumentProvider documentProvider = textEditor.getDocumentProvider();
			if (documentProvider == null) {
				return;
			}
			IAnnotationModel annotationModel = documentProvider.getAnnotationModel(textEditor.getEditorInput());
			if (!(annotationModel instanceof IAnnotationModelExtension)) {
				return;
			}
			IAnnotationModelExtension modelExtension = (IAnnotationModelExtension) annotationModel;
			IDocument doc = documentProvider.getDocument(textEditor.getEditorInput());
			CoverageAnnotationModel coverageModel = (CoverageAnnotationModel) modelExtension.getAnnotationModel(KEY);
			if (coverageModel == null) {
				coverageModel = new CoverageAnnotationModel(textEditor, doc, cu);
				modelExtension.addAnnotationModel(KEY, coverageModel);
			}
		} catch (PluginException e) {
			return;
		}
	}

	@Override
	public void addAnnotation(Annotation annotation, Position position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAnnotation(Annotation annotation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Annotation> getAnnotationIterator() {
		if (dirtyData) {
			updateAnnotationModel();
		}
		return annotations.iterator();
	}

	@Override
	public Position getPosition(Annotation annotation) {
		return CoverageAnnotation.getPosition(annotation);
	}

	@Override
	public void onChanged(JavaGentestEvent event) {
		this.dirtyData = true;
	}

	@Override
	public void onChange(AnnotationChangeEvent event) {
		if (this.cu != event.getTargetMethod().getDeclaringType().getCompilationUnit()) {
			return;
		}
		if (testcases == null) {
			testcases = new HashMap<IMethod, List<String>>();
		}
		testcases.put(event.getTargetMethod(), event.getSelectedTestcases());
		this.dirtyData = true;
		fireModelChanged(new AnnotationModelEvent(this));
	}

}
