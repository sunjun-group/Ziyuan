package tzuyu.engine.store;

public final class QueryTraceStoreFactory {

  private QueryTraceStoreFactory() {

  }

  public static IQueryTraceStore createStore() {
    return VersionBasedQueryTraceStore.getInstance();
  }
}
