package microbat.views;

import icsetlv.common.dto.BreakPointValue;
import icsetlv.trial.model.TraceNode;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
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

import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ReferenceValue;


public class DebugFeedbackView extends ViewPart {

	private Boolean isCorrect;
	
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String STATE = "state";
	
	private Tree inputTree;
	private Tree outputTree;
	private Tree stateTree;
	
	private Button yesButton;
	private Button noButton;
	
	public DebugFeedbackView() {
	}
	
	public void refresh(TraceNode node){
		BreakPointValue state = node.getProgramState();
		
		createVariableViewContent(inputTree, state);
		createVariableViewContent(outputTree, state);
		createVariableViewContent(stateTree, state);
		
		yesButton.setSelection(false);
		noButton.setSelection(false);
		isCorrect = null;
	}
	
	private void createVariableViewContent(Tree tree, BreakPointValue value){
		TreeViewer viewer = new TreeViewer(tree);
		viewer.setContentProvider(new VariableContentProvider());
		viewer.setLabelProvider(new VariableLabelProvider());
		viewer.setInput(value);
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

		createVarGroup(variableForm, "Input Variable: ", INPUT);
		createVarGroup(variableForm, "Output Variable: ", OUTPUT);
		createVarGroup(variableForm, "State Variable: ", STATE);

		variableForm.setWeights(new int[] { 3, 3, 4 });
	}

	private void createVarGroup(SashForm variableForm, String groupName, String type) {
		Group varGroup = new Group(variableForm, SWT.NONE);
		varGroup.setText(groupName);
		varGroup.setLayout(new FillLayout());

		Tree tree = new Tree(varGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeColumn typeColumn = new TreeColumn(tree, SWT.LEFT);
		typeColumn.setAlignment(SWT.LEFT);
		typeColumn.setText("Variable Type");
		typeColumn.setWidth(100);
		
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setAlignment(SWT.LEFT);
		nameColumn.setText("Variable Name");
		nameColumn.setWidth(150);
		
		TreeColumn valueColumn = new TreeColumn(tree, SWT.LEFT);
		valueColumn.setAlignment(SWT.LEFT);
		valueColumn.setText("Variable Value");
		valueColumn.setWidth(300);

		if(type.equals(INPUT)){
			this.inputTree = tree;
		}
		else if(type.equals(OUTPUT)){
			this.outputTree = tree;
		}
		else if(type.equals(STATE)){
			this.stateTree = tree;
		}
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
				case 0: return value.getClassType().name();
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
}
