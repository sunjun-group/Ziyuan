package microbat.views;

import icsetlv.common.dto.BreakPointValue;
import icsetlv.trial.model.TraceNode;

import java.util.ArrayList;
import java.util.List;

import microbat.graphdiff.GraphDiff;
import microbat.model.InterestedVariable;
import microbat.util.Settings;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.GraphNode;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ReferenceValue;


public class DebugFeedbackView extends ViewPart {

	private TraceNode node;
	private Boolean isCorrect;
	
//	public static final String INPUT = "input";
//	public static final String OUTPUT = "output";
//	public static final String STATE = "state";
	
	/**
	 * Here, the 0th element indicates input; 1st element indicates output; and 2nd element 
	 * indicates state.
	 */
//	private CheckboxTreeViewer[] treeViewerList = new CheckboxTreeViewer[3];
	
//	private Tree inputTree;
//	private Tree outputTree;
//	private Tree stateTree;
//	
//	private CheckboxTreeViewer inputTreeViewer;
//	private CheckboxTreeViewer outputTreeViewer;
	private CheckboxTreeViewer stateTreeViewer;
	private CheckboxTreeViewer consequenceTreeViewer;
	
	private Button yesButton;
	private Button noButton;
	
	public DebugFeedbackView() {
	}
	
	public void refresh(TraceNode node){
		this.node = node;
		
		BreakPointValue thisState = node.getProgramState();
//		BreakPointValue afterState = node.getAfterState();
		
		List<GraphDiff> cons = node.getConsequences();
		
//		HierarchyGraphDiffer differ = new HierarchyGraphDiffer();
//		differ.diff(thisState, afterState);
		
		createConsequenceContent(this.consequenceTreeViewer, cons);
		createStateContent(this.stateTreeViewer, thisState);
		
		yesButton.setSelection(false);
		noButton.setSelection(false);
		isCorrect = null;
		
	}
	
	
	private void createConsequenceContent(CheckboxTreeViewer viewer, List<GraphDiff> cons) {
		viewer.setContentProvider(new ConsequenceContentProvider());
		viewer.setLabelProvider(new ConsequenceLabelProvider());
		viewer.setInput(cons);	
		
		viewer.setCheckStateProvider(new VariableCheckStateProvider());
		viewer.refresh(true);
		
		addListener(viewer);
	}

	private void createStateContent(CheckboxTreeViewer viewer, BreakPointValue value){
		viewer.setContentProvider(new VariableContentProvider());
		viewer.setLabelProvider(new VariableLabelProvider());
		viewer.setInput(value);	
		
		viewer.setCheckStateProvider(new VariableCheckStateProvider());
		viewer.refresh(true);
		
		addListener(viewer);
	}
	
