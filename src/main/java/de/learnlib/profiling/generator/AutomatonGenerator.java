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

import java.util.Collections;
import java.util.Random;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.FastDFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.FastMealy;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.util.automata.random.RandomAutomata;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

/**
 * @author frohme
 */
public final class AutomatonGenerator {

    public static final Alphabet<Character> INPUT_ALPHABET = Alphabets.characters('1', '6');
    public static final Alphabet<Character> OUTPUT_ALPHABET = Alphabets.characters('a', 'f');

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
}
