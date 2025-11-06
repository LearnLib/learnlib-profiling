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
package de.learnlib.profiling.benchmark.automata;

import java.util.HashMap;
import java.util.Map;

import de.learnlib.profiling.benchmark.AbstractBenchmark;
import de.learnlib.profiling.benchmark.Benchmark;
import de.learnlib.profiling.generator.AutomatonGenerator;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactNFA;
import net.automatalib.util.automaton.copy.AutomatonCopyMethod;
import net.automatalib.util.automaton.copy.AutomatonLowLevelCopy;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NFASizeBenchmark extends AbstractBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(NFASizeBenchmark.class);

    public static void main(String[] args) {
        final NFASizeBenchmark benchmark = new NFASizeBenchmark();

        benchmark.run();
        benchmark.plot();
    }

    @Override
    public int[] getParameters() {
        return new int[] {1000, 2500, 5000};
    }

    @Override
    public int getIterations() {
        return 1;
    }

    @Override
    public Map<String, Benchmark> getBenchmarks() {
        final Map<String, Benchmark> result = new HashMap<>();

        result.put("CompactDFA", this::compactDFABenchmark);
        result.put("CompactNFA", this::compactNFABenchmark);

        return result;
    }

    @Override
    public String getName() {
        return "Memory Footprint";
    }

    private long compactDFABenchmark(int iter, int param) {
        final DFA<?, Character> dfa = AutomatonGenerator.generateCompactDFA(iter, param);

        final GraphLayout graphLayout = GraphLayout.parseInstance(dfa);

        LOGGER.info("CompactDFA: iter = {}, param = {}\n{}", iter, param, graphLayout.toFootprint());

        return graphLayout.totalSize();
    }

    private long compactNFABenchmark(int iter, int param) {
        final DFA<?, Character> dfa = AutomatonGenerator.generateCompactDFA(iter, param);
        final CompactNFA<Character> nfa = new CompactNFA<>(AutomatonGenerator.INPUT_ALPHABET);
        AutomatonLowLevelCopy.copy(AutomatonCopyMethod.STATE_BY_STATE, dfa, AutomatonGenerator.INPUT_ALPHABET, nfa);

        final GraphLayout graphLayout = GraphLayout.parseInstance(nfa);

        LOGGER.info("CompactNFA: iter = {}, param = {}\n{}", iter, param, graphLayout.toFootprint());

        return graphLayout.totalSize();
    }

}
