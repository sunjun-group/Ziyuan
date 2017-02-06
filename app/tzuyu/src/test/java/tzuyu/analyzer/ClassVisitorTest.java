/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import analyzer.ClassVisitor;

import tzuyu.engine.model.ClassInfo;

/**
 * @author LLT
 *
 */
public class ClassVisitorTest {
	private ClassVisitor classVisitor;
	
	@Before
	public void beforeTest() {
		classVisitor = new ClassVisitor();
	}
	
	@Test
	public void testVisitClass() {
		ClassInfo classInfo = classVisitor.visitClass(VisitedClass.class);
		Map<Class<?>, ClassInfo> referencedClasses = classVisitor.getReferencedClasses();
		Assert.assertNotNull(classInfo);
		Assert.assertNotNull(referencedClasses);
	}
	
	public static class VisitedClass extends ArrayList<String> {
		private static final long serialVersionUID = 1L;
		private VisitedClass vs;
		private List<String> content; 
		
		public VisitedClass(VisitedClass vs) {
			this.content = new ArrayList<String>(vs.getContent());
		}
		
		public VisitedClass() {
			content = new ArrayList<String>();
			content.add("adfsdf");
		}
		
		public List<String> getContent() {
			return content;
		}
		
		@Override
		public int size() {
			return content.size();
		}
	}
}
