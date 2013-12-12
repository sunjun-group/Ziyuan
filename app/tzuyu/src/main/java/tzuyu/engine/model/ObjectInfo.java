package tzuyu.engine.model;

import java.util.List;

public interface ObjectInfo {

  public ClassInfo getType();

  public double getNumericValue();

  public List<ObjectInfo> getValues(int level);

  public boolean isValueNull();

}
