package de.learnlib.profiling.learning;

import java.util.ArrayList;

import de.learnlib.api.LearningAlgorithm;


public class NamedLearnerList<M, I, O> extends ArrayList<NamedLearner<M, I, O>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void addLearner(String name, LearningAlgorithm<? extends M,I,O> learner) {
		add(new NamedLearner<>(name, learner));
	}

}
