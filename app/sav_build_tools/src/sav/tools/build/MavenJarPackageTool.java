/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/

package sav.tools.build;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import sav.common.core.utils.FileUtils;

/**
 * @author thilyly_tran
 *
 */
public class MavenJarPackageTool {
	private static final String MAVEN_HOME = "E:/lyly/Projects/microbat/master/microbat_instrumentator/build/maven/apache-maven-3.5.2";
	private static final String LEARNTEST_PRJ_FOLDER = "E:/lyly/Projects/Ziyuan";
	
	
	
	private static final String JAVA_HOME = getJavaHome();
			// "/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home";
	private static final String LIBS_DEPLOY_FOLDER = LEARNTEST_PRJ_FOLDER + "/app/cfgcoverage.jacoco/libs";
	
	
	public static void main(String[] args) throws MavenInvocationException {
		/* build maven */
		InvocationRequest request = new DefaultInvocationRequest();
		String buildProject = getBaseDir().replace("sav_build_tools", "org.jacoco.build");
		request.setPomFileName(buildProject + "pom.xml");
		request.setGoals(Arrays.asList("clean install"));
		Properties properties = new Properties();
		properties.setProperty("maven.home", MAVEN_HOME);
		properties.setProperty("JAVA_HOME", JAVA_HOME);
		request.setProperties(properties);
		request.setJavaHome(new File(JAVA_HOME));
		Invoker invoke = new DefaultInvoker();
		invoke.setMavenHome(new File(MAVEN_HOME));
		invoke.setWorkingDirectory(new File(buildProject));
		invoke.setLocalRepositoryDirectory(new File("E:/lyly/Projects/mvn_repository"));
		invoke.execute(request);
		
		/* update new jars to our java projects */
		
		FileUtils.copyFile(getTargetFolder("org.jacoco.agent") + "org.jacoco.agent-0.7.10-SNAPSHOT.jar", 
						LIBS_DEPLOY_FOLDER + "/org.jacoco.agent-0.7.10.jar", true);
		
		FileUtils.copyFile(getTargetFolder("org.jacoco.agent") + "org.jacoco.agent-0.7.10-SNAPSHOT-sources.jar", 
				LIBS_DEPLOY_FOLDER + "/org.jacoco.agent-0.7.10-sources.jar", true);
		
		FileUtils.copyFile(getTargetFolder("org.jacoco.core") + "org.jacoco.core-0.7.10-SNAPSHOT.jar", 
				LIBS_DEPLOY_FOLDER + "/org.jacoco.core-0.7.10.jar", true);
		
		FileUtils.copyFile(getTargetFolder("org.jacoco.core") + "org.jacoco.core-0.7.10-SNAPSHOT-sources.jar", 
				LIBS_DEPLOY_FOLDER + "/org.jacoco.core-0.7.10-sources.jar", true);
	}

	private static String getTargetFolder(String prjName) {
		return getBaseDir().replace("sav_build_tools", prjName) + "target/";
	}

	public static String getBaseDir() {
		String path = new File(MavenJarPackageTool.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
		path = path.replace("\\", "/");
		path = path.replace("bin", "");
		return path;
	}
	
	public static String getJavaHome() {
		// work around in case java home not point to jdk but jre.
		String javaHome = System.getProperty("java.home");
		if (javaHome.endsWith("jre")) {
			javaHome = javaHome.substring(0,
					javaHome.lastIndexOf(File.separator + "jre"));
		}
		return javaHome;
	}
}
