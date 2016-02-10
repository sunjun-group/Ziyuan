package microbat.model.trace;

import java.util.HashMap;
import java.util.Map;

public class PotentialCorrectPatternList {
	
	private Map<String, PotentialCorrectPattern> patterns = new HashMap<>();

	public Map<String, PotentialCorrectPattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(Map<String, PotentialCorrectPattern> patterns) {
		this.patterns = patterns;
	}

	public void addPathForPattern(PathInstance path){
		String pathKey = path.getPathKey();
		
		PotentialCorrectPattern pattern = patterns.get(pathKey);
		if(pattern == null){
			pattern = new PotentialCorrectPattern();
			patterns.put(pathKey, pattern);
		}
		
		pattern.addPathInstance(path);
	}

	public boolean containsPattern(PathInstance path) {
		return patterns.containsKey(path.getPathKey());
	}

	public PotentialCorrectPattern getPattern(PathInstance path) {
		return patterns.get(path.getPathKey());
	}

	public void clear() {
		this.patterns.clear();
	}

	
}
