package invariant.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeTemplate extends Template {

	public List<SingleTemplate> templates;
	
	public CompositeTemplate() {
		templates = new ArrayList<SingleTemplate>();
	}
	
	public void addTemplates(SingleTemplate... newTemplates) {
		templates.addAll(new ArrayList<SingleTemplate>(Arrays.asList(newTemplates)));
	}
	
	public boolean check() {
		return false;
	}
	
}
