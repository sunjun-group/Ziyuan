package microbat.model.trace;

import java.util.ArrayList;
import java.util.List;

public class PotentialCorrectPatternList {
	private List<PotentialCorrectPattern> patternList = new ArrayList<>();

	public List<PotentialCorrectPattern> getPatternList() {
		return patternList;
	}

	public void setPatternList(List<PotentialCorrectPattern> patternList) {
		this.patternList = patternList;
	}
	
	public void addPattern(PotentialCorrectPattern pattern){
		if(!patternList.contains(pattern)){
			patternList.add(pattern);
		}
	}
}
