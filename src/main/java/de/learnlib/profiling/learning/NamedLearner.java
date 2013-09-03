package de.learnlib.profiling.learning;

import de.learnlib.api.LearningAlgorithm;

public class NamedLearner<M,I,O> {

	private final String name;
	private final LearningAlgorithm<? extends M,I,O> learner;
	
	public static <M,I,O> NamedLearner<M,I,O> create(String name, LearningAlgorithm<M, I, O> learner) {
		return new NamedLearner<M,I,O>(name, learner);
	}
	
	public NamedLearner(String name, LearningAlgorithm<? extends M, I, O> learner) {
		this.name = name;
		this.learner = learner;
	}


	public String getName() {
		return name;
	}
	
	public LearningAlgorithm<? extends M,I,O> getLearner() {
		return learner;
	}

}
