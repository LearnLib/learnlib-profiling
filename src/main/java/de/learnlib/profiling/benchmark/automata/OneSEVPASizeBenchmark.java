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

import de.learnlib.acex.AcexAnalyzers;
import de.learnlib.algorithm.observationpack.vpa.OPLearnerVPA;
import de.learnlib.algorithm.observationpack.vpa.hypothesis.OneSEVPAHypothesis;
import de.learnlib.oracle.EquivalenceOracle;
import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.equivalence.vpa.SimulatorEQOracle;
import de.learnlib.oracle.membership.SEVPASimulatorOracle;
import de.learnlib.profiling.benchmark.AbstractBenchmark;
import de.learnlib.profiling.benchmark.Benchmark;
import de.learnlib.profiling.generator.AutomatonGenerator;
import de.learnlib.util.Experiment;
import net.automatalib.automaton.vpa.OneSEVPA;
import net.automatalib.automaton.vpa.impl.DefaultOneSEVPA;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneSEVPASizeBenchmark extends AbstractBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneSEVPASizeBenchmark.class);

    private final Map<Integer, Map<Integer, DefaultOneSEVPA<Character>>> defaultSevpaMap = new HashMap<>();
    private final Map<Integer, Map<Integer, OneSEVPAHypothesis<Character>>> hypothesisSevpaMap = new HashMap<>();

    public static void main(String[] args) {
        final OneSEVPASizeBenchmark benchmark = new OneSEVPASizeBenchmark();

        benchmark.run();
        benchmark.plot();
    }

    @Override
    public int[] getParameters() {
        return new int[] {100, 250, 500};
    }

    @Override
    public int getIterations() {
        return 1;
    }

    @Override
    public Map<String, Benchmark> getBenchmarks() {
        final Map<String, Benchmark> result = new HashMap<>();

        result.put("DefaultOneSEVPA | size", this::defaultOneSEVPABenchmarkSize);
        result.put("DefaultOneSEVPA | count", this::defaultOneSEVPABenchmarkCount);
        result.put("HypothesisOneSEVPA | size", this::hypothesisOneSEVPABenchmarkSize);
        result.put("HypothesisOneSEVPA | count", this::hypothesisOneSEVPABenchmarkCount);

        return result;
    }

    @Override
    public String getName() {
        return "Memory Footprint";
    }

    private long defaultOneSEVPABenchmarkSize(int iter, int param) {
        final OneSEVPA<?, Character> oneSevpa = getDefaultSEVPA(iter, param);
        final GraphLayout graphLayout = GraphLayout.parseInstance(oneSevpa);

        LOGGER.info("DefaultOneSEVPA: iter = {}, param = {}\n{}", iter, param, graphLayout.toFootprint());

        return graphLayout.totalSize();
    }

    private long defaultOneSEVPABenchmarkCount(int iter, int param) {
        final OneSEVPA<?, Character> oneSevpa = getDefaultSEVPA(iter, param);
        final GraphLayout graphLayout = GraphLayout.parseInstance(oneSevpa);
        return graphLayout.totalCount();
    }

    private long hypothesisOneSEVPABenchmarkSize(int iter, int param) {
        final OneSEVPA<?, Character> oneSevpa = getSEVPAHypothesis(iter, param);
        final GraphLayout graphLayout = GraphLayout.parseInstance(oneSevpa);

        LOGGER.info("HypothesisOneSEVPA: iter = {}, param = {}\n{}", iter, param, graphLayout.toFootprint());

        return graphLayout.totalSize();
    }

    private long hypothesisOneSEVPABenchmarkCount(int iter, int param) {
        final OneSEVPA<?, Character> oneSevpa = getSEVPAHypothesis(iter, param);
        final GraphLayout graphLayout = GraphLayout.parseInstance(oneSevpa);
        return graphLayout.totalCount();
    }

    private DefaultOneSEVPA<Character> getDefaultSEVPA(int iter, int param) {
        synchronized (defaultSevpaMap) {
            final Map<Integer, DefaultOneSEVPA<Character>> iterMap =
                    defaultSevpaMap.computeIfAbsent(iter, k -> new HashMap<>());
            final DefaultOneSEVPA<Character> sevpa =
                    iterMap.computeIfAbsent(param, k -> AutomatonGenerator.generateDefaultOneSEVPA(iter, param));

            return sevpa;
        }
    }

    private OneSEVPAHypothesis<Character> getSEVPAHypothesis(int iter, int param) {
        synchronized (hypothesisSevpaMap) {
            final Map<Integer, OneSEVPAHypothesis<Character>> iterMap =
                    hypothesisSevpaMap.computeIfAbsent(iter, k -> new HashMap<>());

            return iterMap.computeIfAbsent(param, k -> {

                final DefaultOneSEVPA<Character> defaultSEVPA = getDefaultSEVPA(iter, param);
                final DFAMembershipOracle<Character> mq = new SEVPASimulatorOracle<>(defaultSEVPA);
                final EquivalenceOracle<OneSEVPA<?, Character>, Character, Boolean> eq =
                        new SimulatorEQOracle<>(defaultSEVPA);

                final OPLearnerVPA<Character> learner =
                        new OPLearnerVPA<>(defaultSEVPA.getInputAlphabet(), mq, AcexAnalyzers.BINARY_SEARCH_FWD);
                final Experiment<OneSEVPA<?, Character>> experiment =
                        new Experiment<>(learner, eq, defaultSEVPA.getInputAlphabet());

                experiment.run();

                @SuppressWarnings("unchecked")
                final OneSEVPAHypothesis<Character> result =
                        (OneSEVPAHypothesis<Character>) experiment.getFinalHypothesis();

                return result;
            });
        }
    }

}
