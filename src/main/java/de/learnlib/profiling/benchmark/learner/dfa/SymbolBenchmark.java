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
package de.learnlib.profiling.benchmark.learner.dfa;

import de.learnlib.algorithm.LearningAlgorithm.DFALearner;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;

public class SymbolBenchmark extends AbstractDFALearningBenchmark {

    public static void main(String[] args) {
        final SymbolBenchmark benchmark = new SymbolBenchmark();

        benchmark.run();
        benchmark.plot();
    }

    @Override
    protected long extractBenchmarkValue(DFACounterOracle<Character> oracle, DFALearner<Character> learner) {
        return oracle.getSymbolCounter().getCount();
    }

    @Override
    public String getName() {
        return "Symbol Count";
    }
}
