package invariant.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompositeTemplate extends Template {

	public List<List<SingleTemplate>> disj;
	
	public CompositeTemplate(List<List<SingleTemplate>> disj) {
		this.disj = disj;
	}
	
	@Override
	public String toString() {
		String s = "";
		
		Iterator<List<SingleTemplate>> it1 = disj.iterator();
		
		while (it1.hasNext()) {
			List<SingleTemplate> stl = it1.next();
			Iterator<SingleTemplate> it2 = stl.iterator();
			
			while (it2.hasNext()) {
				SingleTemplate st = it2.next();
				s += st;
				if (it2.hasNext()) s += " && ";
			}
			
			if (it1.hasNext()) s += " || ";
		}
		
		return s;
	}
	
	public List<SingleTemplate> getSingleTemplates() {
		List<SingleTemplate> ret = new ArrayList<SingleTemplate>();
		
		Iterator<List<SingleTemplate>> it1 = disj.iterator();
		
		while (it1.hasNext()) {
			List<SingleTemplate> stl = it1.next();
			Iterator<SingleTemplate> it2 = stl.iterator();
			
			while (it2.hasNext()) {
				SingleTemplate st = it2.next();
				ret.add(st);
			}
		}
		
		return ret;
		
	}

}
