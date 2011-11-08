package com.intersystems.globals.hospmon;

/**
 * This class represents statistics from a collector
 * 
 * @author tspencer
 */
public class CollectionStats {
	private final long timeSpan;
	private final int readingsTaken;
	private final long totalSaveTime;
	
	public CollectionStats(long timeSpan, int readings, long totalTime) {
		this.timeSpan = timeSpan;
		this.readingsTaken = readings;
		this.totalSaveTime = totalTime;
	}

	public long getTimeSpan() {
		return timeSpan;
	}

	public int getReadingsTaken() {
		return readingsTaken;
	}

	public long getTotalSaveTime() {
		return totalSaveTime;
	}
	
	public long getReadingsPerSecond() {
		return (readingsTaken * 1000) / timeSpan;
	}
	
	public long getSaveTimePerSecond() {
		double p = getReadingsPerSecond();
		p = p / readingsTaken;
		p = totalSaveTime * p;
		return (long)p;
	}
}
