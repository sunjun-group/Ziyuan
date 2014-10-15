/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 * 
 */
public abstract class Seed {
	public SeedType type;
	
	public abstract void append(String token);
	
	public abstract void apply(List<String> result);
	
	protected int parseLine(String token) {
		return Integer.parseInt(token) - 1;
	}
	
	protected String getTab(String orgLine) {
		StringBuilder tab = new StringBuilder();
		for (int i = 0; i < orgLine.length(); i++) {
			char ch = orgLine.charAt(i);
			if ((ch != '\t') && ch != ' ') {
				break;
			}
			tab.append(ch);
		}
		return tab.toString();
	}
	
	public static Seed fromType(String token) {
		SeedType type = SeedType.getAssociateType(token);
		Seed seed;
		switch (type) {
		case MOVE:
		case SWAP:
			seed = new LineToLineSeed();
			break;
		default:
			seed = new ReplaceSeed();
			break;
		}
		seed.type = type;
		return seed;
	}
	
	public static class ReplaceSeed extends Seed {
		public List<Integer> treatedLines = new ArrayList<Integer>();
		public String newContent;

		@Override
		public void append(String token) {
			if (token.startsWith("\"")) {
				if (token.equals("\"\"")) {
					newContent = "";
				} else {
					newContent = token.substring(1, token.length() - 1);
				}
				return;
			}
			// if not the content, it must be the line number
			treatedLines.add(parseLine(token));
		}

		@Override
		public void apply(List<String> lines) {
			for (Integer lineNo : treatedLines) {
				String line = lines.get(lineNo);
				String tab = getTab(line);
				String modifiedLine = "";
				switch (type) {
				case REPLACE:
					modifiedLine = String.format("//%s\n%s%s// (seeded)!!",
							line, tab, newContent);
					break;
				case ADD:
					modifiedLine = String.format("%s\n%s%s// (seeded)!!", 
							line, tab, newContent);
					break;
				case REMOVE:
					modifiedLine = String.format("//%s// (seeded)!!", 
							line);
					break;
				default:
					break;
				}
				lines.set(lineNo, modifiedLine);
			}
		}
	}
	
	public static class LineToLineSeed extends Seed {
		public int from;
		public int to;
		
		@Override
		public void append(String token) {
			if (token.startsWith("from")) {
				from = parseLine(token.replace("from ", ""));
			} else if (token.startsWith("to")) {
				to = parseLine(token.replace("to ", ""));
			} else {
				if (from == 0) {
					from = parseLine(token);
				} else {
					to = parseLine(token);
				}
			}
		}

		@Override
		public void apply(List<String> result) {
			// edit [from] line
			String toLine = result.get(to);
			String fromLine = result.get(from);
			if (type == SeedType.MOVE) {
				result.set(to, String.format("%s// (seeded)!!\n%s", fromLine, toLine));
				result.set(from, String.format("//%s ", fromLine));
			} else if (type == SeedType.SWAP) {
				result.set(to, String.format("//%s\n%s // (seeded)!!", toLine, fromLine));
				result.set(from, String.format("//%s\n%s // (seeded)!!", fromLine, toLine));
			}
		}
	}

	public static enum SeedType {
		MOVE, REMOVE, REPLACE, ADD, SWAP;

		public static SeedType getAssociateType(String token) {
			for (SeedType type : values()) {
				if (type.name().toLowerCase().equals(token.toLowerCase())) {
					return type;
				}
			}
			System.err.println("cannot find seed type " + token);
			return null;
		}
	}

}
