/* Copyright (C) 2013 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 * 
 * LearnLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * LearnLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with LearnLib; if not, see
 * <http://www.gnu.de/documents/lgpl.en.html>.
 */

package de.learnlib.profiling.memprobe;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A thread that records data on memory consumption in regular intervals.
 * This can only provide a (very) rough estimate on memory consumption. Due
 * to triggering the garbage collection when sampling undesirable effects
 * may occur, so do not use this in production environments!
 * 
 * @author Maik Merten <maikmerten@googlemail.com>
 */
public class MemProbeThread extends Thread {
	
	private boolean run = true;
	private long timeOffset;
	private long interval;
	private List<MemProbeSample> samples = new ArrayList<>();
	
	public MemProbeThread() {
		this(1000);
	}
	
	public MemProbeThread(long sampleInterval) {
		this.interval = sampleInterval;
	}
	
	
	
	@Override
	public void run() {
		Runtime runtime = Runtime.getRuntime();
		
		
		this.timeOffset = System.currentTimeMillis();
		long timeNow = timeOffset;
		
		
		while(run) {
			long time = timeNow - timeOffset;
			
			// this is why this probe should not be used in production!
			System.gc();
			
			long usedMem = runtime.totalMemory() - runtime.freeMemory();
			
			MemProbeSample sample = new MemProbeSample(time, usedMem);
			samples.add(sample);
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ex) {
				Logger.getLogger(MemProbeThread.class.getName()).log(Level.SEVERE, null, ex);
			}
			
			timeNow = System.currentTimeMillis();
		}
		
	}
	
	public void stopProbing() {
		run = false;
	}
	
	public List<MemProbeSample> getSamples() {
		return samples;
	}
	
	
}
