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
package de.learnlib.profiling.benchmark.learner.dfa;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import de.learnlib.algorithm.LearningAlgorithm.DFALearner;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.profiling.benchmark.AbstractBenchmark;
import de.learnlib.profiling.benchmark.Benchmark;
import de.learnlib.profiling.generator.ActiveDFALearner;
import de.learnlib.profiling.generator.DFABenchmarkInput;
import de.learnlib.util.Experiment.DFAExperiment;
import net.automatalib.alphabet.Alphabet;

public abstract class AbstractDFALearningBenchmark extends AbstractBenchmark {

    @Override
    public int[] getParameters() {
        return new int[] {25, 50, 75, 100};
    }

    @Override
    public int getIterations() {
        return 5;
    }

    @Override
    public Map<String, Benchmark> getBenchmarks() {
        final Map<String, Benchmark> benchmarks = new HashMap<>();

        benchmarks.put("LStar", this::getLStarLearner);
        benchmarks.put("DT", this::getDTLearner);
        benchmarks.put("KV", this::getKVLearner);
        benchmarks.put("TTT", this::getTTTLearner);

        return benchmarks;
    }

    private long getLStarLearner(int iter, int param) {
        return buildBenchmark(new DFABenchmarkInput(iter, param), ActiveDFALearner.LSTAR::buildInstance);
    }

    private long getDTLearner(int iter, int param) {
        return buildBenchmark(new DFABenchmarkInput(iter, param), ActiveDFALearner.OP::buildInstance);
    }

    private long getKVLearner(int iter, int param) {
        return buildBenchmark(new DFABenchmarkInput(iter, param), ActiveDFALearner.KV::buildInstance);
    }

    private long getTTTLearner(int iter, int param) {
        return buildBenchmark(new DFABenchmarkInput(iter, param), ActiveDFALearner.TTT::buildInstance);
    }

    private long buildBenchmark(DFABenchmarkInput input,
                                BiFunction<Alphabet<Character>, DFAMembershipOracle<Character>, DFALearner<Character>> learnerBuilder) {

        final DFACounterOracle<Character> oracle = new DFACounterOracle<>(input.getMembershipOracle());

        final DFALearner<Character> learnerDFA = learnerBuilder.apply(input.getAlphabet(), oracle);

        final DFAExperiment<Character> experiment =
                new DFAExperiment<>(learnerDFA, input.getEquivalenceOracle(), input.getAlphabet());

        experiment.run();

        return extractBenchmarkValue(oracle, learnerDFA);

    }

    protected abstract long extractBenchmarkValue(DFACounterOracle<Character> oracle, DFALearner<Character> learner);
}
