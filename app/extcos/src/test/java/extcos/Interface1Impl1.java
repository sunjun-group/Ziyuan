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
public class Interface1Impl1 implements Interface1{

	@Override
	public void print() {
		for(int i = 0; i < 10; i++){
			System.out.println(i);
		}
	}

}
