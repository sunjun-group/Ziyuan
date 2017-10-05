/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report.annotation;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * @author LLT
 *
 */
public class CoverageAnnotation extends Annotation {
	private static final String COVERAGE_SPECIFICATION = "learntest.plugin.specification.coverage"; //$NON-NLS-1$
	private Position position;
	private int line;
	
	public CoverageAnnotation(int offset, int length, int line) {
		super(COVERAGE_SPECIFICATION, false, null);
		position = new Position(offset, length);
		this.line = line;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public int getLine() {
		return line;
	}

	public static Position getPosition(Annotation annotation) {
		if (annotation instanceof CoverageAnnotation) {
			return ((CoverageAnnotation) annotation).getPosition();
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return String.valueOf(line);
	}

}
