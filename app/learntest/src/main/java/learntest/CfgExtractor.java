package learntest;

import java.io.File;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import sav.java.parser.cfg.CFG;
import sav.java.parser.cfg.CfgFactory;

public class CfgExtractor {
	
	public static void main(String[] args) {
		File file = new File("src/main/resources/CfgTestData.txt");
		new CfgExtractor().extract(file);
	}
	
	public void extract(File file) {
		try {
			CompilationUnit cu = JavaParser.parse(file);
			for (TypeDeclaration type : cu.getTypes()) {
				for (BodyDeclaration body : type.getMembers()) {
					if (body instanceof MethodDeclaration) {
						MethodDeclaration method = (MethodDeclaration) body;
						System.out.println("---------------------------------------------");
						System.out.println(method.getName() + method.getParameters());
						System.out.println("---------------------------------------------");
						CFG cfg = CfgFactory.createCFG(method);
						System.out.println(cfg.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
