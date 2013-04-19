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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.automata.transout.impl.FastMealy;
import net.automatalib.commons.util.Pair;
import net.automatalib.util.automata.random.RandomAutomata;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.FastAlphabet;
import net.automatalib.words.impl.Symbol;
import de.learnlib.algorithms.lstargeneric.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstargeneric.closing.ClosingStrategies;
import de.learnlib.algorithms.lstargeneric.mealy.ClassicLStarMealy;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealy;
import de.learnlib.api.LearningAlgorithm;
import de.learnlib.dhc.mealy.MealyDHC;
import de.learnlib.eqtests.basic.SimulatorEQOracle;
import de.learnlib.mealy.MealyUtil;
import de.learnlib.oracles.SimulatorOracle;
import de.learnlib.profiling.memprobe.MemProbeChart;
import de.learnlib.profiling.memprobe.MemProbeSample;

/**
 * A simple example for proviling Mealy learning algorithms.
 *
 * @author Maik Merten <maikmerten@googlemail.com>
 */
public class LearnMealy {

	public static void main(String[] args) throws Exception {
		
		// Uncomment for use with VisualVM
		// System.in.read();

		Alphabet<Symbol> inputs = new FastAlphabet<>(
				new Symbol("a"),
				new Symbol("b"),
				new Symbol("c"));

		List<String> outputs = Arrays.asList("o1", "o2", "o3");


		FastMealy<Symbol,String> fm = RandomAutomata.randomDeterministic(new Random(1337),
				100000, inputs, null, outputs, new FastMealy<Symbol,String>(inputs));


		SimulatorOracle<Symbol, Word<String>> simoracle = new SimulatorOracle<>(fm);
		SimulatorEQOracle<Symbol, Word<String>> eqoracle = new SimulatorEQOracle<>(fm);



		List<Pair<String,? extends LearningAlgorithm<? extends MealyMachine<?,Symbol,?,String>,Symbol,Word<String>>>> algos
					= new ArrayList<>();
					
		
		algos.add(Pair.make("MealyDHC", new MealyDHC<>(inputs, simoracle)));
		algos.add(Pair.make("ClassicLStarMealy",
				MealyUtil.wrapSymbolLearner(
						ClassicLStarMealy.createForWordOracle(
								inputs,
								simoracle,
								Collections.<Word<Symbol>>emptyList(),
								ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
								ClosingStrategies.CLOSE_FIRST))));
		algos.add(Pair.make("ExtensibleLStarMealy", new ExtensibleLStarMealy<>(inputs, simoracle, Collections.<Word<Symbol>>emptyList(), ObservationTableCEXHandlers.RIVEST_SCHAPIRE, ClosingStrategies.CLOSE_FIRST)));

		List<Pair<String,List<MemProbeSample>>> memdata
			= ProfileUtil.profileLearners(algos, inputs, eqoracle);

		MemProbeChart.displayMemoryCharts(memdata);


	}
}
