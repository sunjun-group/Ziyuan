/**
 * Copyright TODO
 */
package gentest;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.stmt.Statement;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


/**
 * @author LLT
 *
 */
public class TryAst extends AbstractGTTest {
	
	@Test
	public void test() throws ParseException, IOException {
		CompilationUnit cu = JavaParser
				.parse(new File(config.getTestScrPath("gentest") + 
						"/testdata/Program.java"));
		TypeDeclaration type = cu.getTypes().get(0);
		for (BodyDeclaration member : type.getMembers()) {
			if (member instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) member;
				for (Statement stmt : method.getBody().getStmts()) {
					System.out.println(stmt.getClass().getCanonicalName());
					System.out.println(stmt.toString());
				}
			}
		}
	}
}
