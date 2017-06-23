package tzuyu.engine.statistics;

public class NumberTopic extends Topic {

  private String topicName;

  private long count;

  public NumberTopic(String name) {
    topicName = name;
  }

  @Override
  public String getName() {
    return topicName;
  }

  public void setNumber(long number) {
    this.count = number;
  }

  @Override
  public long getValue() {
    return this.count;
  }

}
