/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package gov.nasa.jpf.search;


import gov.nasa.jpf.Config;
import gov.nasa.jpf.jdart.ConcolicMethodExplorer;
import gov.nasa.jpf.jdart.constraints.InternalConstraintsTree;
import gov.nasa.jpf.vm.VM;


/**
 * standard depth first model checking (but can be bounded by search depth
 * and/or explicit Verify.ignoreIf)
 */
public class MySearch extends Search {

  public MySearch (Config config, VM vm) {
  	super(config,vm);
  }

  @Override
  public boolean requestBacktrack () {
    doBacktrack = true;

    return true;
  }
  
  @Override
  public void search () {
	  boolean initialized = true;
	  ConcolicMethodExplorer cme = null;
	  while(initialized) {
		  forward();
		  cme = (ConcolicMethodExplorer)vm.getSystemState().getThreadInfo().getAttr();
		  if(cme.getInitValuation() != null)
			  initialized = false;
	  }
	  int[] node_branch = new int[2];
	  node_branch[0] = Integer.valueOf((String)config.get("explore.node"));
	  node_branch[1] = Integer.valueOf((String)config.get("explore.branch"));
	  InternalConstraintsTree ict = cme.getInternalConstraintsTree();
	  ict.solve(node_branch);
	  System.out.println();
  }


  @Override
  public boolean supportsBacktrack () {
    return true;
  }
}
