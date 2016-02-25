package microbat.views;

import microbat.Activator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ImageUI {

	public static final String CHECK_MARK = "icons/check_mark.png";
	public static final String QUESTION_MARK = "icons/question_mark.png";

	private Image checkMarkImage;
	private Image questionMarkImage;
	
	public Image getCheckMarkImage(){
		if(checkMarkImage==null){
			ImageDescriptor image = Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.CHECK_MARK);
			checkMarkImage = image.createImage();		
		}
		return checkMarkImage;
	}
	
	public Image getQuestionMarkImage(){
		if(questionMarkImage==null){
			ImageDescriptor image = Activator.getDefault().getImageRegistry().getDescriptor(ImageUI.QUESTION_MARK);
			questionMarkImage = image.createImage();		
		}
		return questionMarkImage;
	}
	
}
