package tzuyu.engine.statistics;

/**
 * A general abstract class for a statistic topic. All the data that the user
 * want to account could be a topic
 * 
 * @author Spencer Xiao
 * 
 */
public abstract class Topic {
  public abstract String getName();

  public abstract long getValue();
}
