package mutation.mutator;

import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mutation.io.DebugLineFileWriter;
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
	public <T extends ClassLocation> Map<String, MutationResult> mutate(
			List<T> locs, String srcFolder) {
		Map<String, List<Integer>> classLocationMap = BreakpointUtils.initLineNoMap(locs);
		JParser cuParser = new JParser(srcFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(srcFolder, cuParser);
		MutationVisitor mutationVisitor = new MutationVisitor(
				new MutationMap(), classAnalyzer);
		Map<String, MutationResult> result = new HashMap<String, MutationResult>();
		MutationFileWriter fileWriter = new MutationFileWriter(srcFolder);
		for (Entry<String, List<Integer>> entry : classLocationMap.entrySet()) {
			String className = entry.getKey();
			mutationVisitor
					.reset(classAnalyzer.analyzeCompilationUnit(
							cuParser.parse(className)).get(0), entry.getValue());
			CompilationUnit cu = cuParser.parse(className);
			cu.accept(mutationVisitor, true);
			Map<Integer, List<MutationNode>> muRes = mutationVisitor.getResult();
			MutationResult lineRes = new MutationResult(className);
			for (Entry<Integer, List<MutationNode>> lineData : muRes.entrySet()) {
				Integer line = lineData.getKey();
				List<File> muFiles = fileWriter.write(lineData.getValue(), className, line);
				lineRes.put(line, muFiles);
			}
			result.put(className, lineRes);
		}
		
		return result;
	}

	@Override
	public <T extends ClassLocation> Map<String, DebugLineInsertionResult> insertDebugLine(
			Map<String, List<T>> classLocationMap, String srcFolder) {
		JParser cuParser = new JParser(srcFolder, classLocationMap.keySet());
		ClassAnalyzer classAnalyzer = new ClassAnalyzer(srcFolder, cuParser);
		DebugLineInsertion insertion = new DebugLineInsertion();
		insertion.setFileWriter(new DebugLineFileWriter(srcFolder));
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
