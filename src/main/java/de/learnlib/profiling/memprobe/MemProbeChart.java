/* Copyright (C) 2013 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 * 
 * LearnLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * LearnLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with LearnLib; if not, see
 * <http://www.gnu.de/documents/lgpl.en.html>.
 */
package de.learnlib.profiling.memprobe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Display data sets gathered by the MemProbeThread.
 *
 * @author maik
 */
public class MemProbeChart {

	public static void displayMemoryCharts(Map<String, List<MemProbeSample>> sampleMap) {

		final XYSeriesCollection collection = new XYSeriesCollection();

		for (String seriesName : sampleMap.keySet()) {
			List<MemProbeSample> samples = sampleMap.get(seriesName);

			XYSeries series = new XYSeries(seriesName);

			for (int i = 0; i < samples.size(); ++i) {
				MemProbeSample sample = samples.get(i);

				float percentage = ((float) i) / ((float) samples.size() - 1);
				percentage *= 100f;

				series.add(percentage, sample.usedMemory);
			}

			collection.addSeries(series);

		}

		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Memory consumption", // chart title
				"Progress (%)", // domain axis label
				"Used memory (bytes)", // range axis label
				collection, // data
				PlotOrientation.VERTICAL,
				true, // include legend
				true,
				false);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(1, false);
		chart.getXYPlot().setRenderer(renderer);
		
		
		final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        
		JFrame frame = new JFrame("Memory consumption");
		frame.setLayout(new BorderLayout());
		frame.add(chartPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setVisible(true);
	}
}
