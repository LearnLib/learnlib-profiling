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
package de.learnlib.profiling.learning;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.learnlib.algorithms.dhc.mealy.MealyDHC;
import de.learnlib.algorithms.lstar.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstar.closing.ClosingStrategies;
import de.learnlib.algorithms.lstar.mealy.ClassicLStarMealy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.oracle.equivalence.MealySimulatorEQOracle;
import de.learnlib.oracle.membership.SimulatorOracle;
import de.learnlib.profiling.memprobe.MemProbeChart;
import de.learnlib.profiling.memprobe.MemProbeSample;
import de.learnlib.util.mealy.MealyUtil;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.util.automata.random.RandomAutomata;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.FastAlphabet;
import net.automatalib.words.impl.Symbol;

/**
 * A simple example for profiling Mealy learning algorithms.
 *
 * @author Maik Merten <a href="mailto:maikmerten@googlemail.com">maikmerten@googlemail.com</a>
 */
public final class LearnMealy {

    private LearnMealy() {
        // prevent instantiation
    }

    public static void main(String[] args) {

        // Uncomment for use with VisualVM
        // System.in.read();

        Alphabet<Symbol> inputs = new FastAlphabet<>(new Symbol("a"), new Symbol("b"), new Symbol("c"));

        List<String> outputs = Arrays.asList("o1", "o2", "o3");

        MealyMachine<?, Symbol, ?, String> fm = RandomAutomata.randomDeterministic(new Random(ProfileUtil.DEFAULT_SEED),
                                                                                   ProfileUtil.DEFAULT_SIZE,
                                                                                   inputs,
                                                                                   null,
                                                                                   outputs,
                                                                                   new CompactMealy<>(inputs));

        SimulatorOracle<Symbol, Word<String>> simoracle = new SimulatorOracle<>(fm);
        MealySimulatorEQOracle<Symbol, String> eqoracle = new MealySimulatorEQOracle<>(fm);

        NamedLearnerList<MealyMachine<?, Symbol, ?, String>, Symbol, Word<String>> algos = new NamedLearnerList<>();

        algos.addLearner("MealyDHC", new MealyDHC<>(inputs, simoracle));
        algos.addLearner("ClassicLStarMealy",
                         MealyUtil.wrapSymbolLearner(ClassicLStarMealy.createForWordOracle(inputs,
                                                                                           simoracle,
                                                                                           ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
                                                                                           ClosingStrategies.CLOSE_FIRST)));
        algos.addLearner("ExtensibleLStarMealy",
                         new ExtensibleLStarMealy<>(inputs,
                                                    simoracle,
                                                    Collections.emptyList(),
                                                    ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
                                                    ClosingStrategies.CLOSE_FIRST));

        Map<String, List<MemProbeSample>> memdata = ProfileUtil.profileLearners(algos, inputs, eqoracle);

        MemProbeChart.displayMemoryCharts(memdata);

    }
}
