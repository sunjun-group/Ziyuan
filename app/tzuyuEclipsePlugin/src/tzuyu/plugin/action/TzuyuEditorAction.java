package tzuyu.plugin.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class TzuyuEditorAction extends TzuyuAction implements
		IEditorActionDelegate {
	private IEditorPart editor;
	
	@Override
	public void run(IAction action) {
		if (editor != null	) {
			openDialog("run Tzuyu");
		} else if (selection instanceof TextSelection) {
		} 
			
	}
	
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

}
