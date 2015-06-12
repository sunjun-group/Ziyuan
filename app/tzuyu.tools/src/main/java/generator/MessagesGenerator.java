/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package generator;

import java.io.IOException;
import java.util.Properties;

/**
 * @author LLT
 *
 */
public class MessagesGenerator extends ClassAppender {
	private static String MESSAGES_PROPERTIES_PATH = TRUNK
			+ "app/tzuyuEclipsePlugin/src/tzuyu/plugin/messages.properties";
	private static String GENERATED_MESSAGES_CLASS_PATH = TRUNK
			+ "app/tzuyuEclipsePlugin/src/tzuyu/plugin/commons/constants/Messages.java";
	

	public static void main(String[] args) {
		try {
			MessagesGenerator generator = new MessagesGenerator();
			generator.appendJavaFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String getClassPath() {
		return GENERATED_MESSAGES_CLASS_PATH;
	}
	
	@Override
	protected String getGeneratedContent() {
		Properties props = ToolsUtils.loadProperties(MESSAGES_PROPERTIES_PATH);
		return buildPropsContent(props);
	}

	protected void appendJavaFile() throws IOException {
		System.out.println("Start generating Messages.java from messages.properties");
		super.appendJavaFile();
		System.err.println("\nWarning: Refresh tzuyu.plugin.commons.constants.Messages.java!");
	}

	private static String buildPropsContent(Properties props) {
		StringBuilder content = new StringBuilder();
		for (Object k : props.keySet()) {
			String key = (String) k;
			String value = props.getProperty(key);
			int argNo = -1;
			for (int i = 0; i < 10; i++) {
				if (value.indexOf("{" + i + "}") > -1) {
					argNo = i;
				}
			}
			
			content.append("\n")
					.append("\tpublic String ")
					.append(key.replace(".", "_"))
					.append("(");
			for (int i = 0; i <= argNo; i++) {
				content.append("Object arg" + i);
				if (i < argNo) {
					content.append(", ");
				}
			}
			content.append(") {")
					.append("\n\t\treturn getMessage(\"")
					.append(key).append("\"");
			for (int i = 0; i <= argNo; i++) {
				content.append(", ")
						.append("arg" + i);
			}
			content.append(");")
					.append("\n\t}\n");
					
		}
		return content.append("\n").toString();
	}
}
