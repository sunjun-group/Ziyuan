package tester;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.TVAnswer;
import tzuyu.engine.model.Variable;


/**
 * This node is a node in the generated test case path. This node corresponds to
 * a statement and its parameters;
 * 
 * @author Spencer Xiao
 * 
 */
public class TestCaseNode {

  public static final TestCaseNode epsilonNode = new TestCaseNodeEpsilon();

  /**
   * The execution result(error, normal, unknown) is recorded in this field
   */
  private final TVAnswer result;

  private final Statement statement;
  
  /**
   * The parameter sequence is the concatenation of all the sequences that 
   * generate the parameters for the statement but not including the current 
   * statement.
   */
  private final Sequence parameterSequence;
  
  private Variable receiver;

  private List<TestCaseNode> children;

  private TestCaseNode parent;

  public Statement getStatement() {
    return statement;
  }

  public Sequence getParameters() {
    return parameterSequence;
  }

  public TestCaseNode getParent() {
    return parent;
  }

  public TVAnswer getResult() {
    return result;
  }

  public TestCaseNode(TVAnswer answer, Statement stmt, Sequence paramSeq) {
    this.result = answer;
    this.statement = stmt;
    this.parameterSequence = paramSeq;
    this.children = new ArrayList<TestCaseNode>();
    this.receiver = null;
  }
  
  public void addChild(TestCaseNode child) {
    this.children.add(child);
    child.parent = this;
  }
  
  public Variable getReceiver() {
    return receiver;
  }
  
  public void upateReceiver() {
    if (statement.getAction().getAction().hasReceiverParameter()) {
      Sequence newSeq = parameterSequence.extend(statement);
      this.receiver = new Variable(newSeq, newSeq.size() -1, 0);
    }
  }
  
  public void setReceiver() {
    Sequence newSeq = parameterSequence.extend(statement);
    this.receiver = new Variable(newSeq, newSeq.size() -1, -1);
  }
  
}

class TestCaseNodeEpsilon extends TestCaseNode {

  TestCaseNodeEpsilon() {
    super(TVAnswer.Accepting, null, null);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    return o instanceof TestCaseNodeEpsilon;
  }

}
