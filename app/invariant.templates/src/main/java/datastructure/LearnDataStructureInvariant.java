package datastructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class Edge {
	
	public int pass;
	
	public int fail;
	
	public boolean isCut;
	
	public Edge(int pass, int fail) {
		this.pass = pass;
		this.fail = fail;
		this.isCut = false;
	}
	
	public String toString() {
		return "pass = " + pass + "; fail = " + fail + "; isCut = " + isCut;
	}
	
}

class Region {
	
	public List<Integer> comb;
	
	public List<Integer> coverIndexes;
	
	public Region(List<Integer> comb) {
		this.comb = comb;
		coverIndexes = new ArrayList<Integer>();
	}
	
	public void addCover(int i) {
		coverIndexes.add(i);
	}
	
	public boolean contains(Region r) {
		if (coverIndexes.size() <= r.coverIndexes.size())
			return false;
		
		for (int i : r.coverIndexes) {
			if (!coverIndexes.contains(i))
				return false;
		}
		
		return true;
	}
	
	public String toString() {
		return comb.toString();
	}
	
}

public class LearnDataStructureInvariant {
	
	private List<Boolean> testResults;
	
	private String path;
	
	private List<Edge> edges;
	
	private int numEdges;
	
	private int[][] matrix;
	
	public LearnDataStructureInvariant(List<Boolean> testResults, String path) {
		this.testResults = testResults;
		this.path = path;
	}
	
	private void buildEdges() {
		edges = new ArrayList<Edge>();
		
		for (int i = 0; i < testResults.size(); i++) {
			if (testResults.get(i)) {
				for (int j = 0; j < testResults.size(); j++) {
					if (!testResults.get(j)) {
						edges.add(new Edge(i, j));
					}
				}
			}
		}
		
		numEdges = edges.size();
	}
	
