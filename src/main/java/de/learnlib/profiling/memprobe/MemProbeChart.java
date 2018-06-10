/* Copyright (C) 2013-2018 TU Dortmund
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
package de.learnlib.profiling.memprobe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Display data sets gathered by the MemProbeThread.
 *
 * @author maik
 */
public final class MemProbeChart {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;

    private MemProbeChart() {
        // prevent instantiation
    }

    public static void displayMemoryCharts(Map<String, ? extends List<? extends MemProbeSample>> namedSamples) {

        final XYSeriesCollection collection = new XYSeriesCollection();

        final Map<String, Long> memConsumption = new HashMap<>();
        final Map<String, Long> timeConsumption = new HashMap<>();

        for (Map.Entry<String, ? extends List<? extends MemProbeSample>> ns : namedSamples.entrySet()) {
            List<? extends MemProbeSample> samples = ns.getValue();

            String seriesName = ns.getKey();
            XYSeries series = new XYSeries(seriesName);

            long maxmem = 0;
            long maxtime = 0;

            for (int i = 0; i < samples.size(); ++i) {
                MemProbeSample sample = samples.get(i);

                //series.add(percentage, sample.usedMemory);
                series.add(sample.time, sample.usedMemory);
                if (sample.usedMemory > maxmem) {
                    maxmem = sample.usedMemory;
                }
                // search for biggest timestamp, just in case the order of
                // the samples is not monotonic
                if (sample.time > maxtime) {
                    maxtime = sample.time;
                }
            }

            memConsumption.put(seriesName, maxmem);
            timeConsumption.put(seriesName, maxtime);

            collection.addSeries(series);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart("Memory consumption", // chart title
                                                                "Time (ms)", // domain axis label
                                                                "Used memory (bytes)", // range axis label
                                                                collection, // data
                                                                PlotOrientation.VERTICAL, true, // include legend
                                                                true, false);

        final TableModel tableModel = new ConsumptionTableModel(memConsumption, timeConsumption);
        final JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);

        final ChartPanel chartPanel = new ChartPanel(chart);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartPanel, new JScrollPane(table));

        JFrame frame = new JFrame("Memory consumption");
        frame.setLayout(new BorderLayout());
        frame.add(splitPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.pack();
        frame.setVisible(true);
    }

    private static final class ConsumptionTableModel extends AbstractTableModel {

        private Object[][] data;

        private ConsumptionTableModel(Map<String, Long> memConsumption, Map<String, Long> timeConsumption) {
            data = new Object[memConsumption.keySet().size()][3];
            int i = 0;
            for (String seriesName : memConsumption.keySet()) {
                long maxmem = memConsumption.get(seriesName);
                long maxtime = timeConsumption.get(seriesName);
                data[i][0] = seriesName;
                data[i][1] = maxmem;
                data[i][2] = maxtime;
                ++i;
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
            String[] names = {"Algorithm", "max mem (bytes)", "time (ms)"};
            return names[columnIndex];
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
