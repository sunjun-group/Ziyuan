package tzuyu.engine.model;

/**
 * The query is a list of guarded statements which wraps the original trace.
 * 
 * @author Spencer Xiao
 * 
 */
public class Query {

  private final Trace originalStr;

  private final static Query emptyQuery = new Query(Trace.epsilon);

  public Query(Trace trace) {

    if (trace == null) {
      throw new IllegalArgumentException("the trace is null");
    }

    originalStr = trace;
  }

  public static Query emptyQuery() {
    return emptyQuery;
  }

  /**
   * Return the immediate prefix query of the current query. The immediate query
   * is defined as query obtained by get the substring from index 0 (inclusive)
   * to length - 1(exclusive).
   * 
   * @return
   */
  public Query getImmediatePrefix() {
    if (size() > 0) {
      Trace prefixStr = originalStr.getSubString(0, size() - 1);
      if (prefixStr.size() == 0) {
        return emptyQuery;
      } else {
        Query prefix = new Query(prefixStr);
        return prefix;
      }
    } else {
      return null;
    }
  }

  /**
   * Get the remaining query which is the result of subtracting the current
   * statements with the prefix query <code>prefix</code> which must be a prefix
   * of current query.
   * 
   * @param prefix
   *          a prefix of current query.
   * @return the result query obtained by subtracting the prefix from current
   *         query. If the give query is not a prefix of current query then
   *         return an empty list.
   */
  public Query getRemainingQuery(Query prefix) {
    if (isPrefix(prefix)) {
      Trace str = originalStr.getSubString(prefix.size(), size());
      return new Query(str);
    } else {
      return this;
    }
  }

  /**
   * Checks whether the given query <code>q</code> is a prefix of current query.
   * 
   * @param q
   *          the query to be checked.
   * @return true if <code>q</code> is a prefix of current query; false
   *         otherwise.
   */
  public boolean isPrefix(Query q) {
    if (q.size() > size()) {
      return false;
    }

    for (int i = 0; i < q.size(); i++) {
      if (!get(i).equals(q.get(i))) {
        return false;
      }
    }

    return true;
  }

  public boolean isEpsilon() {
    return size() == 0;
  }

  public int size() {
    return originalStr.size();
  }

  /**
   * Append the statement <code>stmt</code> and its corresponding alphabet
   * number to the statements and original LString of current query and return a
   * new query which refers to the new statements.
   * 
   * @param stmt
   *          the statement is to be appended
   * @return a new query with <code>stmt</code> appended to the end of
   *         statements of current query.
   */
  public Query extend(TzuYuAction stmt) {
    Trace newStr = originalStr.concatenateAtTail(stmt);
    Query retval = new Query(newStr);
    return retval;
  }

  private TzuYuAction get(int index) {
    return (TzuYuAction) originalStr.valueAt(index);
  }

  /**
   * Get the sub-query from index <code>0</code>(inclusive) to index
   * <code>len</code>(exclusive).
   * 
   * @param len
   * @return
   */
  public Query getSubQuery(int len) {
    Trace newString = originalStr.getSubString(0, len);
    return new Query(newString);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof Query) {
      Query obj = (Query) o;
      if (originalStr.size() != obj.originalStr.size()) {
        return false;
      } else {
        return originalStr.equals(obj.originalStr);
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return originalStr.hashCode();
  }

  public final TzuYuAction getStatement(int index) {
    return get(index);
  }

  public final Trace getLString() {
    return originalStr;
  }

  @Override
  public String toString() {
    return originalStr.toString();
  }

}
