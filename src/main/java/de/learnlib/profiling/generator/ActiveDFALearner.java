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

import de.learnlib.algorithm.LearningAlgorithm.DFALearner;
import de.learnlib.algorithm.kv.dfa.KearnsVaziraniDFABuilder;
import de.learnlib.algorithm.lstar.dfa.ExtensibleLStarDFABuilder;
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFABuilder;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFABuilder;
import de.learnlib.oracle.MembershipOracle;
import net.automatalib.alphabet.Alphabet;

public enum ActiveDFALearner {

    LSTAR {
        @Override
        public <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle) {
            return new ExtensibleLStarDFABuilder<I>().withAlphabet(alphabet).withOracle(oracle).create();
        }
    },
    OP {
        @Override
        public <I> DFALearner<I> buildInstance(Alphabet<I> alphabet, MembershipOracle<I, Boolean> oracle) {
            return new OPLearnerDFABuilder<I>().withAlphabet(alphabet).withOracle(oracle).create();
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
