/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author LLT 
 * A countdown timer which starts to work with the first entry and
 *  prints the results ascending with the overall time.
 *  Findbugs utilities.
 */
public class StopTimer {
	TreeMap<Long, String> stopTimes = new TreeMap<Long, String>();

    public synchronized void newPoint(String name) {
        Long time = Long.valueOf(System.currentTimeMillis());
        if (stopTimes.size() == 0) {
            stopTimes.put(time, name);
            return;
        }
        Long lastTime = stopTimes.lastKey();
        if (time.longValue() <= lastTime.longValue()) {
            time = Long.valueOf(lastTime.longValue() + 1);
        }
        stopTimes.put(time, name);
    }

    public synchronized String getResults() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<Long, String>> iterator = stopTimes.entrySet().iterator();
        Entry<Long, String> firstEntry = iterator.next();
        while (iterator.hasNext()) {
            Entry<Long, String> entry = iterator.next();
            long diff = entry.getKey().longValue() - firstEntry.getKey().longValue();
            sb.append(firstEntry.getValue()).append(": ").append(diff).append(" ms\n");
            firstEntry = entry;
        }

        long overall = stopTimes.lastKey().longValue() - stopTimes.firstKey().longValue();
        sb.append("Overall: ").append(overall).append(" ms");
        return sb.toString();
    }
}
