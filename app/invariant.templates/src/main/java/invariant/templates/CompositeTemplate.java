package invariant.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeTemplate extends Template {

	public List<Template> templates;
	
	public CompositeTemplate() {
		templates = new ArrayList<Template>();
	}
	
	public void addTemplates(Template... newTemplates) {
		templates.addAll(new ArrayList<Template>(Arrays.asList(newTemplates)));
	}
	
	public boolean check() {
		return false;
	}
	
}