	private void addListener(final CheckboxTreeViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
			}
		});
		
		
		viewer.addCheckStateListener(new ICheckStateListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object obj = event.getElement();
				if(obj instanceof ExecValue){
					ExecValue value = (ExecValue)obj;
					boolean isChecked = viewer.getChecked(value);
					
					BreakPoint point = node.getBreakPoint();
					InterestedVariable iVar = new InterestedVariable(point.getClassCanonicalName(), 
							point.getLineNo(), value.getVarId());
					if(isChecked){
						if(!Settings.interestedVariables.contains(iVar)){
							Settings.interestedVariables.add(iVar);							
						}
					}
					else{
						Settings.interestedVariables.remove(iVar);
					}
					
					if(stateTreeViewer != null){
						BreakPointValue parent = (BreakPointValue) stateTreeViewer.getInput();
						ExecValue targetObj = parent.findVariableById(value.getVarId());
						
						if(targetObj != null){
							stateTreeViewer.setCheckStateProvider(new VariableCheckStateProvider());
							stateTreeViewer.refresh();						
						}
					}
				}
				else if(obj instanceof GraphDiff){
					GraphDiff diff = (GraphDiff)obj;
					
					
					
					if(consequenceTreeViewer != null){
						List<GraphDiff> conseq = (List<GraphDiff>) consequenceTreeViewer.getInput();
					}
				}
				
				
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout parentLayout = new GridLayout(1, true);
		parent.setLayout(parentLayout);

		createSubmitGroup(parent);
		createVariableComposite(parent);
	}

	private void createVariableComposite(Composite parent) {
		SashForm variableForm = new SashForm(parent, SWT.VERTICAL);
		variableForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

//		createVarGroup(variableForm, "Read Variables: ", INPUT);
//		createVarGroup(variableForm, "Consequences: ", OUTPUT);
		createConsequenceGroup(variableForm, "Consequences: ");
		createVarGroup(variableForm, "States: ");

		variableForm.setWeights(new int[] { 4, 6});
	}

	private void createVarGroup(SashForm variableForm, String groupName) {
		Group varGroup = new Group(variableForm, SWT.NONE);
		varGroup.setText(groupName);
		varGroup.setLayout(new FillLayout());

		Tree tree = new Tree(varGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.CHECK);		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeColumn typeColumn = new TreeColumn(tree, SWT.LEFT);
		typeColumn.setAlignment(SWT.LEFT);
		typeColumn.setText("Variable Type");
		typeColumn.setWidth(100);
		
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setAlignment(SWT.LEFT);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(100);
		
		TreeColumn valueColumn = new TreeColumn(tree, SWT.LEFT);
		valueColumn.setAlignment(SWT.LEFT);
		valueColumn.setText("Variable Value");
		valueColumn.setWidth(300);

		this.stateTreeViewer = new CheckboxTreeViewer(tree);
//		if(type.equals(INPUT)){
//			this.treeViewerList[0] = new CheckboxTreeViewer(tree);
//		}
//		else if(type.equals(OUTPUT)){
//			this.treeViewerList[1] = new CheckboxTreeViewer(tree);
//		}
//		else if(type.equals(STATE)){
//			this.treeViewerList[2] = new CheckboxTreeViewer(tree);
//		}
	}
	
	private void createConsequenceGroup(SashForm variableForm, String groupName) {
		Group varGroup = new Group(variableForm, SWT.NONE);
		varGroup.setText(groupName);
		varGroup.setLayout(new FillLayout());

		Tree tree = new Tree(varGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.CHECK);		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeColumn typeColumn = new TreeColumn(tree, SWT.LEFT);
		typeColumn.setAlignment(SWT.LEFT);
		typeColumn.setText("Variable Type");
		typeColumn.setWidth(100);
		
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setAlignment(SWT.LEFT);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(100);
		
		TreeColumn newValueColumn = new TreeColumn(tree, SWT.LEFT);
		newValueColumn.setAlignment(SWT.LEFT);
		newValueColumn.setText("New Value");
		newValueColumn.setWidth(130);
		
		TreeColumn oldValueColumn = new TreeColumn(tree, SWT.LEFT);
		oldValueColumn.setAlignment(SWT.LEFT);
		oldValueColumn.setText("Old Value");
		oldValueColumn.setWidth(130);
		
		this.consequenceTreeViewer = new CheckboxTreeViewer(tree);
	}

	private void createSubmitGroup(Composite parent) {
		Group feedbackGroup = new Group(parent, SWT.NONE);
		feedbackGroup.setText("Is this step correct?");
		feedbackGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, false));
		feedbackGroup.setLayout(new GridLayout(2, true));

		yesButton = new Button(feedbackGroup, SWT.RADIO);
		yesButton.setText(" Yes");
		yesButton.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, false));
		yesButton.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				isCorrect = true;
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		noButton = new Button(feedbackGroup, SWT.RADIO);
		noButton.setText(" No");
		noButton.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, false));
		noButton.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				isCorrect = false;
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		Label holder = new Label(feedbackGroup, SWT.NONE);
		holder.setText("");

		Button submitButton = new Button(feedbackGroup, SWT.NONE);
		submitButton.setText("submit");
		submitButton
				.setLayoutData(new GridData(SWT.RIGHT, SWT.UP, true, false));
		submitButton.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if (isCorrect == null) {
					MessageBox box = new MessageBox(PlatformUI.getWorkbench()
							.getDisplay().getActiveShell());
					box.setMessage("Please tell me whether this step is correct or not!");
					box.open();
				} else {
					// TODO start recommendation
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});
	}

	@Override
	public void setFocus() {

	}
	
	class ConsequenceContentProvider implements ITreeContentProvider{

		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof List){
				List<GraphDiff> diffs = (List<GraphDiff>) inputElement;
				Object[] elements = diffs.toArray(new GraphDiff[0]);
				
				return elements;
			}
			
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
		
	}
	
	class ConsequenceLabelProvider implements ITableLabelProvider{
		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		@SuppressWarnings("restriction")
		public String getColumnText(Object ele, int columnIndex) {
			
			if(ele instanceof GraphDiff){
				GraphDiff diff = (GraphDiff)ele;
				
				ExecValue before = (ExecValue)diff.getNodeBefore();
				ExecValue after = (ExecValue)diff.getNodeAfter();
				
				ExecValue element = (before != null) ? before : after;
				if(element == null){
					System.err.println("both before and empty of a diff are empty");
					return null;
				}
				else{
					if(element instanceof ReferenceValue){
						ReferenceValue value = (ReferenceValue)element;
						switch(columnIndex){
						case 0: 
							if(value.getClassType() != null){
								return value.getClassType().name();						
							}
							else{
								return "array";
							}
						case 1: return value.getVarId();
						case 2: 
							if(after != null){
								return String.valueOf(((ReferenceValue)after).getReferenceID());
							}
							else{
								return "NULL";
							}
						case 3: 
							if(before != null){
								return String.valueOf(((ReferenceValue)before).getReferenceID());
							}
							else{
								return "NULL";
							}
						}
					}
					else if(element instanceof ArrayValue){
						ArrayValue value = (ArrayValue)element;
						switch(columnIndex){
						case 0: return "array[" + value.getComponentType() + "]";
						case 1: return value.getVarId();
						case 2: 
							if(after != null){
								return String.valueOf(((ArrayValue)after).getReferenceID());
							}
							else{
								return "NULL";
							}
						case 3: 
							if(before != null){
								return String.valueOf(((ArrayValue)before).getReferenceID());
							}
							else{
								return "NULL";
							}
						}
					}
					else if(element instanceof PrimitiveValue){
						PrimitiveValue value = (PrimitiveValue)element;
						switch(columnIndex){
						case 0: return value.getPrimitiveType();
						case 1: return value.getVarId();
						case 2: 
							if(after != null){
								return ((PrimitiveValue)after).getStrVal();
							}
							else{
								return "NULL";
							}
						case 3: 
							if(before != null){
								return ((PrimitiveValue)before).getStrVal();
							}
							else{
								return "NULL";
							}
						}
					}
				}
			}
			
			return null;
		}
	}
	
	class VariableContentProvider implements ITreeContentProvider{
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof BreakPointValue){
				BreakPointValue value = (BreakPointValue)inputElement;
				return value.getChildren().toArray(new ExecValue[0]);
			}
			else if(inputElement instanceof ReferenceValue){
				ReferenceValue value = (ReferenceValue)inputElement;
				return value.getChildren().toArray(new ExecValue[0]);
			}
			return null;
		}

		public Object[] getChildren(Object parentElement) {
			return getElements(parentElement);
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			if(element instanceof ReferenceValue){
				return true;
			}
			return false;
		}
		
	}

	@SuppressWarnings("restriction")
	class VariableLabelProvider implements ITableLabelProvider{

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof ReferenceValue){
				ReferenceValue value = (ReferenceValue)element;
				switch(columnIndex){
				case 0: 
					if(value.getClassType() != null){
						return value.getClassType().name();						
					}
					else{
						return "array";
					}
				case 1: return value.getVarId();
				case 2: return "";
				}
			}
			else if(element instanceof ArrayValue){
				ArrayValue value = (ArrayValue)element;
				switch(columnIndex){
				case 0: return "array[" + value.getComponentType() + "]";
				case 1: return value.getVarId();
				case 2: return "";
				}
			}
			else if(element instanceof PrimitiveValue){
				PrimitiveValue value = (PrimitiveValue)element;
				switch(columnIndex){
				case 0: return value.getPrimitiveType();
				case 1: return value.getVarId();
				case 2: return value.getStrVal();
				}
			}
			return null;
		}
		
	}
	
	class VariableCheckStateProvider implements ICheckStateProvider{

		@Override
		public boolean isChecked(Object element) {
			if(element instanceof ExecValue){
				ExecValue value = (ExecValue)element;
				String varId = value.getVarId();
				
				if(node != null){
					BreakPoint point = node.getBreakPoint();
					InterestedVariable iVar = new InterestedVariable(point.getClassCanonicalName(), 
							point.getLineNo(), varId);
					
					if(Settings.interestedVariables.contains(iVar)){
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean isGrayed(Object element) {
			return false;
		}
		
	}
	
//	private List<ExecValue> filterVariable(BreakPointValue value, List<Variable> criteria){
//	ArrayList<ExecValue> list = new ArrayList<>();
//	for(ExecValue ev: value.getChildren()){
//		if(variableListContain(criteria, ev.getVarId())){
//			list.add(ev);
//		}
//	}
//	return list;
//}

//private boolean variableListContain(List<Variable> variables,
//		String varId) {
//	for(Variable var: variables){
//		if(var.getId().equals(varId)){
//			return true;
//		}
//	}
//	return false;
//}
}
