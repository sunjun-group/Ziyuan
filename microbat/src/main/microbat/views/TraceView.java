package microbat.views;

import icsetlv.trial.model.Trace;
import icsetlv.trial.model.TraceNode;

import java.util.Iterator;
import java.util.List;

import microbat.Activator;
import microbat.util.JavaUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import sav.strategies.dto.BreakPoint;

public class TraceView extends ViewPart {
	
	private TableViewer listViewer;

	public TraceView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		listViewer = new TableViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		listViewer.setContentProvider(new TraceContentProvider());
		listViewer.setLabelProvider(new TraceLabelProvider());
		
		Trace trace = Activator.getDefault().getCurrentTrace();
		listViewer.setInput(trace);
		
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					ISelection iSel = event.getSelection();
					if(iSel instanceof StructuredSelection){
						StructuredSelection sel = (StructuredSelection)iSel;
						Object obj = sel.getFirstElement();
						
						if(obj instanceof TraceNode){
							TraceNode node = (TraceNode)obj;
							DebugFeedbackView view = (DebugFeedbackView)PlatformUI.getWorkbench().
									getActiveWorkbenchWindow().getActivePage().showView(MicroBatViews.DEBUG_FEEDBACK);
							view.refresh(node);
							
							markJavaEditor(node);
							
							listViewer.getTable().setFocus();
						}
					}
					
				} catch (PartInitException e) {
					e.printStackTrace();
				}
				
			}

			@SuppressWarnings("unchecked")
			private void markJavaEditor(TraceNode node) {
				BreakPoint breakPoint = node.getBreakPoint();
				String qualifiedName = breakPoint.getClassCanonicalName();
				ICompilationUnit javaUnit = JavaUtil.findCompilationUnitInProject(qualifiedName);
				
				try {
					ITextEditor sourceEditor = (ITextEditor) JavaUI.openInEditor(javaUnit);
					AnnotationModel annotationModel = (AnnotationModel)sourceEditor.getDocumentProvider().
							getAnnotationModel(sourceEditor.getEditorInput());
					/**
					 * remove all the other annotations
					 */
					Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
					while(annotationIterator.hasNext()) {
						Annotation currentAnnotation = annotationIterator.next();
						annotationModel.removeAnnotation(currentAnnotation);
					}	
					
					IFile javaFile = (IFile)sourceEditor.getEditorInput().getAdapter(IResource.class);
					IDocumentProvider provider = new TextFileDocumentProvider();
					provider.connect(javaFile);
					IDocument document = provider.getDocument(javaFile);
					IRegion region = document.getLineInformation(breakPoint.getLineNo()-1);
					
					if (region != null) {
						sourceEditor.selectAndReveal(region.getOffset(), 0);
					}
					
					ReferenceAnnotation annotation = new ReferenceAnnotation(false, "Please check the status of this line");
					Position position = new Position(region.getOffset(), region.getLength());
					
					annotationModel.addAnnotation(annotation, position);
					
					
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (JavaModelException e) {
					e.printStackTrace();
				} catch (BadLocationException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void updateData(){
		Trace trace = Activator.getDefault().getCurrentTrace();
		listViewer.setInput(trace);
		listViewer.refresh();
	}

	@Override
	public void setFocus() {
		
	}
	
	class TraceContentProvider implements IStructuredContentProvider{

		public void dispose() {
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Trace){
				Trace trace = (Trace)inputElement;
				List<TraceNode> nodeList = trace.getExectionList();
				
				return nodeList.toArray(new TraceNode[0]);
			}
			
			return null;
		}

	}
	
	class TraceLabelProvider implements ILabelProvider{

		public void addListener(ILabelProviderListener listener) {
			
		}

		public void dispose() {
			
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			
		}

		public Image getImage(Object element) {
			if(element instanceof TraceNode){
				Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED);
				return image;				
			}
			
			return null;
		}

		public String getText(Object element) {
			if(element instanceof TraceNode){
				TraceNode node = (TraceNode)element;
				BreakPoint breakPoint = node.getBreakPoint();
//				BreakPointValue programState = node.getProgramState();
				
				String className = breakPoint.getClassCanonicalName();
//				String methodName = breakPoint.getMethodName();
				int lineNumber = breakPoint.getLineNo();
				int order = node.getOrder();
				
				//TODO it is better to parse method name as well.
//				String message = className + "." + methodName + "(...): line " + lineNumber;
				String message = order + ". " + className + " line:" + lineNumber;
				return message;
				
			}
			
			return null;
		}
		
	}

}
