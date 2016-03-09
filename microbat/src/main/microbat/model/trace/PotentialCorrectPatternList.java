package microbat.model.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.model.trace.PathInstance.SourceLine;

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

	public List<PathInstance> findSimilarIterationPath(PathInstance path) {
		List<PathInstance> list = new ArrayList<>();
		for(PotentialCorrectPattern pattern: this.patterns.values()){
			PathInstance labelPath = pattern.getLabelInstance();
			SourceLine patternStartLine = path.new SourceLine(labelPath.getStartNode().getClassCanonicalName(), labelPath.getStartNode().getLineNumber());
			SourceLine patternEndLine = path.new SourceLine(labelPath.getEndNode().getClassCanonicalName(), labelPath.getEndNode().getLineNumber());
			
			SourceLine pathStartLine = path.new SourceLine(path.getStartNode().getClassCanonicalName(), path.getStartNode().getLineNumber());
			SourceLine pathEndLine = path.new SourceLine(path.getEndNode().getClassCanonicalName(), path.getEndNode().getLineNumber());
			
			if(patternStartLine.equals(pathStartLine) &&
					patternEndLine.equals(pathEndLine)){
				list.add(labelPath);
			}
		}
		
		return list;
	}

	public List<PotentialCorrectPattern> findPatternsWithEndNode(SourceLine line) {
		List<PotentialCorrectPattern> patterns = new ArrayList<>();
		for(PotentialCorrectPattern pattern: this.patterns.values()){
			PathInstance labelPath = pattern.getLabelInstance();
			TraceNode node = labelPath.getEndNode();
			SourceLine endLine = new PathInstance().new SourceLine(node.getClassCanonicalName(), node.getLineNumber());
			
			if(line.equals(endLine)){
				patterns.add(pattern);
			}
		}
		
		return patterns;
	}

	public PotentialCorrectPatternList clone(){
		PotentialCorrectPatternList clonedPatterns = new PotentialCorrectPatternList();
		
		for(String key: patterns.keySet()){
			PotentialCorrectPattern pattern = patterns.get(key);
			PotentialCorrectPattern clonedPattern = pattern.clone();
			
			clonedPatterns.getPatterns().put(key, clonedPattern);
		}
		
		return clonedPatterns;
	}
}