	private void buildMatrix(int col, String name) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(name));
			
			int row = 0;
			
			String s = br.readLine();
			while (s != null) {
				matrix[row++][col] = Integer.parseInt(s);
				s = br.readLine();
			}
		} catch (Exception e) {
			
		}
	}
	
	private boolean removeRedundant() {
		List<Integer> keepRow = new ArrayList<Integer>();
		
//		for (int fstRow = 0; fstRow <= matrix.length - 2; fstRow++) {
		for (int fstRow = matrix.length - 1; fstRow >= 1; fstRow--) {
			boolean isDupRow = false;
			int dupRow = -1;
			
//			for (int sndRow = fstRow + 1; sndRow <= matrix.length - 1; sndRow++) {
			for (int sndRow = fstRow - 1; sndRow >= 0; sndRow--) {
				boolean isEqualAll = true;
				
				for (int col = 0; col < matrix[0].length; col++) {
					if (matrix[fstRow][col] != matrix[sndRow][col]) {
						isEqualAll = false;
						break;
					}
				}
				
				if (isEqualAll) {
					isDupRow = true;
					dupRow = sndRow;
					break;
				}
			}
			
			if (!isDupRow) {
				keepRow.add(0, fstRow);
			} else {
				if (testResults.get(fstRow) != testResults.get(dupRow)) {
					System.out.println(fstRow);
					System.out.println(dupRow);
					return false;
				}
			}
		}
		
		keepRow.add(0, 0);
//		keepRow.add(matrix.length - 1);
		
//		Collections.reverse(keepRow);
		
//		System.out.println(keepRow.toString());
		
		int i = 0;
		
		Iterator<Boolean> it = testResults.iterator();
		while (it.hasNext()) {
			it.next();
			if (!keepRow.contains(i)) it.remove();
			i++;
		}
		
		int[][] tmp = new int[keepRow.size()][matrix[0].length];
		
		i = 0;
		for (int row : keepRow) {
			for (int col = 0; col < matrix[0].length; col++) {
				tmp[i][col] = matrix[row][col];
			}
			i++;
		}
		
		matrix = tmp;
		
		return true;
	}
	
	private void buildMatrix() {
		File folder = new File(path);
//		File[] files = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));
		File[] files = folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		matrix = new int[testResults.size()][files.length];
		
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].toString();
			buildMatrix(i, fileName);
		}
	}
	
	private List<Integer> chooseCols() {
		List<Integer> cols = new ArrayList<Integer>();
		
		while (numEdges > 0) {
			int maxCut = 0, maxCol = -1;
			
			for (int col = 0; col < matrix[0].length; col++) {
				int colCut = 0;
				
				for (Edge edge : edges) {
					if (!edge.isCut && matrix[edge.pass][col] == 1 &&
							matrix[edge.fail][col] == 0) colCut++;
				}
				
				if (colCut > maxCut) {
					maxCut = colCut;
					maxCol = col;  
				}
			}
			
			if (maxCol != -1) {
//				System.out.println(maxCol);
//				System.out.println(maxCut);
				
				cols.add(maxCol);
				numEdges -= maxCut;
								
				for (Edge edge : edges) {
					if (!edge.isCut && matrix[edge.pass][maxCol] == 1 &&
							matrix[edge.fail][maxCol] == 0)
						edge.isCut = true;
				}
			} else return new ArrayList<Integer>();
		}
		
		return cols;
	}
	
	private boolean isSatisfied(Region region, List<Integer> failIndexes) {
		for (int i = 0; i < testResults.size(); i++) {
			if (failIndexes.contains(i)) {
				boolean isFail = false;
				
				for (int j : region.comb) {
					if (matrix[i][j] == 0) {
						isFail = true;
						break;
					}
				}
				
				if (!isFail) return false;
			}
		}
		
		return true;
	}
	
	private boolean isImproved(Region region, List<Integer> passIndexes,
			List<Integer> coveredIndexes) {
		boolean isImproved = false;
		
		for (int i = 0; i < testResults.size(); i++) {
			if (passIndexes.contains(i)) {
				boolean isPass = true;
				
				for (int j : region.comb) {
					if (matrix[i][j] == 0) {
						isPass = false;
						break;
					}
				}
				
				if (isPass) {
					region.addCover(i);
					if (!coveredIndexes.contains(i)) {
						coveredIndexes.add(i);
						isImproved = true;
					}
				}
			}
		}
		
		return isImproved;
	}
	
	private List<Region> createRegions(List<Integer> cols) {
		List<Region> result = new ArrayList<Region>();
		
		List<Integer> passIndexes = new ArrayList<Integer>();
		List<Integer> failIndexes = new ArrayList<Integer>();
		
		List<Integer> coveredIndexes = new ArrayList<Integer>();
		
		for (int i = 0; i < testResults.size(); i++) {
			if (testResults.get(i)) passIndexes.add(i);
			else failIndexes.add(i);
		}
				
		outer:
		for (int i = 1; i <= cols.size(); i++) {
			List<List<Integer>> comb = Utilities.comb(cols, i);
			
			for (List<Integer> group : comb) {
				Region region = new Region(group);
				
				if (isSatisfied(region, failIndexes) &&
						isImproved(region, passIndexes, coveredIndexes)) {
					Iterator<Region> it = result.iterator();
					
					while (it.hasNext()) {
						Region oldRegion = it.next();
						if (region.contains(oldRegion)) {
							it.remove();
						}
					}
					
					result.add(region);
				}
				
				if (passIndexes.size() == coveredIndexes.size()) break outer;
			}
		}
		
		return result;
	}
	
	public String learn() {
		buildMatrix();
		
//		System.out.println(testResults.toString());
		String testResultsStr = testResults.toString();
		
		System.out.println(testResults);
		
		for (int i1 = 0; i1 < matrix.length; i1++) {
			for (int i2 = 0; i2 < matrix[0].length; i2++) {
				System.out.print(matrix[i1][i2] + " ");
			}
			System.out.println();
		}
		
		if (!testResultsStr.contains("true")) return "false";
		else if (!testResultsStr.contains("false")) return "true";
		
		if (!removeRedundant()) return "Illed form matrix";
		
//		System.out.println(testResults);
//		
//		for (int i1 = 0; i1 < matrix.length; i1++) {
//			for (int i2 = 0; i2 < matrix[0].length; i2++) {
//				System.out.print(matrix[i1][i2] + " ");
//			}
//			System.out.println();
//		}
		
		buildEdges();
		List<Integer> cols = chooseCols();
		
		if (cols.size() == 0) return "null";
		else if (cols.size() == 1) return cols.toString();
		else return createRegions(cols).toString();
	}
	
	public static void main(String[] args) {
		List<Boolean> testResults = Arrays.asList(false, true, false);
		String path = "/Users/HongLongPham/Workspace/testdata/data-structure/learn_test/";
		
		LearnDataStructureInvariant l = new LearnDataStructureInvariant(testResults, path);
		System.out.println(l.learn());
	}

}
