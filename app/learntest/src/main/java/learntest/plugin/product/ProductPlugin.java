package learntest.plugin.product;

import org.osgi.framework.BundleContext;

import learntest.plugin.LearntestLogger;
import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.PluginException;
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
	protected void internalInitLogger(String projectName) throws PluginException {
		LearntestLogger.initLog4j(projectName, "product_log4j");
	}

	@Override
	protected IGentestManager initJavaGentestManager() {
		return new JavaGentestManager();
	}

}