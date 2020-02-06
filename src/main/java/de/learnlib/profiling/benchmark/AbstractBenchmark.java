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
package de.learnlib.profiling.benchmark;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.google.common.collect.Maps;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author frohme
 */
public abstract class AbstractBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBenchmark.class);

    private List<Map<String, LongSummaryStatistics>> benchmarkResults;

    public abstract int[] getParameters();

    public abstract int getIterations();

    public abstract Map<String, Benchmark> getBenchmarks();

    public abstract String getName();

    public void run() {

        final int[] parameters = getParameters();

        final Map<String, Benchmark> benchmarks = getBenchmarks();
        this.benchmarkResults = new ArrayList<>(parameters.length);

        for (int param : parameters) {

            LOGGER.info("Benchmarking parameter {}", param);

            final Map<String, LongSummaryStatistics> results = Maps.newHashMapWithExpectedSize(benchmarks.size());

            for (Map.Entry<String, Benchmark> b : benchmarks.entrySet()) {
                final String benchmarkName = b.getKey();
                final Benchmark benchmark = b.getValue();

                LongSummaryStatistics summary = IntStream.range(0, getIterations())
                                                         .parallel()
                                                         .mapToObj(iter -> benchmark.benchmark(iter, param))
                                                         .collect(Collectors.summarizingLong(l -> l));

                results.put(benchmarkName, summary);
            }

            this.benchmarkResults.add(results);
        }
    }

    public void plot() {

        final XYSeriesCollection collection = new XYSeriesCollection();

        final int[] params = getParameters();
        final Map<String, Benchmark> benchmarks = getBenchmarks();

        for (String benchmark : benchmarks.keySet()) {

            final XYSeries series = new XYSeries(benchmark);
            series.setDescription(benchmark);

            for (int i = 0; i < this.benchmarkResults.size(); i++) {
                final Map<String, LongSummaryStatistics> results = this.benchmarkResults.get(i);

                series.add(params[i], results.get(benchmark).getAverage());
            }

            collection.addSeries(series);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(getName(), // chart title
                                                                "Input parameters", // domain axis label
                                                                "Output values", // range axis label
                                                                collection, // data
                                                                PlotOrientation.VERTICAL, true, // include legend
                                                                true, false);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setDefaultShapesVisible(true);

        final TableModel tableModel = new ConsumptionTableModel(collection);
        final JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);

        final ChartPanel chartPanel = new ChartPanel(chart);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, new JScrollPane(table));
        splitPane.setResizeWeight(0.75);

        JFrame frame = new JFrame(getName());
        frame.setLayout(new BorderLayout());
        frame.add(splitPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }

    private static final class ConsumptionTableModel extends AbstractTableModel {

        private final Object[][] data;
        private final XYSeriesCollection collection;

        private ConsumptionTableModel(XYSeriesCollection collection) {

            this.collection = collection;
            this.data = new Object[collection.getSeriesCount()][collection.getItemCount(0) + 1];

            for (int i = 0; i < collection.getSeriesCount(); i++) {
                final XYSeries series = collection.getSeries(i);

                data[i][0] = series.getDescription();

                for (int j = 0; j < series.getItemCount(); j++) {
                    data[i][j + 1] = series.getDataItem(j).getY();
                }
            }
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return data[0].length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Experiment";
            }
            return collection.getSeries(0).getDataItem(columnIndex - 1).getX().toString();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
    }
}
