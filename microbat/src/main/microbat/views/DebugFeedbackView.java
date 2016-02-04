package microbat.views;

import java.util.ArrayList;
import java.util.List;

import microbat.Activator;
import microbat.algorithm.graphdiff.GraphDiff;
import microbat.model.BreakPointValue;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.ArrayValue;
import microbat.model.value.PrimitiveValue;
import microbat.model.value.ReferenceValue;
import microbat.model.value.VarValue;
import microbat.util.Settings;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class DebugFeedbackView extends ViewPart {

	public static final String CORRECT = "correct";
	public static final String INCORRECT = "incorrect";
	public static final String UNCLEAR = "unclear";
	
	private TraceNode currentNode;
	private TraceNode lastestNode;
	
	private String feedbackType;
	
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
	private ICheckStateListener RWVarListener;
	private ITreeViewerListener treeListener;
	
	private Button yesButton;
	private Button noButton;
	private Button unclearButton;
	
	public DebugFeedbackView() {
	}
	
	public void refresh(TraceNode node){
		this.currentNode = node;
		
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
		unclearButton.setSelection(false);
		feedbackType = null;
		
	}
	
	
	private void createWrittenVariableContent(List<VarValue> writtenVariables) {
		this.writtenVariableTreeViewer.setContentProvider(new RWVariableContentProvider());
		this.writtenVariableTreeViewer.setLabelProvider(new VariableLabelProvider());
		this.writtenVariableTreeViewer.setInput(writtenVariables);	
		
		setChecks(this.writtenVariableTreeViewer, RW);

		this.writtenVariableTreeViewer.refresh(true);
		
	}

	private void createReadVariableContect(List<VarValue> readVariables) {
		this.readVariableTreeViewer.setContentProvider(new RWVariableContentProvider());
		this.readVariableTreeViewer.setLabelProvider(new VariableLabelProvider());
		this.readVariableTreeViewer.setInput(readVariables);	
		
		setChecks(this.readVariableTreeViewer, RW);

		this.readVariableTreeViewer.refresh(true);
	}

	private void createConsequenceContent(List<GraphDiff> cons) {
		this.consequenceTreeViewer.setContentProvider(new ConsequenceContentProvider());
		this.consequenceTreeViewer.setLabelProvider(new ConsequenceLabelProvider());
		this.consequenceTreeViewer.setInput(cons);	
		
		setChecks(this.consequenceTreeViewer, STATE);
		
		this.consequenceTreeViewer.refresh(true);
	}

	private void createStateContent(BreakPointValue value){
		this.stateTreeViewer.setContentProvider(new VariableContentProvider());
		this.stateTreeViewer.setLabelProvider(new VariableLabelProvider());
		this.stateTreeViewer.setInput(value);	
		
		setChecks(this.stateTreeViewer, STATE);

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
	
	public static final String RW = "rw";
	public static final String STATE = "state";
	
	private void setChecks(CheckboxTreeViewer treeViewer, String type){
		Tree tree = treeViewer.getTree();
		for(TreeItem item: tree.getItems()){
			setChecks(item, type);
		}
	}
	
	private void setChecks(TreeItem item, String type){
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
		
		String varID = ev.getVarID();
		
		if(type.equals(STATE)){
			Trace trace = Activator.getDefault().getCurrentTrace();
			varID = trace.findTrueIDFromStateVariable(varID, currentNode.getOrder());
		}
		
		if(Settings.interestedVariables.contains(varID)){
			item.setChecked(true);
		}
		else{
			item.setChecked(false);
		}

		for(TreeItem childItem: item.getItems()){
			setChecks(childItem, type);
		}
	}
	
	
	class StateListener implements ICheckStateListener{
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			Object obj = event.getElement();
			VarValue value = null;
			
			if(obj instanceof VarValue){
				value = (VarValue)obj;
				String varID = value.getVarID();
				
				Trace trace = Activator.getDefault().getCurrentTrace();
				String trueVarID = trace.findTrueIDFromStateVariable(varID, currentNode.getOrder());
				
				if(!Settings.interestedVariables.contains(trueVarID)){
					Settings.interestedVariables.add(trueVarID);							
				}
				else{
					Settings.interestedVariables.remove(trueVarID);
				}
				
				setChecks(writtenVariableTreeViewer, RW);
				setChecks(readVariableTreeViewer, RW);
				setChecks(stateTreeViewer, STATE);
				
				writtenVariableTreeViewer.refresh();
				readVariableTreeViewer.refresh();
				stateTreeViewer.refresh();	
			}
			
		}
	}
	
	class RWVarListener implements ICheckStateListener{
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			Object obj = event.getElement();
			VarValue value = null;
			
			if(obj instanceof VarValue){
				value = (VarValue)obj;
				String varID = value.getVarID();
				
				if(!Settings.interestedVariables.contains(varID)){
					Settings.interestedVariables.add(varID);							
				}
				else{
					Settings.interestedVariables.remove(varID);
				}
				
				setChecks(writtenVariableTreeViewer, RW);
				setChecks(readVariableTreeViewer, RW);
				setChecks(stateTreeViewer, STATE);
				
				writtenVariableTreeViewer.refresh();
				readVariableTreeViewer.refresh();
				stateTreeViewer.refresh();	
			}
			
		}
	}

	private void addListener() {
//		stateListener = new ICheckStateListener() {
//			@Override
//			public void checkStateChanged(CheckStateChangedEvent event) {
//				Object obj = event.getElement();
//				VarValue value = null;
//				
//				if(obj instanceof VarValue){
//					value = (VarValue)obj;
//				}
//				else if(obj instanceof GraphDiff){
//					GraphDiff diff = (GraphDiff)obj;
//					value = (VarValue)diff.getChangedNode();
//				}
//				String varID = value.getVarID();
//				
//				if(!Settings.interestedVariables.contains(varID)){
//					Settings.interestedVariables.add(varID);							
//				}
//				else{
//					Settings.interestedVariables.remove(varID);
//				}
//				
////				setChecks(consequenceTreeViewer);
//				setChecks(writtenVariableTreeViewer);
//				setChecks(readVariableTreeViewer);
//				setChecks(stateTreeViewer);
//				
//				writtenVariableTreeViewer.refresh();
//				readVariableTreeViewer.refresh();
//				stateTreeViewer.refresh();	
//				
////				consequenceTreeViewer.refresh();	
//			}
//		};
		
		treeListener = new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
//				setChecks(consequenceTreeViewer);
//				setChecks(stateTreeViewer);
//				
//				stateTreeViewer.refresh();	
//				consequenceTreeViewer.refresh();
				
				setChecks(readVariableTreeViewer, RW);
				setChecks(writtenVariableTreeViewer, RW);
				setChecks(stateTreeViewer, STATE);
				
				readVariableTreeViewer.refresh();
				writtenVariableTreeViewer.refresh();
				stateTreeViewer.refresh();	
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				
			}
		};
		
		stateListener = new StateListener();
		RWVarListener = new RWVarListener();
		
		this.readVariableTreeViewer.addTreeListener(treeListener);
		this.writtenVariableTreeViewer.addTreeListener(treeListener);
		this.stateTreeViewer.addTreeListener(treeListener);
		
		this.writtenVariableTreeViewer.addCheckStateListener(RWVarListener);
		this.readVariableTreeViewer.addCheckStateListener(RWVarListener);
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
		feedbackGroup.setText("Are all variables in this step correct?");
		feedbackGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, false));
		feedbackGroup.setLayout(new GridLayout(4, true));

		yesButton = new Button(feedbackGroup, SWT.RADIO);
		yesButton.setText(" Yes");
		yesButton.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, false));
		yesButton.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				feedbackType = DebugFeedbackView.CORRECT;
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
				feedbackType = DebugFeedbackView.INCORRECT;
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		unclearButton = new Button(feedbackGroup, SWT.RADIO);
		unclearButton.setText(" Unclear");
		unclearButton.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, false));
		unclearButton.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				feedbackType = DebugFeedbackView.UNCLEAR;
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});

