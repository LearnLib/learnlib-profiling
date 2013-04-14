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
package de.learnlib.profiling.learning;

import de.learnlib.algorithms.lstargeneric.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstargeneric.closing.ClosingStrategies;
import de.learnlib.algorithms.lstargeneric.mealy.ClassicLStarMealy;
import de.learnlib.api.LearningAlgorithm;
import de.learnlib.dhc.mealy.MealyDHC;
import de.learnlib.eqtests.basic.SimulatorEQOracle;
import de.learnlib.examples.mealy.ExampleRandomlyGenerated;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.oracles.SimulatorOracle;
import de.learnlib.profiling.memprobe.MemProbeChart;
import de.learnlib.profiling.memprobe.MemProbeSample;
import de.learnlib.profiling.memprobe.MemProbeThread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.automatalib.automata.UniversalDeterministicAutomaton;
import net.automatalib.automata.transout.impl.FastMealy;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.FastAlphabet;
import net.automatalib.words.impl.Symbol;

/**
 * A simple example for proviling Mealy learning algorithms.
 *
 * @author Maik Merten <maikmerten@googlemail.com>
 */
public class LearnMealy {

	public static void main(String[] args) {

		Alphabet<Symbol> inputs = new FastAlphabet<>(
				new Symbol("a"),
				new Symbol("b"),
				new Symbol("c"));

		Alphabet<Symbol> outputs = new FastAlphabet<>(
				new Symbol("o1"),
				new Symbol("o2"),
				new Symbol("o3"));


		FastMealy<Symbol, Symbol> fm = ExampleRandomlyGenerated.constructMachine(inputs, outputs, new Random(1337), 100000);
		Alphabet<Symbol> alphabet = fm.getInputAlphabet();


		SimulatorOracle<Symbol, Word<Symbol>> simoracle = new SimulatorOracle<>(fm);
		SimulatorEQOracle<Symbol, Word<Symbol>> eqoracle = new SimulatorEQOracle<>(fm);



		Map<String, LearningAlgorithm> algos = new HashMap<>();

		algos.put("MealyDHC", new MealyDHC(alphabet, simoracle));
		algos.put("ClasicLStarMealy", ClassicLStarMealy.createForWordOracle(alphabet, simoracle, new ArrayList<Word<Symbol>>(), ObservationTableCEXHandlers.RIVEST_SCHAPIRE, ClosingStrategies.CLOSE_FIRST));

		Map<String, List<MemProbeSample>> memdata = new HashMap<>();

		for (String learnerName : algos.keySet()) {
			System.out.println("=== " + learnerName + " ===");
			
			LearningAlgorithm learner = algos.get(learnerName);

			MemProbeThread memprobe = new MemProbeThread(250);
			memprobe.start();

			DefaultQuery<Symbol, Word<Symbol>> counterexample = null;
			do {
				if (counterexample == null) {
					learner.startLearning();
				} else {
					System.out.println("found counterexample: " + counterexample.getInput() + " / " + counterexample.getOutput());
					learner.refineHypothesis(counterexample);
				}
				UniversalDeterministicAutomaton hypothesis = (UniversalDeterministicAutomaton) learner.getHypothesisModel();
				System.out.println("hypothesis has " + hypothesis.size() + " states");

				counterexample = eqoracle.findCounterExample(hypothesis, alphabet);

			} while (counterexample != null);

			memprobe.stopProbing();

			memdata.put(learnerName, memprobe.getSamples());
			algos.put(learnerName, null);

		}

		MemProbeChart.displayMemoryCharts(memdata);


	}
}
