package jdart.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import main.RunJPF;

public class RunJDartHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		String[] quicksortConfig = new String[]{
				"+app=libs/jdart/jpf.properties",
				"+jpf-jdart.classpath+=../../bin",
				"+target=Sorting",
				"+concolic.method=quicksort",
				"+concolic.method.quicksort=${target}.quicksort(a:int[])",
				"+concolic.method.quicksort.config=all_fields_symbolic"
		};
		
		RunJPF.run(quicksortConfig);
		
		return null;
	}

}
