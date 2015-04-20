package mutation.mutator;

import japa.parser.ast.CompilationUnit;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mutation.mutator.MutationVisitor.MutationNode;
import mutation.parser.ClassAnalyzer;
import mutation.parser.JParser;
import sav.common.core.utils.BreakpointUtils;
import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/9/15.
 */
public class Mutator implements IMutator {
	
	@Override
	public MutationResult mutate(List<ClassLocation> locations,
			String sourceFolder) {
		Map<String, List<ClassLocation>> classLocationMap = BreakpointUtils
				.initBrkpsMap(locations);
		JParser cuParser = new JParser(sourceFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(sourceFolder, cuParser);
		MutationVisitor mutationVisitor = new MutationVisitor(new MutationMap());
		MutationResult mutationResult = new MutationResult();
		for (Entry<String, List<ClassLocation>> entry : classLocationMap.entrySet()) {
			CompilationUnit cu = cuParser.parse(entry.getKey());
			cu.accept(mutationVisitor, true);
			Map<Integer, List<MutationNode>> result = mutationVisitor.getResult();
			mutationResult.importData(entry, result);
		}
		
		return mutationResult;
	}

	@Override
	public MutationResult insertFakeLine(List<ClassLocation> locations,
			String sourcFolder) {
		return null;
	}
}
