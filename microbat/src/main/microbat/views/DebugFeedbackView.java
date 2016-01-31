package microbat.views;

import java.util.ArrayList;
import java.util.List;

import microbat.algorithm.graphdiff.GraphDiff;
import microbat.model.BreakPointValue;
import microbat.model.trace.TraceNode;
import microbat.model.value.ArrayValue;
import microbat.model.value.PrimitiveValue;
import microbat.model.value.ReferenceValue;
import microbat.model.value.VarValue;
import microbat.util.Settings;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
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
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


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
	private CheckboxTreeViewer writtenVariableTreeViewer;
	private CheckboxTreeViewer readVariableTreeViewer;
	
	private CheckboxTreeViewer consequenceTreeViewer;
	
	private ICheckStateListener stateListener;
	private ITreeViewerListener treeListener;
	
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
		
//		createConsequenceContent(cons);
		createStateContent(thisState);
		createWrittenVariableContent(node.getWrittenVariables());
		createReadVariableContect(node.getReadVariables());
		
		yesButton.setSelection(false);
		noButton.setSelection(false);
		isCorrect = null;
		
	}
	
	
	private void createWrittenVariableContent(List<VarValue> writtenVariables) {
		this.writtenVariableTreeViewer.setContentProvider(new RWVariableContentProvider());
		this.writtenVariableTreeViewer.setLabelProvider(new VariableLabelProvider());
		this.writtenVariableTreeViewer.setInput(writtenVariables);	
		
		setChecks(this.writtenVariableTreeViewer);

		this.writtenVariableTreeViewer.refresh(true);
		
	}

	private void createReadVariableContect(List<VarValue> readVariables) {
		this.readVariableTreeViewer.setContentProvider(new RWVariableContentProvider());
		this.readVariableTreeViewer.setLabelProvider(new VariableLabelProvider());
		this.readVariableTreeViewer.setInput(readVariables);	
		
		setChecks(this.readVariableTreeViewer);

		this.readVariableTreeViewer.refresh(true);
	}

	private void createConsequenceContent(List<GraphDiff> cons) {
		this.consequenceTreeViewer.setContentProvider(new ConsequenceContentProvider());
		this.consequenceTreeViewer.setLabelProvider(new ConsequenceLabelProvider());
		this.consequenceTreeViewer.setInput(cons);	
		
		setChecks(this.consequenceTreeViewer);
		
		this.consequenceTreeViewer.refresh(true);
	}

	private void createStateContent(BreakPointValue value){
		this.stateTreeViewer.setContentProvider(new VariableContentProvider());
		this.stateTreeViewer.setLabelProvider(new VariableLabelProvider());
		this.stateTreeViewer.setInput(value);	
		
		setChecks(this.stateTreeViewer);

		this.stateTreeViewer.refresh(true);
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
//		createConsequenceGroup(variableForm, "Consequences: ");
		this.writtenVariableTreeViewer = createVarGroup(variableForm, "Written Variables: ");
		this.readVariableTreeViewer = createVarGroup(variableForm, "Read Variables: ");
		this.stateTreeViewer = createVarGroup(variableForm, "States: ");

		variableForm.setWeights(new int[] { 3, 3, 4});
		
		addListener();
	}
	
	private void setChecks(CheckboxTreeViewer treeViewer){
		Tree tree = treeViewer.getTree();
		for(TreeItem item: tree.getItems()){
			setChecks(item);
		}
	}
	
	private void setChecks(TreeItem item){
		Object element = item.getData();
		if(element == null){
			return;
		}
		
		VarValue ev = null;
		if(element instanceof VarValue){
			ev = (VarValue)element;
		}
		else if(element instanceof GraphDiff){
			ev = (VarValue) ((GraphDiff)element).getChangedNode();
		}
		
//		InterestedVariable iv = new InterestedVariable(point.getDeclaringCompilationUnitName(), 
//				point.getLineNo(), ev);
		
		if(Settings.interestedVariables.contains(ev.getVarID())){
			item.setChecked(true);
		}
		else{
			item.setChecked(false);
		}

		for(TreeItem childItem: item.getItems()){
			setChecks(childItem);
		}
	}

	private void addListener() {
		stateListener = new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object obj = event.getElement();
				VarValue value = null;
				
				if(obj instanceof VarValue){
					value = (VarValue)obj;
				}
				else if(obj instanceof GraphDiff){
					GraphDiff diff = (GraphDiff)obj;
					value = (VarValue)diff.getChangedNode();
				}
				String varID = value.getVarID();
				
//				BreakPoint point = node.getBreakPoint();
//				InterestedVariable iVar = new InterestedVariable(point.getDeclaringCompilationUnitName(), 
//						point.getLineNo(), value);
				
				if(!Settings.interestedVariables.contains(varID)){
					Settings.interestedVariables.add(varID);							
				}
				else{
					Settings.interestedVariables.remove(varID);
				}
				
//				setChecks(consequenceTreeViewer);
				setChecks(readVariableTreeViewer);
				setChecks(writtenVariableTreeViewer);
				setChecks(stateTreeViewer);
				
				readVariableTreeViewer.refresh();
				writtenVariableTreeViewer.refresh();
				stateTreeViewer.refresh();	
				
//				consequenceTreeViewer.refresh();	
			}
			
			
		};
		
		treeListener = new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
