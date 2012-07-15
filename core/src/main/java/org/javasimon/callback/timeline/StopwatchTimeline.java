package org.javasimon.callback.timeline;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.javasimon.utils.SimonUtils;

/**
 * Timeline for Stopwatches, historicise {@link Stopwatch} state on a 
 * rolling period of time.
 * @author gerald
 */
public final class StopwatchTimeline extends Timeline<StopwatchTimeRange> {
	/**
	 * Main constructor
	 * @param capacity Number of time range.
	 * @param timeRangeWidth Width of each time range
	 */
	public StopwatchTimeline(int capacity, long timeRangeWidth) {
		super(capacity, timeRangeWidth);
	}
	/**
	 * Produces a {@link StopwatchTimeRange} object
	 * @param startTimestamp Range beginning
	 * @param endTimestamp Range ending
	 * @return {@link StopwatchTimeRange} object
	 */
	@Override
	protected StopwatchTimeRange createTimeRange(long startTimestamp, long endTimestamp) {
		return new StopwatchTimeRange(startTimestamp, endTimestamp);
	}
	/**
	 * Main method used to insert the split on the timeline: <ol>
	 * <li>Split start is used to determine in which timerange it should be split. A new time range may be created if needed.</li>
	 * <li>Split duration is added to time range statistics.
	 * </ol>
	 * The split might be drop if it's too old.
	 * @param split Split
	 */
	public void addSplit(Split split) {
		final long timestamp = SimonManager.millisForNano(split.getStart());
		StopwatchTimeRange timeRange;
		synchronized (this) {
			timeRange = getOrCreateTimeRange(timestamp);
		}
		if (timeRange != null) {
			synchronized(timeRange) {
				timeRange.addSplit(timestamp, split.runningFor());
			}
		}
	}
	/**
	 * Take a snapshot of the timeline.
	 * @return Timeline sample
	 */
	@Override
	public TimelineSample<StopwatchTimeRange> sample() {
		StopwatchTimeRange[] timeRangesCopy;
		synchronized (this) {
			timeRangesCopy = timeRanges.toArray(new StopwatchTimeRange[timeRanges.size()]);
		}
		// TODO sample each timerange by making a copy of it
		return new TimelineSample<StopwatchTimeRange>(timeRanges.size(), timeRangeWidth, timeRangesCopy);
	}

}
