/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package extcos;

/**
 * @author khanh
 *
 */
public class Interface1Impl2 implements Interface1{


	@Override
	public void print() {
		for(char c = 'a'; c <= 'z'; c++){
			System.out.println(c);
		}
	}
	
}