//				setChecks(consequenceTreeViewer);
//				setChecks(stateTreeViewer);
//				
//				stateTreeViewer.refresh();	
//				consequenceTreeViewer.refresh();
				
				setChecks(readVariableTreeViewer);
				setChecks(writtenVariableTreeViewer);
				setChecks(stateTreeViewer);
				
				readVariableTreeViewer.refresh();
				writtenVariableTreeViewer.refresh();
				stateTreeViewer.refresh();	
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				
			}
		};
		
		this.readVariableTreeViewer.addTreeListener(treeListener);
		this.writtenVariableTreeViewer.addTreeListener(treeListener);
		this.stateTreeViewer.addTreeListener(treeListener);
		
		this.readVariableTreeViewer.addCheckStateListener(stateListener);
		this.writtenVariableTreeViewer.addCheckStateListener(stateListener);
		this.stateTreeViewer.addCheckStateListener(stateListener);
		
//		this.consequenceTreeViewer.addTreeListener(treeListener);
//		this.stateTreeViewer.addTreeListener(treeListener);
//		
//		this.consequenceTreeViewer.addCheckStateListener(stateListener);
//		this.stateTreeViewer.addCheckStateListener(stateListener);
	}

	private CheckboxTreeViewer createVarGroup(SashForm variableForm, String groupName) {
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

		return new CheckboxTreeViewer(tree);
//		this.stateTreeViewer = new CheckboxTreeViewer(tree);
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
	
	@SuppressWarnings("unchecked")
	class RWVariableContentProvider implements ITreeContentProvider{

		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ArrayList){
				ArrayList<VarValue> elements = (ArrayList<VarValue>)inputElement;
				return elements.toArray(new VarValue[0]);
			}
			
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof ReferenceValue){
				ReferenceValue parent = (ReferenceValue)parentElement;
				if(parent.getChildren() == null){
					VarValue vv = node.getProgramState().findVarValue(parent.getVarID());
					if(vv != null){
						parent.setChildren(vv.getChildren());
						return vv.getChildren().toArray(new VarValue[0]);
					}
				}
				else{
					return parent.getChildren().toArray(new VarValue[0]);
				}
			}
			
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof ReferenceValue){
				ReferenceValue parent = (ReferenceValue)element;
				
				List<VarValue> children = ((ReferenceValue)element).getChildren();
				if(children == null){
					VarValue vv = node.getProgramState().findVarValue(parent.getVarID());
					if(vv != null){
						parent.setChildren(vv.getChildren());
						return !parent.getChildren().isEmpty();
					}
				}
				else{
					return true;
				}
			}
			return false;
		}
		
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
		
		public String getColumnText(Object ele, int columnIndex) {
			
			if(ele instanceof GraphDiff){
				GraphDiff diff = (GraphDiff)ele;
				
				VarValue before = (VarValue)diff.getNodeBefore();
				VarValue after = (VarValue)diff.getNodeAfter();
				
				VarValue element = (before != null) ? before : after;
				if(element == null){
					System.err.println("both before and empty of a diff are empty");
					return null;
				}
				else{
					if(element instanceof ArrayValue){
						ArrayValue value = (ArrayValue)element;
						switch(columnIndex){
						case 0: return "array[" + value.getComponentType() + "]";
						case 1: return value.getVariablePath();
						case 2: 
							if(after != null){
								return "id = " + String.valueOf(((ArrayValue)after).getReferenceID());
							}
							else{
								return "NULL";
							}
						case 3: 
							if(before != null){
								return "id = " + String.valueOf(((ArrayValue)before).getReferenceID());
							}
							else{
								return "NULL";
							}
						}
					}
					else if(element instanceof ReferenceValue){
						ReferenceValue value = (ReferenceValue)element;
						switch(columnIndex){
						case 0: 
							if(value.getClassType() != null){
								return value.getConciseTypeName();						
							}
							else{
								return "array";
							}
						case 1: 
							return value.getVariablePath();
						case 2: 
							if(after != null){
								return "id = " + String.valueOf(((ReferenceValue)after).getReferenceID());
							}
							else{
								return "NULL";
							}
						case 3: 
							if(before != null){
								return "id = " + String.valueOf(((ReferenceValue)before).getReferenceID());
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
						case 1: return value.getVariablePath();
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
				return value.getChildren().toArray(new VarValue[0]);
			}
			else if(inputElement instanceof ReferenceValue){
				ReferenceValue value = (ReferenceValue)inputElement;
				return value.getChildren().toArray(new VarValue[0]);
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
				List<VarValue> children = ((ReferenceValue)element).getChildren();
				return children != null && !children.isEmpty();
			}
			return false;
		}
		
	}

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
						return value.getConciseTypeName();						
					}
					else{
						return "array";
					}
				case 1: 
					String name = value.getVarName();
//					if(value.isRoot() && value.isField()){
//						name = "this." + name;
//					}
					
//					System.out.println(value.getVarId());
					
					return name;
				case 2: return value.getMessageValue();
				}
			}
			else if(element instanceof ArrayValue){
				ArrayValue value = (ArrayValue)element;
				switch(columnIndex){
				case 0: return "array[" + value.getComponentType() + "]";
				case 1: 
					String name = value.getVarName();
					if(value.isRoot() && value.isField()){
						name = "this." + name;
					}
					return name;
				case 2: return value.getMessageValue();
				}
			}
			else if(element instanceof PrimitiveValue){
				PrimitiveValue value = (PrimitiveValue)element;
				switch(columnIndex){
				case 0: return value.getPrimitiveType();
				case 1: 
					String name = value.getVarName();
					if(value.isRoot() && value.isField()){
						name = "this." + name;
					}
					return name;
				case 2: return value.getStrVal() + " (id=" + value.getVarID() + ")";
				}
			}
			return null;
		}
		
	}
	
	class VariableCheckStateProvider implements ICheckStateProvider{

		@Override
		public boolean isChecked(Object element) {
			
			VarValue value = null;
			if(element instanceof VarValue){
				value = (VarValue)element;
			}
			else if(element instanceof GraphDiff){
				value = (VarValue) ((GraphDiff)element).getChangedNode();
			}
			
			if(node != null){
//				BreakPoint point = node.getBreakPoint();
//				InterestedVariable iVar = new InterestedVariable(point.getDeclaringCompilationUnitName(), 
//						point.getLineNo(), value);
				String varID = value.getVarID();
				if(Settings.interestedVariables.contains(varID)){
					return true;
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
