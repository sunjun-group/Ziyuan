package tester;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.RelativeNegativeIndex;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.TVAnswer;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.Variable;


/**
 * A test case for a given trace is a sequence of executable statements with
 * concrete parameters but without the instrumented states. In order to get 
 * the instrumented states, one have to execute the test case with an 
 * instance of the {@link IInstrumentor}.
 * 
 * @author Spencer Xiao
 *   
 */
public class TestCase {

  /**
   * The original abstract trace
   */
  private Query abstractTrace;

  private TestCaseNode lastStatement;

  public static final TestCase epsilon = new TestCase(Query.emptyQuery(),
      TestCaseNode.epsilonNode);

  /**
   * Ensure that the size of the<code>seqs<code> is equal to the size of 
   * <code>stmts</code>.
   * 
   * @param trace
   *          the abstract trace which the test case corresponds to.
   * @param nodes
   *          the parameter sequences that generate the concrete parameters for
   *          the statements.
   */
  private TestCase(Query trace, TestCaseNode node) {
    abstractTrace = trace;
    lastStatement = node;
  }

  public boolean isEpsilon() {
    return abstractTrace.isEpsilon()
        && lastStatement.equals(TestCaseNode.epsilonNode);
  }

  public boolean isNormal() {
    return lastStatement.getResult() == TVAnswer.Accepting;
  }
  
  public boolean isUnkonw() {
    return lastStatement.getResult() == TVAnswer.Unknown;
  }
  
  public boolean isErroneous() {
    return lastStatement.getResult() == TVAnswer.Rejecting;
  }
  
  public Variable getReceiver() {
    
    return lastStatement.getReceiver();
  }

  public TestCaseNode getLastStatement() {
    return lastStatement;
  }

  public TestCase extend(TestCaseNode newNode) {
    Query newQuery = abstractTrace.extend(newNode.getStatement().getAction());

    // update the node to the store
    lastStatement.addChild(newNode);
    // update the receiver variable
    Variable receiver = lastStatement.getReceiver();
    // for constructor, if the receiver in last statement is null, we need set 
    // the execution result of the current statement as the initial receiver, 
    // if the receiver in last statement is not null, we do nothing, then the 
    // statements after this statement will have no receiver, then it will 
    // throw exceptions, if there are more than one receiver, then we ignore 
    // all the intermediate constructors.
    if (newNode.getStatement().getAction().getAction().isConstructor()) {
      if (receiver == null) {
        // set the initial receiver.
        newNode.setReceiver();
      } else {
        // Do nothing
      }
    } else {
      newNode.upateReceiver();
    }

    return new TestCase(newQuery, newNode);
  }

  public Query getTrace() {
    return abstractTrace;
  }

  public QueryTrace execute(IInstrumentor instrumentor) {
    // Get the trace, here we use the doubly-linked list to improve
    // performance of the operation of add(0, element), which is 
    // takes constant time for LinkedList. 
    List<TestCaseNode> statements = new LinkedList<TestCaseNode>();
    TestCaseNode current = lastStatement;
    // we don't include the epsilon node in the trace
    while (!current.equals(TestCaseNode.epsilonNode)) {
      statements.add(0, current);
      current = current.getParent();
    }
    // prepare for instrumentation
    instrumentor.startInstrument();

    QueryTrace lastTrace = null;
    for (int index = 0; index < statements.size(); index++) {

      TestCaseNode node = statements.get(index);
      Statement stmt = node.getStatement();
      TzuYuAction action = abstractTrace.getStatement(index);
      

      Sequence sequence = node.getParameters();
      int size = sequence.size();

      List<Variable> inputVars = new ArrayList<Variable>();
      List<RelativeNegativeIndex> indices = stmt.getInputVars();

      for (int i = 0; i < indices.size(); i++) {
        RelativeNegativeIndex idx = indices.get(i);
        Variable var = new Variable(sequence, size + idx.stmtIdx, idx.argIdx);
        inputVars.add(var);
      }

      ExecutionResult result = RuntimeExecutor.executeGuard(action, inputVars);

      // instrument for state before the statement execution
      instrumentor.instrument(stmt, inputVars, result.getRuntime());
      List<Prestate> states = instrumentor.getRuntimeTrace();
      Query query = abstractTrace.getSubQuery(index + 1);

      Sequence newSeq = sequence.extend(action, inputVars);
      

      if (result.isPassing()) {
        boolean normal = RuntimeExecutor.executeStatement(action,
            result.getRuntime());

        if (normal) {
          // to filter the trace in which there are multiple constructors
          if (index != 0 && action.getAction().isConstructor()) {
            lastTrace = new QueryTrace(query, newSeq, states, 
                TVAnswer.Rejecting, query.size() - 2);
            break;
          } else {
            lastTrace = new QueryTrace(query, newSeq, states,
                TVAnswer.Accepting, query.size() - 1);
          }
        } else {
          lastTrace = new QueryTrace(query, newSeq, states,
              TVAnswer.Rejecting, query.size() - 2);
          //for parameters that throws exception, we terminate the process
          break;
        }
      } else {
        lastTrace = new QueryTrace(query, newSeq, states, TVAnswer.Unknown,
            query.size() - 2);
        //for parameters that cannot pass guard test, we terminate the process
        break;
      }
    }
    // finish the instrumentation
    instrumentor.endInstrument();

    return lastTrace;
  }
  
  public Sequence getSequence() {
    // We start from the end of the test case towards its head which is 
    // the epsilon node.
    
    TestCaseNode current = lastStatement;
    List<TestCaseNode> nodes = new LinkedList<TestCaseNode>();
    while (current != null) {
      nodes.add(0, current);
      current = current.getParent();
    }
    
    //Sequence sequence = new Sequence();
    
    int size = nodes.size();
    //The first statement is the receiver, we need to handle it specially
    //if (size > 0) {
      //sequence = nodes.get(0).getParameters();
    //}
   
    // Create the sequence from the above statements
    TestCaseNode node = nodes.get(size - 1);
    Sequence paramSequence = node.getParameters();
    Sequence newSequence = paramSequence.extend(node.getStatement());
    
    return newSequence;
  }

}
