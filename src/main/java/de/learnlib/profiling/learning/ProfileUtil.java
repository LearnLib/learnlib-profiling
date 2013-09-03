package de.learnlib.profiling.learning;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.automatalib.automata.UniversalDeterministicAutomaton;
import de.learnlib.api.EquivalenceOracle;
import de.learnlib.api.LearningAlgorithm;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.profiling.memprobe.MemProbeSample;
import de.learnlib.profiling.memprobe.MemProbeThread;

public class ProfileUtil {

	
	public static <I,O,A extends UniversalDeterministicAutomaton<?, I, ?, ?, ?>>
	Map<String,List<MemProbeSample>> profileLearners(
			List<? extends NamedLearner<A,I,O>> algos,
			Collection<? extends I> inputs,
			EquivalenceOracle<? super A, I, O> eqOracle) {
		
		Map<String,List<MemProbeSample>> memdata = new HashMap<>();
		
		for(NamedLearner<A,I,O> p : algos) {
			String name = p.getName();
			LearningAlgorithm<? extends A,I,O> learner = p.getLearner();
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
			memdata.put(name, memprobe.getSamples());
		}

		return memdata;
	}

}
