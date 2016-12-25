package lstar;

/**
 * The exception that should be thrown when the query result is neither true nor
 * false. This case may happen when the destination system can not be described
 * as a DFA.
 * 
 * @author Spencer Xiao
 * 
 */
public class QueryException extends Exception {

  private static final long serialVersionUID = -6726624936492141368L;

  /**
   * The actual result returned by the concrete teacher.
   */
  private Object queryResult;

  public QueryException(Object queryResult) {
    this.queryResult = queryResult;
  }

  public Object getResult() {
    return this.queryResult;
  }

  @Override
  public String getMessage() {
    return queryResult.toString();
  }

}
