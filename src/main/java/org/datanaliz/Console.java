package org.datanaliz;

import org.datanaliz.chart.ExpressionDistributionChart;
import org.datanaliz.chart.XYChart;
import org.datanaliz.expression.CCLE;
import org.datanaliz.expression.ExpSet;
import org.datanaliz.expression.GEOSeries;
import org.datanaliz.stat.Histogram;
import org.jfree.chart.demo.TimeSeriesChartDemo1;
import org.jfree.ui.RefineryUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Console
{
	public static void main (String[] args)
	{
		if (args.length > 0)
		{
			if (args[0].equals("expdist") && args.length > 2)
			{
				displayExpressionDensity(args[1], Arrays.asList(args).subList(2, args.length));
			}
		}
	}
	
	private static void displayExpressionDensity(String dataset, List<String> sms)
	{
		ExpSet expSet;

		if (dataset.equals("CCLE"))
		{
			expSet = new CCLE(sms).getExpSet();
		}
		else
		{
			expSet = new GEOSeries(dataset, sms).getExpSet();
		}

		ExpressionDistributionChart chart = new ExpressionDistributionChart(expSet);
		chart.open();
	}
}