//		Label holder = new Label(feedbackGroup, SWT.NONE);
//		holder.setText("");

		Button submitButton = new Button(feedbackGroup, SWT.NONE);
		submitButton.setText("Find bug!");
		submitButton
				.setLayoutData(new GridData(SWT.RIGHT, SWT.UP, true, false));
		submitButton.addMouseListener(new MouseListener() {
			
			public void mouseUp(MouseEvent e) {}
			public void mouseDoubleClick(MouseEvent e) {}

			public void mouseDown(MouseEvent e) {
				if (feedbackType == null) {
					MessageBox box = new MessageBox(PlatformUI.getWorkbench()
							.getDisplay().getActiveShell());
					box.setMessage("Please tell me whether this step is correct or not!");
					box.open();
				} else {
					Trace trace = Activator.getDefault().getCurrentTrace();
					TraceNode suspiciousNode = null;
					
					if(!feedbackType.equals(DebugFeedbackView.UNCLEAR)){
						currentNode.setStepCorrectness(TraceNode.STEP_CORRECT);
						
						TraceNode conflictConcerningNode = null;
						if(feedbackType.equals(DebugFeedbackView.INCORRECT)){
							currentNode.setVarsCorrectness(TraceNode.VARS_INCORRECT);
							conflictConcerningNode = currentNode;
						}
						else if(feedbackType.equals(DebugFeedbackView.CORRECT)){
							currentNode.setVarsCorrectness(TraceNode.VARS_CORRECT);
							conflictConcerningNode = lastestNode;
						}
						
						if(conflictConcerningNode != null){
							
							long t1 = System.currentTimeMillis();
							boolean isConflict = trace.checkDomiatorConflicts(conflictConcerningNode.getOrder());;
							long t2 = System.currentTimeMillis();
							System.out.println("time for checkDomiatorConflicts: " + (t2-t1));
							
							if(!isConflict){
								t1 = System.currentTimeMillis();
								trace.distributeSuspiciousness(Settings.interestedVariables);
								t2 = System.currentTimeMillis();
								System.out.println("time for distributeSuspiciousness: " + (t2-t1));
								
								suspiciousNode = trace.findMostSupiciousNode();
							}
							else{
								boolean userConfirm = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
										"Feedback Conflict", "The choice is conflict with your previous feedback, are your sure with your this choice?");
								if(userConfirm){
									suspiciousNode = trace.findOldestConflictNode(conflictConcerningNode.getOrder());
								}
							}
						}
						
						int checkTime = trace.getCheckTime()+1;
						currentNode.setCheckTime(checkTime);
						trace.setCheckTime(checkTime);
					}
					else if(feedbackType.equals(DebugFeedbackView.UNCLEAR)){
						//TODO
						//suspiciousNode = trace.findMoreClearNode();
					}
					
					if(suspiciousNode != null){
						jumpToNode(trace, suspiciousNode);	
					}
				}
			}
			private void jumpToNode(Trace trace, TraceNode suspiciousNode) {
				TraceView view;
				try {
					view = (TraceView)PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage().showView(MicroBatViews.TRACE);
					view.jumpToNode(trace, suspiciousNode.getOrder());
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
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
					VarValue vv = currentNode.getProgramState().findVarValue(parent.getVarID());
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
					VarValue vv = currentNode.getProgramState().findVarValue(parent.getVarID());
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
								return ((PrimitiveValue)after).getManifestationValue();
							}
							else{
								return "NULL";
							}
						case 3: 
							if(before != null){
								return ((PrimitiveValue)before).getManifestationValue();
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
			if(element instanceof VarValue){
				VarValue varValue = (VarValue)element;
				switch(columnIndex){
				case 0: 
					return varValue.getType();
				case 1: 
					String name = varValue.getVarName();
					return name;
				case 2: return varValue.getManifestationValue();
				}
			}
			
//			if(element instanceof ReferenceValue){
//				ReferenceValue value = (ReferenceValue)element;
//				switch(columnIndex){
//				case 0: 
//					if(value.getClassType() != null){
//						return value.getConciseTypeName();						
//					}
//				case 1: 
//					String name = value.getVarName();
//					return name;
//				case 2: return value.getMessageValue();
//				}
//			}
//			else if(element instanceof ArrayValue){
//				ArrayValue value = (ArrayValue)element;
//				switch(columnIndex){
//				case 0: return "array[" + value.getComponentType() + "]";
//				case 1: 
//					String name = value.getVarName();
//					if(value.isRoot() && value.isField()){
//						name = "this." + name;
//					}
//					return name;
//				case 2: return value.getMessageValue();
//				}
//			}
//			else if(element instanceof PrimitiveValue){
//				PrimitiveValue value = (PrimitiveValue)element;
//				switch(columnIndex){
//				case 0: return value.getPrimitiveType();
//				case 1: 
//					String name = value.getVarName();
//					if(value.isRoot() && value.isField()){
//						name = "this." + name;
//					}
//					return name;
//				case 2: return value.getStrVal() + " (id=" + value.getVarID() + ")";
//				}
//			}
//			else if(element instanceof VirtualValue){
//				
//			}
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
			
			if(currentNode != null){
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
