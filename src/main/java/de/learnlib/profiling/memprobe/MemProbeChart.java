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

import javax.swing.JFrame;

import net.automatalib.commons.util.Pair;

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
public class MemProbeChart {

	public static void displayMemoryCharts(List<Pair<String, List<MemProbeSample>>> namedSamples) {

		final XYSeriesCollection collection = new XYSeriesCollection();

		for(Pair<String,List<MemProbeSample>> ns : namedSamples) {
			List<MemProbeSample> samples = ns.getSecond();

			XYSeries series = new XYSeries(ns.getFirst());

			for (int i = 0; i < samples.size(); ++i) {
				MemProbeSample sample = samples.get(i);

				//series.add(percentage, sample.usedMemory);
				series.add(sample.time, sample.usedMemory);
			}

			collection.addSeries(series);

		}

		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Memory consumption", // chart title
				"Time (ms)", // domain axis label
				"Used memory (bytes)", // range axis label
				collection, // data
				PlotOrientation.VERTICAL,
				true, // include legend
				true,
				false);

		
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
