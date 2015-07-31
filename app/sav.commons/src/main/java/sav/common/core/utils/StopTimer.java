/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import sav.common.core.Logger;

/**
 * A countdown timer which starts to work with the first entry and
 *  prints the results ascending with the overall time.
 *  Findbugs utilities.
 */
public class StopTimer {
	private String module;
	private TreeMap<Long, String> stopTimes = new TreeMap<Long, String>();
	
	public StopTimer(String module) {
		this.module = module;
	}

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

    public void logResults(Logger<?> log) {
        List<String> lines = getResults();
		for (String line : lines) {
			log.debug(line);
		}
    }

	private synchronized List<String> getResults() {
		List<String> lines = new ArrayList<String>();
        Iterator<Entry<Long, String>> iterator = stopTimes.entrySet().iterator();
        Entry<Long, String> firstEntry = iterator.next();
        Entry<Long, String> lastEntry = firstEntry;
        while (iterator.hasNext()) {
        	lastEntry = iterator.next();
			lines.add(getExecutionTime(firstEntry, lastEntry.getKey().longValue()));
            firstEntry = lastEntry;
        }
        // print the last entry
        long currentTimeMillis = System.currentTimeMillis();
		lines.add(getExecutionTime(lastEntry, currentTimeMillis));
		long overall = currentTimeMillis - stopTimes.firstKey().longValue();
		lines.add(new StringBuilder(module).append(" ").append("Overall: ")
				.append(getTimeString(overall)).toString());
		return lines;
	}
    
	private String getExecutionTime(Entry<Long, String> entry, long endTime) {
		long diff = endTime - entry.getKey().longValue();
		return new StringBuilder(module).append(" - ").append(entry.getValue())
				.append(": ").append(getTimeString(diff)).toString();
	}

	private String getTimeString(long diff) {
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		long diffSec = timeUnit.toSeconds(diff);
		long diffMin = timeUnit.toMinutes(diff);
		StringBuilder sb = new StringBuilder();
		sb.append(diff).append(" ms");
		if (diffMin > 1) {
			sb.append("(").append(diffMin).append("m").append(")");
		} else if (diffSec > 1) {
			sb.append("(").append(diffSec).append("s").append(")");
		}
		return sb.toString();
	}
}
