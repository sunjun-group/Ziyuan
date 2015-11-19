package microbat.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class DebugFeedbackView extends ViewPart {

	private Boolean isCorrect;
	
	public DebugFeedbackView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout parentLayout = new GridLayout(1, true);
		parent.setLayout(parentLayout);
		
		createSubmitGroup(parent);
	}
	
	private void createSubmitGroup(Composite parent){
		Group feedbackGroup = new Group(parent, SWT.NONE);
		feedbackGroup.setText("Is this step correct?");
		feedbackGroup.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, false));
		feedbackGroup.setLayout(new GridLayout(2, true));
		
		Button radioButton1 = new Button(feedbackGroup, SWT.RADIO);
		radioButton1.setText(" Yes");
		radioButton1.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, false));
		radioButton1.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {}
			
			public void mouseDown(MouseEvent e) {
				isCorrect = true;
			}
			
			public void mouseDoubleClick(MouseEvent e) {}
		});
		
		Button radioButton2 = new Button(feedbackGroup, SWT.RADIO);
		radioButton2.setText(" No");
		radioButton2.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, false));
		radioButton2.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {}
			
			public void mouseDown(MouseEvent e) {
				isCorrect = false;
			}
			
			public void mouseDoubleClick(MouseEvent e) {}
		});
		
		Label holder = new Label(feedbackGroup, SWT.NONE);
		holder.setText("");
		
		Button submitButton = new Button(feedbackGroup, SWT.NONE);
		submitButton.setText("submit");
		submitButton.setLayoutData(new GridData(SWT.RIGHT, SWT.UP, true, false));
		submitButton.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {}
			
			public void mouseDown(MouseEvent e) {
				if(isCorrect == null){
					MessageBox box = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell());
					box.setMessage("Please tell me whether this step is correct or not!");
					box.open();
				}
				else{
					//TODO start recommendation
				}
			}
			
			public void mouseDoubleClick(MouseEvent e) {}
		});
	}

	@Override
	public void setFocus() {

	}

}
