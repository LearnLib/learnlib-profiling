package de.learnlib.profiling.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.commons.util.Pair;
import net.automatalib.util.automata.random.RandomAutomata;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.FastAlphabet;
import net.automatalib.words.impl.Symbol;
import de.learnlib.algorithms.angluin.Angluin;
import de.learnlib.algorithms.lstargeneric.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstargeneric.closing.ClosingStrategies;
import de.learnlib.algorithms.lstargeneric.dfa.ExtensibleLStarDFA;
import de.learnlib.api.LearningAlgorithm;
import de.learnlib.eqtests.basic.SimulatorEQOracle;
import de.learnlib.oracles.SimulatorOracle;
import de.learnlib.profiling.memprobe.MemProbeChart;
import de.learnlib.profiling.memprobe.MemProbeSample;

public class LearnDFA {

	public static void main(String[] args) throws Exception {
		// Uncomment for use with VisualVM
		// System.in.read();

		Alphabet<Symbol> inputs = new FastAlphabet<>(new Symbol("a"),
				new Symbol("b"), new Symbol("c"));

		DFA<?,Symbol> dfa = RandomAutomata.randomDeterministic(
				new Random(1337), 1000, inputs, Arrays.asList(true, false), null,
				new CompactDFA<Symbol>(inputs));

		SimulatorOracle<Symbol, Boolean> simoracle = new SimulatorOracle<>(
				dfa);
		SimulatorEQOracle<Symbol, Boolean> eqoracle = new SimulatorEQOracle<>(
				dfa);

		List<Pair<String, ? extends LearningAlgorithm<? extends DFA<?,Symbol>, Symbol, Boolean>>> algos = new ArrayList<>();

		algos.add(Pair.make("ExtensibleLStarDFA", new ExtensibleLStarDFA<>(
				inputs, simoracle, Collections.<Word<Symbol>> emptyList(),
				ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
				ClosingStrategies.CLOSE_FIRST)));
		algos.add(Pair.make("ExtensibleLStarDFAClassic", new ExtensibleLStarDFA<>(
				inputs, simoracle, Collections.<Word<Symbol>> emptyList(),
				ObservationTableCEXHandlers.CLASSIC_LSTAR,
				ClosingStrategies.CLOSE_FIRST)));
		algos.add(Pair.make("Angluin", new Angluin<>(inputs, simoracle)));
		
		List<Pair<String, List<MemProbeSample>>> memdata = ProfileUtil
				.profileLearners(algos, inputs, eqoracle);

		MemProbeChart.displayMemoryCharts(memdata);
	}

}
