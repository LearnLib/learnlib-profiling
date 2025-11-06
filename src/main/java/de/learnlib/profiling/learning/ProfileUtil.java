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
package de.learnlib.profiling.learning;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.learnlib.algorithm.LearningAlgorithm;
import de.learnlib.oracle.EquivalenceOracle;
import de.learnlib.profiling.memprobe.MemProbeSample;
import de.learnlib.profiling.memprobe.MemProbeThread;
import de.learnlib.query.DefaultQuery;
import net.automatalib.automaton.UniversalDeterministicAutomaton;

public final class ProfileUtil {

    public static final int DEFAULT_SEED = 1337;
    public static final int DEFAULT_SIZE = 10000;
    public static final int DEFAULT_SAMPLE_INTERVAL = 250;

    private ProfileUtil() {
        // prevent instantiation
    }

    public static <I, O, A extends UniversalDeterministicAutomaton<?, I, ?, ?, ?>> Map<String, List<MemProbeSample>> profileLearners(
            List<? extends NamedLearner<A, I, O>> algos,
            Collection<? extends I> inputs,
            EquivalenceOracle<? super A, I, O> eqOracle) {

        Map<String, List<MemProbeSample>> memdata = new HashMap<>();

        for (NamedLearner<A, I, O> p : algos) {
            String name = p.getName();
            LearningAlgorithm<? extends A, I, O> learner = p.getLearner();
            if (learner == null) {
                continue;
            }

            System.out.println("=== " + name + " ===");

            MemProbeThread memprobe = new MemProbeThread(DEFAULT_SAMPLE_INTERVAL);
            memprobe.start();

            DefaultQuery<I, O> counterexample = null;

            learner.startLearning();
            do {
                A hyp = learner.getHypothesisModel();
                System.out.println("Hypothesis has " + hyp.size() + " states");

                counterexample = eqOracle.findCounterExample(hyp, inputs);

                if (counterexample != null) {
                    learner.refineHypothesis(counterexample);
                }
            } while (counterexample != null);

            memprobe.stopProbing();
            memdata.put(name, memprobe.getSamples());
        }

        return memdata;
    }

}
