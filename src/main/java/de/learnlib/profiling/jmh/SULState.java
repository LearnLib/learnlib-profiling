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
package de.learnlib.profiling.jmh;

import de.learnlib.api.oracle.EquivalenceOracle.DFAEquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.profiling.generator.DFABenchmarkInput;
import net.automatalib.words.Alphabet;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author frohme
 */
@State(Scope.Thread)
public class SULState {

    @Param({"50", "100", "150", "200"})
    public int size;

    private DFABenchmarkInput input;

    @Setup
    public void setup() {
        this.input = new DFABenchmarkInput(0, size);
    }

    public DFAMembershipOracle<Character> getMembershipOracle() {
        return input.getMembershipOracle();
    }

    public DFAEquivalenceOracle<Character> getEquivalenceOracle() {
        return input.getEquivalenceOracle();
    }

    public Alphabet<Character> getAlphabet() {
        return input.getAlphabet();
    }
}
