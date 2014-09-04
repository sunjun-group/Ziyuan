package javacocoWrapper;

import static org.junit.Assert.*;

import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class SampleProgramTest {
	
	
	@Test
	public void test1() {
		SamplePrograms sampleProgram = new SamplePrograms();
		int max = sampleProgram.Max(1, 1, 1);
		
		System.out.println("run test 1");
		assertEquals(max, 1);
	}
	
	@Test
	public void test2() {
		SamplePrograms sampleProgram = new SamplePrograms();
		int max = sampleProgram.Max(1, 2, 3);
		
		System.out.println("run test 2");
		assertEquals(max, 3);
	}
	
	@Test
	public void test3() {
		SamplePrograms sampleProgram = new SamplePrograms();
		int max = sampleProgram.Max(3, 2, 1);
		
		System.out.println("run test 3");
		assertEquals(max, 3);
	}
	
	@Test
	public void test4() {
		SamplePrograms sampleProgram = new SamplePrograms();
		int max = sampleProgram.Max(3, 1, 2);
		
		System.out.println("run test 4");
		assertEquals(max, 3);
	}
	
	@Test
	public void test5() {
		SamplePrograms sampleProgram = new SamplePrograms();
		int max = sampleProgram.Max(1, 3, 2);
		
		System.out.println("run test 5");
		assertEquals(max, 3);
	}

}
