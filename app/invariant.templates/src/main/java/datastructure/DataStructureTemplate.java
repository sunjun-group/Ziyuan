package datastructure;

import java.util.ArrayList;
import java.util.List;

public class DataStructureTemplate {
	
	public String name;
	
	public int heapArgc;
	
	public int pureArgc;
	
	public List<String> pureProperties;
	
	public DataStructureTemplate(String name, int heapArgc, int pureArgc) {
		this.name = name;
		this.heapArgc = heapArgc;
		this.pureArgc = pureArgc;
		this.pureProperties = new ArrayList<String>();
	}
	
	public void addProperty(String prop) {
		pureProperties.add(prop);
	}
	
	public String toString() {
		return name;
	}

}
