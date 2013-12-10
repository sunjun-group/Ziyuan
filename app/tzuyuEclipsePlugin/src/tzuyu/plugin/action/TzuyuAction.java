package tzuyu.plugin.action;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import tzuyu.plugin.constant.JElementType;

public class TzuyuAction implements IObjectActionDelegate {
	// current selection
	protected ISelection selection;

	protected void openDialog(String msg) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		TitleAreaDialog dialog = new TitleAreaDialog(shell);
		dialog.create();
		dialog.setMessage(msg);
		dialog.open();
	}

	@Override
	public void run(IAction action) {
		if (selection == null || selection.isEmpty()) {
			openDialog("selection is null or empty");
		} else if (selection instanceof IStructuredSelection) {
			IJavaElement firstSelectedElement = (IJavaElement) ((IStructuredSelection)selection).getFirstElement();
			int elementType = firstSelectedElement.getElementType();
			for (JElementType type : JElementType.values()) {
				if (type.getIJeType() == elementType) {
					openDialog("You have just selected a " + type.name());
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}
}
