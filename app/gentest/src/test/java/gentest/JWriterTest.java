/**
 * Copyright TODO
 */
package gentest;

import gentest.core.data.Sequence;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.Statement;
import gentest.junit.JWriter;

import japa.parser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * @author LLT
 *
 */
public class JWriterTest {

	@Test
	public void testWrite() {
		JWriter writer = new JWriter();
		List<Sequence> methods = new ArrayList<Sequence>();
		Sequence seq = new Sequence();
		Statement stmt1 = new RAssignment(Integer.class, 1);
		seq.getStmts().add(stmt1);
		methods.add(seq );
		writer.setClazzName("JWriterTestResult");
		writer.setPackageName("jwriter.test.result");
		writer.setMethodPrefix("test");
		CompilationUnit cu = writer.write(methods);
		System.out.println(cu.toString());
	}
}
