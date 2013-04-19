package de.learnlib.profiling.learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.automatalib.automata.UniversalDeterministicAutomaton;
import net.automatalib.commons.util.Pair;
import de.learnlib.api.EquivalenceOracle;
import de.learnlib.api.LearningAlgorithm;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.profiling.memprobe.MemProbeSample;
import de.learnlib.profiling.memprobe.MemProbeThread;

public class ProfileUtil {

	
	public static <I,O,A extends UniversalDeterministicAutomaton<?, I, ?, ?, ?>>
	List<Pair<String,List<MemProbeSample>>> profileLearners(
			List<Pair<String,? extends LearningAlgorithm<? extends A,I,O>>> algos,
			Collection<? extends I> inputs,
			EquivalenceOracle<? super A, I, O> eqOracle) {
		
		List<Pair<String,List<MemProbeSample>>> memdata = new ArrayList<>();
		
		for(Pair<String,? extends LearningAlgorithm<? extends A,I,O>> p : algos) {
			String name = p.getFirst();
			LearningAlgorithm<? extends A,I,O> learner = p.getSecond();
			if(learner == null)
				continue;
			
			System.out.println("=== " + name + " ===");
			
			MemProbeThread memprobe = new MemProbeThread(250);
			memprobe.start();

			DefaultQuery<I, O> counterexample = null;
			
			learner.startLearning();
			do {
				A hyp = learner.getHypothesisModel();
				System.out.println("Hypothesis has " + hyp.size() + " states");
				
				counterexample = eqOracle.findCounterExample(hyp, inputs);
				
				if(counterexample != null)
					learner.refineHypothesis(counterexample);
			} while(counterexample != null);
			

			memprobe.stopProbing();
			memdata.add(Pair.make(name, memprobe.getSamples()));
			p.setSecond(null);
		}

		return memdata;
	}

}
