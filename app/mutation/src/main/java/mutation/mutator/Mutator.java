package mutation.mutator;

import japa.parser.ast.CompilationUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mutation.io.MutationFileWriter;
import mutation.mutator.MutationVisitor.MutationNode;
import mutation.mutator.insertdebugline.DebugLineInsertion;
import mutation.mutator.insertdebugline.DebugLineInsertionResult;
import mutation.parser.ClassAnalyzer;
import mutation.parser.JParser;
import sav.common.core.utils.BreakpointUtils;
import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/9/15.
 */
public class Mutator implements IMutator {
	
	@Override
	public <T extends ClassLocation> MutationResult mutate(
			Map<String, List<T>> classLocationMap, String sourceFolder) {
		JParser cuParser = new JParser(sourceFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(sourceFolder, cuParser);
		MutationVisitor mutationVisitor = new MutationVisitor(
				new MutationMap(), classAnalyzer);
		MutationResult mutationResult = new MutationResult();
		for (Entry<String, List<T>> entry : classLocationMap.entrySet()) {
			CompilationUnit cu = cuParser.parse(entry.getKey());
			cu.accept(mutationVisitor, true);
			Map<Integer, List<MutationNode>> result = mutationVisitor.getResult();
//			mutationResult.importData(entry, result);
		}
		
		return mutationResult;
	}

	@Override
	public <T extends ClassLocation> Map<String, DebugLineInsertionResult> insertDebugLine(
			Map<String, List<T>> classLocationMap, String srcFolder) {
		JParser cuParser = new JParser(srcFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(srcFolder, cuParser);
		DebugLineInsertion insertion = new DebugLineInsertion();
		insertion.setFileWriter(new MutationFileWriter(srcFolder));
		Map<String, DebugLineInsertionResult> result = new HashMap<String, DebugLineInsertionResult>();
		for (Entry<String, List<T>> entry : classLocationMap.entrySet()) {
			CompilationUnit cu = cuParser.parse(entry.getKey());
			/*
			 * TODO: change behavior or analyzeCompilationUnit or
			 * DebugLineInsertion?
			 */
			insertion.init(entry.getKey(), classAnalyzer
					.analyzeCompilationUnit(cu).get(0), BreakpointUtils
					.extractLineNo(entry.getValue()));
			DebugLineInsertionResult insertResult = insertion.insert(cu);
			result.put(entry.getKey(), insertResult);
		}
		return result;
	}
}
