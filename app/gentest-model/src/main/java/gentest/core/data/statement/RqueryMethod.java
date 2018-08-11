/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.statement;

import gentest.core.data.MethodCall;


/**
 * @author LLT
 *
 */
public class RqueryMethod extends Rmethod {
	private static final long serialVersionUID = 4867507745130808977L;
	private transient MethodCall queryMethod;
	
	public RqueryMethod() {
		super(null);
	}

	public RqueryMethod(MethodCall staticMethod) {
		super(staticMethod.getMethod());
		this.queryMethod = staticMethod;
	}

	public RqueryMethod(MethodCall method, int receiverId) {
		super(method.getMethod(), receiverId);
		this.queryMethod = method;
	}
	
	@Override
	public RStatementKind getKind() {
		return RStatementKind.QUERY_METHOD_INVOKE;
	}
	
	public MethodCall getQueryMethod() {
		return queryMethod;
	}
}
