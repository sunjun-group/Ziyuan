/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.tools.build;

import static sav.tools.build.ProjectConfiguration.getProjectBaseDir;

import java.io.File;
import java.io.FileReader;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import sav.common.core.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class EvosuiteDeployer {
	private static String MVN_REPO = "/Users/lylytran/Projects/maven-repository";

	public static void main(String[] args) throws Exception {
		copyDependencies(MVN_REPO, getProjectBaseDir() + "/evosuitor", getProjectBaseDir() + "/evosuitor/libs-ext");
	}

	public static void copyDependencies(String mvnRepo, String projectFolder, String libsFolder) throws Exception {
		copyRecursiveDependencies(mvnRepo, projectFolder + "/pom.xml", libsFolder);
	}

	private static void copyRecursiveDependencies(String mvnRepo, String pomFile, String libsFolder) throws Exception {
		if (!new File(pomFile).exists()) {
			return;
		}
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(pomFile));
		System.out.println(model.getId());
		System.out.println(model.getGroupId());
		System.out.println(model.getArtifactId());
		System.out.println(model.getVersion());
		for (Dependency dependency : model.getDependencies()) {
			String version = getVersion(dependency.getVersion(), model);
			String path = FileUtils.getFilePath(mvnRepo, dependency.getGroupId().replace(".", "/"),
					dependency.getArtifactId().replace(".", "/"), version);
			String jarPath = FileUtils.getFilePath(path,
					dependency.getArtifactId() + "-" + version + ".jar");
			String sourcePath = FileUtils.getFilePath(path,
					dependency.getArtifactId() + "-" + version + "-sources.jar");
			copy(jarPath, sourcePath, libsFolder);
			copyRecursiveDependencies(mvnRepo,
					FileUtils.getFilePath(path, dependency.getArtifactId() + "-" + version + ".pom"),
					libsFolder);
		}
	}

	private static String getVersion(String version, Model model) {
		if ("${project.version}".equals(version)) {
			version = model.getVersion();
		}
		if ("${project.version}".equals(version) || version == null) {
			version = model.getParent().getVersion();
		}
		if ("${javaparser-version}".equals(version)) {
			version = "1.0.11";
		}
		return version;
	}

	private static void copy(String jarPath, String sourcePath, String libsFolder) {
		try {
			FileUtils.copyFileToFolder(jarPath, libsFolder, true);
		} catch (Exception e) {
			System.out.println("error : " + e.getMessage());
		}
		try {
			FileUtils.copyFileToFolder(sourcePath, libsFolder, true);
		} catch (Exception e) {
			System.out.println("error : " + e.getMessage());
		}
	}

}