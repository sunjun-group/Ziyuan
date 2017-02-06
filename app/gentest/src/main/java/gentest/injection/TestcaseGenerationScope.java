/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

/**
 * @author LLT
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD }) 
@Retention(RetentionPolicy.RUNTIME) 
@ScopeAnnotation
public @interface TestcaseGenerationScope {

}
