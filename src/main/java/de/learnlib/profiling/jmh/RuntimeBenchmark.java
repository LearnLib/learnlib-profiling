/* Copyright (C) 2013-2019 TU Dortmund
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
package de.learnlib.profiling.jmh;

import de.learnlib.api.algorithm.LearningAlgorithm.DFALearner;
import de.learnlib.profiling.generator.ActiveDFALearner;
import de.learnlib.util.Experiment.DFAExperiment;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author frohme
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 10, time = 5)
@Fork(1)
public class RuntimeBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(RuntimeBenchmark.class.getSimpleName()).build();

        new Runner(opt).run();
    }

    @Benchmark
    public void benchLStar(SULState state, Blackhole blackhole) {
        final DFALearner<Character> learnerDFA =
                ActiveDFALearner.LSTAR.buildInstance(state.getAlphabet(), state.getMembershipOracle());

        final DFAExperiment<Character> experiment =
                new DFAExperiment<>(learnerDFA, state.getEquivalenceOracle(), state.getAlphabet());

        experiment.run();

        blackhole.consume(experiment.getFinalHypothesis());
    }

    @Benchmark
    public void benchTTT(SULState state, Blackhole blackhole) {
        final DFALearner<Character> learnerDFA =
                ActiveDFALearner.TTT.buildInstance(state.getAlphabet(), state.getMembershipOracle());

        final DFAExperiment<Character> experiment =
                new DFAExperiment<>(learnerDFA, state.getEquivalenceOracle(), state.getAlphabet());

        experiment.run();

        blackhole.consume(experiment.getFinalHypothesis());
    }

}
