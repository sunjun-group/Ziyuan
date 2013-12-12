package lstar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tzuyu.engine.model.Trace;


public final class ObservationTable {

  // the S rows are used as the states
  public Map<Trace, String> sRows;
  // The S*A rows which are used as transitions;
  public Map<Trace, String> saRows;
  // This field corresponds to the E element in the
  // formal definition of observation table
  public List<Trace> columns;

  public ObservationTable() {
    // Here we use LinkedHashMap instead of the normal
    // HashMap, because we need the Map::KeySet() returns
    // an ordered elements. This feature is guaranteed by
    // LinkedHashMap while the HashMap implementation return
    // a set of randomly arranged elements.
    sRows = new LinkedHashMap<Trace, String>();
    saRows = new LinkedHashMap<Trace, String>();
    columns = new ArrayList<Trace>();
  }

  /**
   * Add the new string to the S*A rows with the
   * 
   * @param i
   * @param j
   * @param isMember
   */
  public void addSARow(Trace str, String isMember) {
    saRows.put(str, isMember);
  }

  public void addSRow(Trace str, String isMember) {
    sRows.put(str, isMember);
  }

  public String getRow(Trace str) {
    String value = sRows.get(str);
    if (value == null) {
      value = saRows.get(str);
    }

    return value;
  }

  public void clear() {
    this.columns.clear();
    this.sRows.clear();
    this.saRows.clear();
  }

}
