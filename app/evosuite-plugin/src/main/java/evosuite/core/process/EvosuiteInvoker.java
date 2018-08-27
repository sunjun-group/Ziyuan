/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core.process;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.result.TestGenerationResult;

/**
 * @author LLT
 *
 */
public class EvosuiteInvoker {
	public static final String CMD_SEPRATOR = "###";
	public static final String END_TOKEN = "Learntest-Evosuite finished!!!!";
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		long runningTime = System.currentTimeMillis();
		String filePath = args[0];
		String[] evosuiteParams = args[1].split(CMD_SEPRATOR);
		System.out.println(Arrays.toString(evosuiteParams));
		EvoSuite evosuite = new EvoSuite();
		List<List<TestGenerationResult>> result = (List<List<TestGenerationResult>>) evosuite
				.parseCommandLine(evosuiteParams);
		File file = new File(filePath);
		
		FileOutputStream fileStream = null;
		OutputStream bufferedStream = null;
		DataOutputStream outputWriter = null;
		FileLock lock = null;
		try {
			System.out.println(result);
			fileStream = new FileOutputStream(file, false);
			// Avoid concurrent writes from other processes:
			lock = fileStream.getChannel().lock();
			bufferedStream = new BufferedOutputStream(fileStream);
			outputWriter = new DataOutputStream(bufferedStream);
			runningTime = System.currentTimeMillis() - runningTime;
			byte[] bytes = ByteConverter.convertToBytes(EvosuiteTestResult.extract(result, runningTime));
			outputWriter.writeInt(bytes.length);
			outputWriter.write(bytes);
			outputWriter.flush();
		} finally {
			if (lock != null) {
				lock.release();
			}
			close(fileStream);
			close(bufferedStream);
			close(outputWriter);
			runningTime = System.currentTimeMillis() - runningTime;
		}
		System.out.println("Evosuite Running Time: " + runningTime);
		System.out.println(END_TOKEN);
	}

	private static void close(Closeable closeble) {
		if (closeble != null) {
			try {
				closeble.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
	
}
