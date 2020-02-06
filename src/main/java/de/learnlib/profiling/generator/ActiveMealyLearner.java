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
package de.learnlib.profiling.generator;

import de.learnlib.algorithms.dhc.mealy.MealyDHCBuilder;
import de.learnlib.algorithms.discriminationtree.mealy.DTLearnerMealyBuilder;
import de.learnlib.algorithms.kv.mealy.KearnsVaziraniMealyBuilder;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.algorithms.ttt.mealy.TTTLearnerMealyBuilder;
import de.learnlib.api.algorithm.LearningAlgorithm.MealyLearner;
import de.learnlib.api.oracle.MembershipOracle;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * @author frohme
 */
public enum ActiveMealyLearner {

    LSTAR {
        @Override
        public <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle) {
            return new ExtensibleLStarMealyBuilder<I, O>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    DHC {
        @Override
        public <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle) {
            return new MealyDHCBuilder<I, O>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    DT {
        @Override
        public <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle) {
            return new DTLearnerMealyBuilder<I, O>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    KV {
        @Override
        public <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle) {
            return new KearnsVaziraniMealyBuilder<I, O>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    TTT {
        @Override
        public <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle) {
            return new TTTLearnerMealyBuilder<I, O>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    };

    public abstract <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle);
}
