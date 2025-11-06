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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.learnlib.algorithm.lstar.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithm.lstar.closing.ClosingStrategies;
import de.learnlib.algorithm.lstar.dfa.ExtensibleLStarDFA;
import de.learnlib.oracle.equivalence.DFASimulatorEQOracle;
import de.learnlib.oracle.membership.SimulatorOracle;
import de.learnlib.profiling.memprobe.MemProbeChart;
import de.learnlib.profiling.memprobe.MemProbeSample;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.FastAlphabet;
import net.automatalib.alphabet.impl.Symbol;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.util.automaton.random.RandomAutomata;

public final class LearnDFA {

    private LearnDFA() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        // Uncomment for use with VisualVM
        // System.in.read();

        Alphabet<Symbol<String>> inputs = new FastAlphabet<>(new Symbol<>("a"), new Symbol<>("b"), new Symbol<>("c"));

        DFA<?, Symbol<String>> dfa = RandomAutomata.randomDeterministic(new Random(ProfileUtil.DEFAULT_SEED),
                                                                ProfileUtil.DEFAULT_SIZE,
                                                                inputs,
                                                                Arrays.asList(true, false),
                                                                null,
                                                                new CompactDFA<>(inputs));

        System.err.println("Target has " + dfa.size() + " states");

        SimulatorOracle<Symbol<String>, Boolean> simoracle = new SimulatorOracle<>(dfa);
        DFASimulatorEQOracle<Symbol<String>> eqoracle = new DFASimulatorEQOracle<>(dfa);

        NamedLearnerList<DFA<?, Symbol<String>>, Symbol<String>, Boolean> algos = new NamedLearnerList<>();

        algos.addLearner("ExtensibleLStarDFA",
                         new ExtensibleLStarDFA<>(inputs,
                                                  simoracle,
                                                  Collections.emptyList(),
                                                  ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
                                                  ClosingStrategies.CLOSE_FIRST));
        algos.addLearner("ExtensibleLStarDFAClassic",
                         new ExtensibleLStarDFA<>(inputs,
                                                  simoracle,
                                                  Collections.emptyList(),
                                                  ObservationTableCEXHandlers.CLASSIC_LSTAR,
                                                  ClosingStrategies.CLOSE_FIRST));

        Map<String, List<MemProbeSample>> memdata = ProfileUtil.profileLearners(algos, inputs, eqoracle);

        MemProbeChart.displayMemoryCharts(memdata);
    }

}
