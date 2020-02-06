/* Copyright (C) 2013-2020 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
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
package de.learnlib.profiling.benchmark.automata;

import java.util.HashMap;
import java.util.Map;

import de.learnlib.profiling.benchmark.AbstractBenchmark;
import de.learnlib.profiling.benchmark.Benchmark;
import de.learnlib.profiling.generator.AutomatonGenerator;
import net.automatalib.automata.transducers.MealyMachine;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author frohme
 */
public class MealySizeBenchmark extends AbstractBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(MealySizeBenchmark.class);

    public static void main(String[] args) {
        final MealySizeBenchmark benchmark = new MealySizeBenchmark();

        benchmark.run();
        benchmark.plot();
    }

    @Override
    public int[] getParameters() {
        return new int[] {100, 250, 500, 1000};
    }

    @Override
    public int getIterations() {
        return 1;
    }

    @Override
    public Map<String, Benchmark> getBenchmarks() {
        final Map<String, Benchmark> result = new HashMap<>();

        result.put("CompactMealy", this::compactMealyBenchmark);
        result.put("FastMealy", this::compactFastBenchmark);

        return result;
    }

    @Override
    public String getName() {
        return "Memory Footprint";
    }

    private long compactMealyBenchmark(int iter, int param) {
        final MealyMachine<?, Character, ?, Character> mealy =
                AutomatonGenerator.generateCompactMealy(iter, Math.toIntExact(param));

        final GraphLayout graphLayout = GraphLayout.parseInstance(mealy);

        LOGGER.info("CompactMealy: iter = {}, param = {}\n{}", iter, param, graphLayout.toFootprint());

        return graphLayout.totalSize();
    }

    private long compactFastBenchmark(int iter, int param) {
        final MealyMachine<?, Character, ?, Character> mealy =
                AutomatonGenerator.generateFastMealy(iter, Math.toIntExact(param));

        final GraphLayout graphLayout = GraphLayout.parseInstance(mealy);

        LOGGER.info("FastMealy: iter = {}, param = {}\n{}", iter, param, graphLayout.toFootprint());

        return graphLayout.totalSize();
    }
}
