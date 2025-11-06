/* Copyright (C) 2013-2025 TU Dortmund University
 * This file is part of LearnLib <https://learnlib.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 */
public class MemProbeThread extends Thread {

    private static final int DEFAULT_SAMPLE_INTERVAL = 1000;

    private boolean run = true;
    private long interval;
    private List<MemProbeSample> samples = new ArrayList<>();

    public MemProbeThread() {
        this(DEFAULT_SAMPLE_INTERVAL);
    }

    public MemProbeThread(long sampleInterval) {
        this.interval = sampleInterval;
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();

        long timeOffset = System.currentTimeMillis();
        long timeNow = timeOffset;

        while (run) {
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
