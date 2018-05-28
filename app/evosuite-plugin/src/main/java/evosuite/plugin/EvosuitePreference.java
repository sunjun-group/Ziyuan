package evosuite.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sav.common.core.utils.FileUtils;
import sav.eclipse.plugin.IProjectUtils;
import sav.eclipse.plugin.IResourceUtils;
import sav.eclipse.plugin.SWTFactory;
import sav.eclipse.plugin.WorkbenchUtils;

public class EvosuitePreference extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String EVOSUITE_WORKING_DIR = "evosuite.base.dir";
	private static final String TARGET_PROJECT = "evosuite.target.project";
	private static final String TARGET_METHODS_FILE_PATH = "evosuite.target.methods";
	
	private Combo projectCombo;
	private Text evosuiteWorkingDirText;
	private Button evosuiteWorkingDirBrowser;
	private Text targetMethodsFileText;
	private Button targetMethodsFileBrowser;

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite contents= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		SWTFactory.createLabel(contents, "Target project: ");
		projectCombo = SWTFactory.creatDropdown(contents);
		projectCombo.setItems(WorkbenchUtils.getProjectsInWorkspace());
		SWTFactory.horizontalSpan(projectCombo, 2);
		
		SWTFactory.createLabel(contents, "Evosuite Working Dir: ");
		evosuiteWorkingDirText = new Text(contents, SWT.BORDER);
		GridData workingDirLayerTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
		evosuiteWorkingDirText.setLayoutData(workingDirLayerTextData);
		
		evosuiteWorkingDirBrowser = SWTFactory.createBtn(contents, "Browse");
		
		SWTFactory.createLabel(contents, "Target methods file: ");
		targetMethodsFileText = new Text(contents, SWT.BORDER);
		GridData targetMethodsTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
		targetMethodsFileText.setLayoutData(targetMethodsTextData);
		
		targetMethodsFileBrowser = SWTFactory.createBtn(contents, "Browse");
		
		setDefaultValue();
		registerListener();
		return contents;
	}

	private String projectPath;
	private void registerListener() {
		projectCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String projectName = projectCombo.getText();
				if (projectName != null) {
					IProject project = IProjectUtils.getProject(projectName);
					projectPath = IResourceUtils.relativeToAbsolute(project.getFullPath()).toOSString();
					evosuiteWorkingDirText.setText(FileUtils.getFilePath(projectPath, "evosuite"));
					targetMethodsFileText.setText(FileUtils.getFilePath(projectPath, "targetMethods.txt"));
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		evosuiteWorkingDirBrowser.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(WorkbenchUtils.getActiveWorkbenchWindow(EvosuitePlugin.getDefault()).getShell(), SWT.OPEN);
				dialog.setFilterPath(projectPath);
				String result = dialog.open();
				evosuiteWorkingDirText.setText(result);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		targetMethodsFileBrowser.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(
						WorkbenchUtils.getActiveWorkbenchWindow(EvosuitePlugin.getDefault()).getShell(), SWT.OPEN);
				dialog.setFilterPath(projectPath);
				dialog.setFilterExtensions(new String[] { "*.txt" });
				String result = dialog.open();
				targetMethodsFileText.setText(result);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}

	private void setDefaultValue() {
		EvosuitePreferenceData data = getEvosuitePreferenceData();
		projectCombo.setText(data.getProjectName());
		evosuiteWorkingDirText.setText(data.getEvosuiteWorkingDir());
		targetMethodsFileText.setText(data.getTargetMethodListFile());
	}

	public static EvosuitePreferenceData getEvosuitePreferenceData() {
		IPreferenceStore pref = EvosuitePlugin.getDefault().getPreferenceStore();
		EvosuitePreferenceData data = new EvosuitePreferenceData();
		data.setEvosuiteWorkingDir(pref.getString(EVOSUITE_WORKING_DIR));
		data.setProjectName(pref.getString(TARGET_PROJECT));
		data.setTargetMethodListFile(pref.getString(TARGET_METHODS_FILE_PATH));
		return data;
	}
	
	@Override
	public boolean performOk() {
		IPreferenceStore pref = EvosuitePlugin.getDefault().getPreferenceStore();
		pref.setValue(EVOSUITE_WORKING_DIR, evosuiteWorkingDirText.getText());
		pref.setValue(TARGET_PROJECT, projectCombo.getText());
		pref.setValue(TARGET_METHODS_FILE_PATH, targetMethodsFileText.getText());
		return true;
	}
}
