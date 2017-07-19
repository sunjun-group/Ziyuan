/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.testdata;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class SampleProgram1 {

//    @Test
//    public void test1() throws Throwable {
//        SamplePrograms sampleprograms0 = new SamplePrograms();
//        int i0 = -42;
//        int i1 = 93;
//        int i2 = 170;
//        int i3 = sampleprograms0.Max(i0, i1, i2);
//    }
    
    @Test
    public void test2() throws Throwable {
        SamplePrograms sampleprograms0 = new SamplePrograms();
        int i0 = 42;
        int i1 = 20;
        int i2 = 170;
        int i3 = sampleprograms0.Max(i0, i1, i2);
    }
    
    @Test
    public void test100() throws Throwable {
        int i0 = 500;
        int i1 = -24;
        int i2 = 500;
        SamplePrograms sampleprograms0 = new SamplePrograms();
        int i3 = sampleprograms0.Max(i0, i1, i2);
    }
    
    @Test
    public void test102() throws Throwable {
        int i0 = 392;
        int i1 = 500;
        int i2 = 500;
        SamplePrograms sampleprograms0 = new SamplePrograms();
        int i3 = sampleprograms0.Max(i0, i1, i2);
    }
}
