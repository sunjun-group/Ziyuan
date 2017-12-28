package learntest.plugin.product;

import org.osgi.framework.BundleContext;

import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.event.IGentestManager;
import learntest.plugin.commons.event.JavaGentestManager;
import learntest.plugin.console.LearntestConsole;

/**
 * The activator class controls the plug-in life cycle
 */
public class ProductPlugin extends LearntestPlugin {
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		LearntestConsole.delegateConsole();
	};
	
	@Override
	protected IGentestManager initJavaGentestManager() {
		return new JavaGentestManager();
	}

}