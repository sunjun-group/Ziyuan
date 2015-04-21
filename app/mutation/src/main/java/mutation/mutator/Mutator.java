package mutation.mutator;

import japa.parser.ast.CompilationUnit;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mutation.mutator.MutationVisitor.MutationNode;
import mutation.mutator.insertdebugline.DebugLineInsertion;
import mutation.mutator.insertdebugline.DebugLineResult;
import mutation.parser.ClassAnalyzer;
import mutation.parser.JParser;
import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/9/15.
 */
public class Mutator implements IMutator {
	
	@Override
	public MutationResult mutate(Map<String, List<ClassLocation>> classLocationMap,
			String sourceFolder) {
		JParser cuParser = new JParser(sourceFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(sourceFolder, cuParser);
		MutationVisitor mutationVisitor = new MutationVisitor(
				new MutationMap(), classAnalyzer);
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
	public MutationResult insertDebugLine(Map<String, List<ClassLocation>> classLocationMap,
			String sourceFolder) {
		JParser cuParser = new JParser(sourceFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(sourceFolder, cuParser);
		DebugLineInsertion insertion = new DebugLineInsertion();
		MutationResult mutationResult = new MutationResult();
		for (Entry<String, List<ClassLocation>> entry : classLocationMap.entrySet()) {
			CompilationUnit cu = cuParser.parse(entry.getKey());
			/*
			 * TODO: change behavior or analyzeCompilationUnit or DebugLineInsertion?
			 */
			insertion.reset(classAnalyzer.analyzeCompilationUnit(cu).get(0), entry.getValue());
			List<DebugLineResult> result = insertion.insert(cu);
			mutationResult.importData(result);
		}
		return mutationResult;
	}
}
