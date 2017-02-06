package tzuyu.engine.statistics;

public class MemoryTopic extends Topic {

  private String topicName;

  private long start;

  private long end;

  public MemoryTopic(String name) {
    topicName = name;
    start = 0;
    end = 0;
  }

  @Override
  public String getName() {
    return topicName;
  }

  public long getEnd() {
    return end;
  }

  public long getStart() {
    return start;
  }

  public void setStart() {
    start = Runtime.getRuntime().freeMemory();
  }

  public void setEnd() {
    end = Runtime.getRuntime().freeMemory();
  }

  @Override
  public long getValue() {
    return start - end;
  }

}
