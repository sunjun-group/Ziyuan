package tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tzuyu.engine.model.Query;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Randomness;


/**
 * All the test cases generated so far. This class is essentially a forest of
 * trees of test case nodes. Each test case corresponds to one path from the 
 * root node to a leaf node.
 * 
 * @author Spencer Xiao
 * 
 */
public class TestCaseStore {

  private Map<Query, List<TestCase>> normalTraces = 
      new HashMap<Query, List<TestCase>>();
  
  private Map<Query, List<TestCase>> errorTraces = 
      new HashMap<Query, List<TestCase>>();
  
  private Map<Query, List<TestCase>> unknownTraces = 
      new HashMap<Query, List<TestCase>>();
  
  public void addTestCase(TestCase tc) {
    
    if (tc.isNormal()) {
      if (normalTraces.containsKey(tc.getTrace())) {
        List<TestCase> cases = normalTraces.get(tc.getTrace());
        cases.add(tc);
      } else {
        List<TestCase> cases = new ArrayList<TestCase>();
        cases.add(tc);
        normalTraces.put(tc.getTrace(), cases);
      }
    } else if (tc.isErroneous()) {
      if (errorTraces.containsKey(tc.getTrace())) {
        List<TestCase> cases = errorTraces.get(tc.getTrace());
        cases.add(tc);
      } else {
        List<TestCase> cases = new ArrayList<TestCase>();
        cases.add(tc);
        errorTraces.put(tc.getTrace(), cases);
      }
    } else {// An unknown trace
      if (unknownTraces.containsKey(tc.getTrace())) {
        List<TestCase> cases = unknownTraces.get(tc.getTrace());
        cases.add(tc);
      } else {
        List<TestCase> cases = new ArrayList<TestCase>();
        cases.add(tc);
        unknownTraces.put(tc.getTrace(), cases);
      }
    }
  }

  public TestCase selectANormalTrace(Query trace) {
    if (normalTraces.containsKey(trace)) {
      List<TestCase> goodCases = new ArrayList<TestCase>();
      List<TestCase> cases = normalTraces.get(trace);
      for (TestCase tc : cases) {
        if (tc.isNormal()) {
          goodCases.add(tc);
        }
      }
      if (goodCases.size() > 0) {
        return Randomness.randomMember(goodCases);
      } else {
        return null;
      }
    } else {
      return null;
    }

  }

	public TestCase selectAnErroneousTrace(Query trace) {
		if (errorTraces.containsKey(trace)) {
			List<TestCase> badCases = new ArrayList<TestCase>();
			List<TestCase> cases = errorTraces.get(trace);
			for (TestCase tc : cases) {
				if (tc.isErroneous()) {
					badCases.add(tc);
				}
			}
			if (badCases.size() > 0) {
				return Randomness.randomMember(badCases);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public List<TestCase> selectNormalTraces(Query trace) {
		List<TestCase> goodTraces = new ArrayList<TestCase>();
		if (normalTraces.containsKey(trace)) {
			List<TestCase> cases = normalTraces.get(trace);
			for (TestCase tc : cases) {
				if (tc.isNormal()) {
					goodTraces.add(tc);
				}
			}
		}

		return goodTraces;
	}

  public List<TestCase> selectErrorTraces(Query trace) {
    List<TestCase> badTraces = new ArrayList<TestCase>();
    if (errorTraces.containsKey(trace)) {
      List<TestCase> cases = errorTraces.get(trace);
      for (TestCase tc : cases) {
        if (tc.isErroneous()){
          badTraces.add(tc);
        }
      }
    }

    return badTraces;
  }
  
	public List<TestCase> selectUnkownTraces(Query trace) {
		List<TestCase> failedTraces = new ArrayList<TestCase>();
		if (unknownTraces.containsKey(trace)) {
			List<TestCase> cases = unknownTraces.get(trace);
			for (TestCase tc : cases) {
				if (tc.isUnknown()) {
					failedTraces.add(tc);
				}
			}
		}

		return failedTraces;
	}
  
	public List<TestCase> findFailedEvidence(TzuYuAction statement) {

		List<TestCase> result = new ArrayList<TestCase>();
		Set<Query> keys = errorTraces.keySet();

		for (Query query : keys) {
			// Get the failed action, which is the last action in the query
			TzuYuAction lastAction = query.getStatement(query.size() - 1);
			// We find a failed evidence for the passed in statement.
			if (lastAction.equals(statement)) {
				return errorTraces.get(query);
			} else if (lastAction.getAction().equals(statement.getAction())) {
				// When the method calls are the same in the two statements but
				// the guard is different, we need to check whether the
				// parameters
				// in the failed action pass the guard testing of the statement
				TestCase errorCase = selectAnErroneousTrace(query);
				result.add(errorCase);
			}
		}

		return result;
	}
  
  	public Pair<List<TestCase>, List<TestCase>> getTestcases(boolean pass, boolean fail) {
  		List<TestCase> goodTcs = new ArrayList<TestCase>();
  		List<TestCase> errorTcs = new ArrayList<TestCase>();
  		if (pass) {
  			goodTcs = getAllGoodTestCases();
  		}
  		if (fail) {
  			errorTcs = getAllErrorTestCases();
  		}
  		return Pair.of(goodTcs, errorTcs); 
  	}
  	
  	/**
     * Get one representative case for each of all the generated normal 
     * and erroneous queries. We exclude the unknown traces here.
     * @return
     */
	public Pair<List<TestCase>, List<TestCase>> getRepresentativeForAllTestCases() {
		return new Pair<List<TestCase>, List<TestCase>>(getAllGoodTestCases(),
				getAllErrorTestCases());
	}

	private List<TestCase> getAllErrorTestCases() {
		List<TestCase> errorCases = new ArrayList<TestCase>();
		for (Query query : errorTraces.keySet()) {
			// Randomly select one not all the error test cases in order to
			// improve
			// performance of testing
			errorCases.add(selectAnErroneousTrace(query));
		}
		return errorCases;
	}
	
	public List<TestCase> getAllGoodTestCases() {
		List<TestCase> normalCases = new ArrayList<TestCase>();
		for (Query query : normalTraces.keySet()) {
			// Randomly select one not all the normal test cases in order to
			// improve
			// performance of testing.
			normalCases.add(selectANormalTrace(query));
		}
		return normalCases;
	}

}
