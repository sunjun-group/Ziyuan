/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package generator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * @author LLT
 *
 */
public class GenerateMessagesClass {

	private static final String START_GENERATED_PART_TOKEN = "//	Generated part";
	private static final String END_GENERATED_PART_TOKEN = "//	End generated part";
	private static String pathname = "D:/_1_Projects/Tzuyu/workspace/trunk/app/tzuyuEclipsePlugin/src/tzuyu/plugin/messages.properties";
	private static String messageClassPath = "D:/_1_Projects/Tzuyu/workspace/trunk/app/tzuyuEclipsePlugin/src/tzuyu/plugin/core/constants/Messages.java";
	

	public static void main(String[] args) {
		try {
			generateMessagesClass();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateMessagesClass() throws IOException {
		Properties props = loadProperties();
		String propsContent = buildPropsContent(props);
		File messageCl = new File(messageClassPath);
		StringBuilder content = new StringBuilder();
		boolean inGeneratedZone = false;
		for (Object ln : FileUtils.readLines(messageCl)) {
			String line = (String) ln;
			if (!inGeneratedZone || line.contains(END_GENERATED_PART_TOKEN)) {
				content.append(line).append("\n");
			}
			
			if (line.contains(START_GENERATED_PART_TOKEN)) {
				inGeneratedZone = true;
				content.append(propsContent);
			} else if (line.contains(END_GENERATED_PART_TOKEN)) {
				inGeneratedZone = false;
			} 
		}
		// update class
		FileUtils.writeStringToFile(messageCl, content.toString());
	}

	private static String buildPropsContent(Properties props) {
		StringBuilder content = new StringBuilder();
		for (Object k : props.keySet()) {
			String key = (String) k;
			content.append("\n")
					.append("\tpublic String ")
					.append(key.replace(".", "_"))
					.append("() {")
					.append("\n\t\treturn getMessage(\"")
					.append(key)
					.append("\");")
					.append("\n\t}\n");
					
		}
		return content.append("\n").toString();
	}

	private static Properties loadProperties() {
		Properties prop = new Properties();
		File file = new File(pathname);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			prop.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		return prop;
	}

}
