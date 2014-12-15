/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package extcos;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author khanh
 *
 */
public class ReferenceAnalyserTest {
	@Test
	public void test1(){
		Class<?> abstractClass = Abstract1.class;
		Class<?> output = new ReferenceAnalyser().getRandomImplClzz(abstractClass);
		
		Assert.assertEquals(Abstract1Impl1.class.getName(), output.getName());
	}
	
	@Test
	public void test2(){
		for(int i = 0; i < 100; i++){
			Class<?> abstractClass = Abstract2.class;
			Class<?> output = new ReferenceAnalyser().getRandomImplClzz(abstractClass);
			
			boolean impl1 = output.getName().equals(Abstract2Impl1.class.getName());
			boolean impl2 = output.getName().equals(Abstract2Impl2.class.getName());
			
			System.out.println(output.getName());
			Assert.assertTrue(impl1 || impl2);
		}
	}
	
	@Test
	public void test3(){
		for(int i = 0; i < 100; i++){
			Class<?> interfaceClass = Interface1.class;
			Class<?> output = new ReferenceAnalyser().getRandomImplClzz(interfaceClass);
			
			boolean impl1 = output.getName().equals(Interface1Impl1.class.getName());
			boolean impl2 = output.getName().equals(Interface1Impl2.class.getName());
			
			System.out.println(output.getName());
			Assert.assertTrue(impl1 || impl2);
		}
	}
}
