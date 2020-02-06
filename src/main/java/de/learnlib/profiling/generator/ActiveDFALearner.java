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

import de.learnlib.algorithms.discriminationtree.dfa.DTLearnerDFABuilder;
import de.learnlib.algorithms.kv.dfa.KearnsVaziraniDFABuilder;
import de.learnlib.algorithms.lstar.dfa.ExtensibleLStarDFABuilder;
import de.learnlib.algorithms.ttt.dfa.TTTLearnerDFABuilder;
import de.learnlib.api.algorithm.LearningAlgorithm.DFALearner;
import de.learnlib.api.oracle.MembershipOracle;
import net.automatalib.words.Alphabet;

/**
 * @author frohme
 */
public enum ActiveDFALearner {

    LSTAR {
        @Override
        public <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle) {
            return new ExtensibleLStarDFABuilder<I>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    DT {
        @Override
        public <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle) {
            return new DTLearnerDFABuilder<I>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    KV {
        @Override
        public <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle) {
            return new KearnsVaziraniDFABuilder<I>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    TTT {
        @Override
        public <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle) {
            return new TTTLearnerDFABuilder<I>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    };

    public abstract <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle);
}
