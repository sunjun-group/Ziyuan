package testdata;

import java.util.ArrayList;
import java.util.List;

public class StatelessBoundedStack {

  private List<Object> content;

  public StatelessBoundedStack() {
    content = new ArrayList<Object>();
  }

  public boolean push(Object obj) throws Exception {
    if (content.size() == 3) {
      throw new Exception("stack is full while atempt to push");
    }

    content.add(obj);
    return true;
  }

  public Object pop() throws Exception {
    if (content.size() == 0) {
      throw new Exception("stack is empty while atempt to pop");
    }
    return content.remove(content.size() - 1);
  }
}
