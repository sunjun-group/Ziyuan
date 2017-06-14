package learntest.preference;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import learntest.Activator;
import learntest.main.LearnTestConfig;

public class LearnTestPreference extends PreferencePage implements IWorkbenchPreferencePage {

	private Combo projectCombo;
	private Text testClassText;
	private Text testMethodText;
	private Button isL2TButton;
	private Text methodLineNumberText;
	
	private String defaultTargetProject = "";
	private String defaultTestClass = "";
	private String defaultTestMethod = "";
	private String defaultL2T = "";
	private String defaultMethodLineNumber = "";
	
	public static final String TARGET_PORJECT = "targetProjectName";
	public static final String CLASS_NAME = "className";
	public static final String METHOD_NAME = "methodName";
	public static final String IS_L2T = "isL2T";
	public static final String METHOD_LINE_NUMBER = "methodLineNumber";
	
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
		this.defaultTargetProject = Activator.getDefault().getPreferenceStore().getString(TARGET_PORJECT);
		this.defaultTestClass = Activator.getDefault().getPreferenceStore().getString(CLASS_NAME);
		this.defaultTestMethod = Activator.getDefault().getPreferenceStore().getString(METHOD_NAME);
		this.defaultL2T = Activator.getDefault().getPreferenceStore().getString(IS_L2T);
		if(this.defaultL2T == null){
			this.defaultL2T = "true";
		}
		
		this.defaultMethodLineNumber = Activator.getDefault().getPreferenceStore().getString(METHOD_LINE_NUMBER);
		if(this.defaultMethodLineNumber == null){
			this.defaultMethodLineNumber = "0";
		}
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
		
		Label methodNameLabel = new Label(testInfoGroup, SWT.NONE);
		methodNameLabel.setText("Test Method: ");
		testMethodText = new Text(testInfoGroup, SWT.BORDER);
		testMethodText.setText(this.defaultTestMethod);
		GridData methodNameTextData = new GridData(SWT.FILL, SWT.FILL, true, false);
		methodNameTextData.horizontalSpan = 2;
		testMethodText.setLayoutData(methodNameTextData);
		
		Label methodLineNumberLabel = new Label(testInfoGroup, SWT.NONE);
		methodLineNumberLabel.setText("Method Line Number: ");
		methodLineNumberText = new Text(testInfoGroup, SWT.BORDER);
		methodLineNumberText.setText(this.defaultMethodLineNumber);
		GridData methodLineNumberData = new GridData(SWT.FILL, SWT.FILL, true, false);
		methodLineNumberData.horizontalSpan = 2;
		methodLineNumberText.setLayoutData(methodLineNumberData);
		
		Label isL2TLabel = new Label(testInfoGroup, SWT.NONE);
		isL2TLabel.setText("Use L2T: ");
		isL2TButton = new Button(testInfoGroup, SWT.CHECK);
		isL2TButton.setSelection(Boolean.valueOf(this.defaultL2T));
		GridData isL2TData = new GridData(SWT.FILL, SWT.FILL, true, false);
		isL2TData.horizontalSpan = 2;
		isL2TButton.setLayoutData(isL2TData);
	}
	
	
	
	public boolean performOk(){
		String isL2TString = String.valueOf(this.isL2TButton.getSelection());
		
		IEclipsePreferences preferences = ConfigurationScope.INSTANCE.getNode("learntest.preference");
		preferences.put(TARGET_PORJECT, this.projectCombo.getText());
		preferences.put(CLASS_NAME, this.testClassText.getText());
		preferences.put(METHOD_NAME, this.testMethodText.getText());
		preferences.put(IS_L2T, isL2TString);
		preferences.put(METHOD_LINE_NUMBER, this.methodLineNumberText.getText());
		
		Activator.getDefault().getPreferenceStore().putValue(TARGET_PORJECT, this.projectCombo.getText());
		Activator.getDefault().getPreferenceStore().putValue(CLASS_NAME, this.testClassText.getText());
		Activator.getDefault().getPreferenceStore().putValue(METHOD_NAME, this.testMethodText.getText());
		Activator.getDefault().getPreferenceStore().putValue(IS_L2T, isL2TString);
		Activator.getDefault().getPreferenceStore().putValue(METHOD_LINE_NUMBER, this.methodLineNumberText.getText());
		
		confirmChanges();
		
		return true;
		
	}
	
	private void confirmChanges(){
		LearnTestConfig.projectName = this.projectCombo.getText();
		LearnTestConfig.targetClassName = this.testClassText.getText();
		LearnTestConfig.targetMethodName = this.testMethodText.getText();
		LearnTestConfig.isL2TApproach = this.isL2TButton.getSelection();
		LearnTestConfig.targetMethodLineNum = this.methodLineNumberText.getText();
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
