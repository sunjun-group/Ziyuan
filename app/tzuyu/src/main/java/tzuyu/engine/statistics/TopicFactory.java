package tzuyu.engine.statistics;

import java.util.LinkedHashMap;
import java.util.Map;

public class TopicFactory {
  private static Map<String, NumberTopic> numberTopicsMap = 
      new LinkedHashMap<String, NumberTopic>();

  private static Map<String, MemoryTopic> memoryTopicsMap = 
      new LinkedHashMap<String, MemoryTopic>();

  private static Map<String, TimeTopic> timeTopicsMap = 
      new LinkedHashMap<String, TimeTopic>();

  public static void clear() {
    numberTopicsMap.clear();
    memoryTopicsMap.clear();
    timeTopicsMap.clear();
  }

  public static TimeTopic getTimeTopic(String name) {
    if (timeTopicsMap.containsKey(name)) {
      return timeTopicsMap.get(name);
    }

    TimeTopic newTopic = new TimeTopic(name);
    timeTopicsMap.put(name, newTopic);

    return newTopic;
  }

  public static NumberTopic getNumberTime(String name) {
    if (numberTopicsMap.containsKey(name)) {
      return numberTopicsMap.get(name);
    }

    NumberTopic newTopic = new NumberTopic(name);
    numberTopicsMap.put(name, newTopic);

    return newTopic;
  }

  public static MemoryTopic getMemoryTopic(String name) {
    if (memoryTopicsMap.containsKey(name)) {
      return memoryTopicsMap.get(name);
    }

    MemoryTopic newTopic = new MemoryTopic(name);
    memoryTopicsMap.put(name, newTopic);

    return newTopic;
  }

}
