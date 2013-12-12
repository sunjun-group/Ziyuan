package tzuyu.engine.statistics;

/**
 * A time topic records the start time and end time of an event.
 * 
 * @author Spencer Xiao
 * 
 */
public class TimeTopic extends Topic {
  private String topicName;
  private long start;
  private long end;

  public TimeTopic(String name) {
    topicName = name;
    start = 0;
    end = 0;
  }

  @Override
  public String getName() {
    return topicName;
  }

  public void setStart() {
    start = System.currentTimeMillis();
  }

  public void setEnd() {
    end = System.currentTimeMillis();
  }

  public long getStart() {
    return start;
  }

  public long getEnd() {
    return end;
  }

  @Override
  public long getValue() {
    return end - start;
  }

}
