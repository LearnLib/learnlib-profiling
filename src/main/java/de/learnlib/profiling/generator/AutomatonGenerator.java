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

import java.util.Collections;
import java.util.Random;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.VPAlphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.alphabet.impl.DefaultVPAlphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.automaton.fsa.impl.FastDFA;
import net.automatalib.automaton.transducer.MealyMachine;
import net.automatalib.automaton.transducer.impl.CompactMealy;
import net.automatalib.automaton.transducer.impl.FastMealy;
import net.automatalib.automaton.vpa.impl.DefaultOneSEVPA;
import net.automatalib.util.automaton.random.RandomAutomata;

public final class AutomatonGenerator {

    public static final Alphabet<Character> INPUT_ALPHABET;
    public static final Alphabet<Character> OUTPUT_ALPHABET;
    public static final VPAlphabet<Character> VPD_ALPHABET;

    static {
        INPUT_ALPHABET = Alphabets.characters('1', '6');
        OUTPUT_ALPHABET = Alphabets.characters('a', 'f');

        final Alphabet<Character> returnAlphabet = Alphabets.characters('A', 'C');

        VPD_ALPHABET = new DefaultVPAlphabet<>(INPUT_ALPHABET, OUTPUT_ALPHABET, returnAlphabet);
    }

    private AutomatonGenerator() {
        // prevent instantiation
    }

    public static DFA<?, Character> generateCompactDFA(int seed, int size) {
        return RandomAutomata.randomDeterministic(new Random(seed),
                                                  size,
                                                  INPUT_ALPHABET,
                                                  DFA.STATE_PROPERTIES,
                                                  DFA.TRANSITION_PROPERTIES,
                                                  new CompactDFA<>(INPUT_ALPHABET));
    }

    public static DFA<?, Character> generateFastDFA(int seed, int size) {
        return RandomAutomata.randomDeterministic(new Random(seed),
                                                  size,
                                                  INPUT_ALPHABET,
                                                  DFA.STATE_PROPERTIES,
                                                  DFA.TRANSITION_PROPERTIES,
                                                  new FastDFA<>(INPUT_ALPHABET));
    }

    public static MealyMachine<?, Character, ?, Character> generateCompactMealy(int seed, int size) {
        return RandomAutomata.randomDeterministic(new Random(seed),
                                                  size,
                                                  INPUT_ALPHABET,
                                                  Collections.singleton(null),
                                                  OUTPUT_ALPHABET,
                                                  new CompactMealy<>(INPUT_ALPHABET));
    }

    public static MealyMachine<?, Character, ?, Character> generateFastMealy(int seed, int size) {
        return RandomAutomata.randomDeterministic(new Random(seed),
                                                  size,
                                                  INPUT_ALPHABET,
                                                  Collections.singleton(null),
                                                  OUTPUT_ALPHABET,
                                                  new FastMealy<>(INPUT_ALPHABET));
    }

    public static DefaultOneSEVPA<Character> generateDefaultOneSEVPA(int seed, int size) {
        return RandomAutomata.randomOneSEVPA(new Random(seed), size, VPD_ALPHABET, 0.3, 0.3, false);
    }
}
