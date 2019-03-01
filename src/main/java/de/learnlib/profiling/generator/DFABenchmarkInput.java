/* Copyright (C) 2013-2019 TU Dortmund
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

import de.learnlib.api.oracle.EquivalenceOracle.DFAEquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.equivalence.SimulatorEQOracle.DFASimulatorEQOracle;
import de.learnlib.oracle.membership.SimulatorOracle.DFASimulatorOracle;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.words.Alphabet;

/**
 * @author frohme
 */
public class DFABenchmarkInput {

    private DFAMembershipOracle<Character> membershipOracle;
    private DFAEquivalenceOracle<Character> equivalenceOracle;

    public DFABenchmarkInput(int seed, int size) {
        final DFA<?, Character> sul = AutomatonGenerator.generateFastDFA(seed, size);
        membershipOracle = new DFASimulatorOracle<>(sul);
        equivalenceOracle = new DFASimulatorEQOracle<>(sul);
    }

    public DFAMembershipOracle<Character> getMembershipOracle() {
        return membershipOracle;
    }

    public DFAEquivalenceOracle<Character> getEquivalenceOracle() {
        return equivalenceOracle;
    }

    public Alphabet<Character> getAlphabet() {
        return AutomatonGenerator.INPUT_ALPHABET;
    }

}