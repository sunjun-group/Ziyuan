package learntest.preference;

import java.awt.Button;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class LearnTestPreference extends PreferencePage implements IWorkbenchPreferencePage {

	private Combo projectCombo;
	private Text testClassText;
	private Text testMethodText;
	
	private String defaultTargetProject = "";
	private String defaultTestClass = "";
	private String defaultTestMethod = "";
	
	public LearnTestPreference() {
	}

	public LearnTestPreference(String title) {
		super(title);
	}

	public LearnTestPreference(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		composite.setLayout(layout);
		
		Label projectLabel = new Label(composite, SWT.NONE);
		projectLabel.setText("Target Project");
		
		projectCombo = new Combo(composite, SWT.BORDER);
		projectCombo.setItems(getProjectsInWorkspace());
		projectCombo.setText(this.defaultTargetProject);
		GridData comboData = new GridData(SWT.FILL, SWT.FILL, true, false);
		comboData.horizontalSpan = 2;
		projectCombo.setLayoutData(comboData);
		
		createTestInfoGroup(composite);
		
		return composite;
	}
	
	private void createTestInfoGroup(Composite parent){
		Group testInfoGroup = new Group(parent, SWT.NONE);
		testInfoGroup.setText("Test Configuration");
		GridData seedStatementGroupData = new GridData(SWT.FILL, SWT.FILL, true, true);
		seedStatementGroupData.horizontalSpan = 3;
		testInfoGroup.setLayoutData(seedStatementGroupData);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		testInfoGroup.setLayout(layout);
		
		Label testClassLabel = new Label(testInfoGroup, SWT.NONE);
		testClassLabel.setText("Test Class: ");
		testClassText = new Text(testInfoGroup, SWT.BORDER);
		testClassText.setText(this.defaultTestClass);
		GridData lanuchClassTextData = new GridData(SWT.FILL, SWT.FILL, true, false);
		lanuchClassTextData.horizontalSpan = 2;
		testClassText.setLayoutData(lanuchClassTextData);
		
		Label classNameLabel = new Label(testInfoGroup, SWT.NONE);
		classNameLabel.setText("Class Name: ");
		testMethodText = new Text(testInfoGroup, SWT.BORDER);
		testMethodText.setText(this.defaultTestMethod);
		GridData methodNameTextData = new GridData(SWT.FILL, SWT.FILL, true, false);
		methodNameTextData.horizontalSpan = 2;
		testMethodText.setLayoutData(methodNameTextData);
//		
//		Label lineNumberLabel = new Label(seedStatementGroup, SWT.NONE);
//		lineNumberLabel.setText("Line Number: ");
//		lineNumberText = new Text(seedStatementGroup, SWT.BORDER);
//		lineNumberText.setText(this.defaultLineNumber);
//		GridData lineNumTextData = new GridData(SWT.FILL, SWT.FILL, true, false);
//		lineNumTextData.horizontalSpan = 2;
//		lineNumberText.setLayoutData(lineNumTextData);
		
	}
	
	private String[] getProjectsInWorkspace(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		String[] projectStrings = new String[projects.length];
		for(int i=0; i<projects.length; i++){
			projectStrings[i] = projects[i].getName();
		}
		
		return projectStrings;
	}

}
