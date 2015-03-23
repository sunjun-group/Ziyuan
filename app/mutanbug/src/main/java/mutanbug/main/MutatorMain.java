/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.main;

import japa.parser.ast.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mutanbug.commons.utils.FileUtils;
import mutanbug.mutator.Mutator;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.strategies.dto.ClassLocation;
import sav.strategies.mutanbug.IMutator;

/**
 * @author LLT
 *
 */
public class MutatorMain implements IMutator {

	@Override
	public <T extends ClassLocation> Map<T, List<File>> mutate(List<T> locations,
			String srcFolder) {
		Map<String, List<T>> locMap = BreakpointUtils.initBrkpsMap(locations);
		Map<T, List<File>> result = new HashMap<T, List<File>>();
		File tempFolder = FileUtils.createTempFolder("mutatedSource");
		for (Entry<String, List<T>> entry : locMap.entrySet()) {
			result.putAll(mutate(entry.getKey(), entry.getValue(), srcFolder,
					tempFolder));
		}
		return result;
	}
	
	private <T extends ClassLocation> Map<T, List<File>> mutate(String className, List<T> locations, String srcFolder,
			File outputFolder) {
		Mutator mutator = new Mutator();
		File javaFile = new File(ClassUtils.getJFilePath(srcFolder, className));
		Map<Integer, T> locMap = new HashMap<Integer, T>();
		for (T loc : locations) {
			locMap.put(loc.getLineNo(), loc);
		}
		Map<Node, List<Node>> mutations = mutator.mutateFile(javaFile, null, locMap.keySet());
		Map<T, List<File>> result = new HashMap<T, List<File>>();
		try {
			List<?> orgs = org.apache.commons.io.FileUtils.readLines(javaFile);
			for (Node node : mutations.keySet()) {
				int beginLine = node.getBeginLine() - 1;
				String folderName = beginLine + 1 + "_"
						+ node.getBeginColumn();
				List<Node> mutatedNodes = mutations.get(node);
				List<Object> newContent = new ArrayList<Object>(orgs);
				String mutationLine = (String) newContent.get(beginLine);
				List<File> files = new ArrayList<File>(mutatedNodes.size());
				for (int i = 0; i < mutatedNodes.size(); i++) {
					Node mutatedNode = mutatedNodes.get(i);
					File verFolder = FileUtils.createFolder(outputFolder,
							folderName + i + "");
					File muFile = new File(verFolder, javaFile.getName());
					newContent.set(beginLine, mutationLine.replace(node.toString(), mutatedNode.toString()));
					org.apache.commons.io.FileUtils.writeLines(muFile, newContent);
					files.add(muFile);
				}
				result.put(locMap.get(beginLine + 1), files);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}		
		return result;
	}

	
	public static void main(String[] args) throws IOException {
		String srcFolder = "D:/_1_Projects/Tzuyu/workspace/trunk/app/sav.commons/src/test/java/sav/commons/testdata/";
		List<ClassLocation> locs = new ArrayList<ClassLocation>();
		locs.add(new ClassLocation("SamplePrograms", "Max", 10)); 
		locs.add(new ClassLocation("SamplePrograms", "Max", 13));
		locs.add(new ClassLocation("SamplePrograms", "Max", 31));
		MutatorMain main = new MutatorMain();
		main.mutate(locs, srcFolder);
	}

	
}
