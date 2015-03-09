package gentest;

import gentest.core.TestcaseGenerator;
import gentest.core.data.statement.Statement;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.generator.ValueGeneratorMediator;
import gentest.junit.AstNodeConverter;
import gentest.junit.variable.VariableNamer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ValueGenerator {

//	public void run() {
//		Injector injector = Guice.createInjector(new GentestInjector());
//		ValueGeneratorMediator valueGenerator = injector.getInstance(ValueGeneratorMediator.class);
//		GeneratedVariable var = valueGenerator.generate(clazz, type, firstVarId, isReceiver);
//		AstNodeConverter converter = new AstNodeConverter(new VariableNamer());
//		for (Statement stmt : var.getStmts()) {
//			converter.reset();
//			stmt.accept(converter);
//			converter.getResult();
//		}
//		
//	}
	
}
