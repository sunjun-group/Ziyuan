package tzuyu.plugin.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import tzuyu.plugin.core.dto.RunConfiguration;
import tzuyu.plugin.core.dto.WorkObject;

public class TzuyuEditorAction extends TzuyuAction<RunConfiguration> implements
		IEditorActionDelegate {
	private IEditorPart editor;
	
	@Override
	protected void run(WorkObject workObject, RunConfiguration config) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	@Override
	protected RunConfiguration initConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}


}
