package org.datanaliz.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;

/**
 * @author Ozgun Babur
 */
public class XYChart extends ApplicationFrame
{
	DefaultXYDataset dataset = new DefaultXYDataset();

	public XYChart(String title)
	{
		super(title);
	}

	private void init()
	{
		ChartPanel chartPanel = createPanel();
		chartPanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(chartPanel);
	}

	public void addSeries(double[] x, double[] y, String name)
	{
		assert x.length == y.length;
		dataset.addSeries(name, new double[][]{x, y});
	}

	private static JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(
			"Title",  // title
			"x",			 // x-axis label
			"y",   // y-axis label
			dataset,			// data
			PlotOrientation.VERTICAL,
			true,			   // create legend?
			true,			   // generate tooltips?
			false			   // generate URLs?
		);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		return chart;

	}

	public ChartPanel createPanel() {
		JFreeChart chart = createChart(dataset);
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		return panel;
	}

	public void open()
	{
		init();
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		XYChart demo = new XYChart("XY chart");
		demo.addSeries(new double[]{1,2,3,4,5,6,7,8,9}, new double[]{3,4,6,8,7,4,6,7,3}, "Random");
		demo.open();
	}

	static
	{
		ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
	}

}
