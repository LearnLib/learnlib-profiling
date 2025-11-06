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
package de.learnlib.profiling.generator;

import de.learnlib.algorithm.LearningAlgorithm.MealyLearner;
import de.learnlib.algorithm.dhc.mealy.MealyDHCBuilder;
import de.learnlib.algorithm.kv.mealy.KearnsVaziraniMealyBuilder;
import de.learnlib.algorithm.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.algorithm.observationpack.mealy.OPLearnerMealyBuilder;
import de.learnlib.algorithm.ttt.mealy.TTTLearnerMealyBuilder;
import de.learnlib.oracle.MembershipOracle;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.word.Word;

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
    OP {
        @Override
        public <I, O> MealyLearner<I, O> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Word<O>> oracle) {
            return new OPLearnerMealyBuilder<I, O>().withAlphabet(alphabet).withOracle(oracle).create();
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
