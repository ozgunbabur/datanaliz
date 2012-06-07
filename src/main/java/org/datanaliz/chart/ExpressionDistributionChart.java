package org.datanaliz.chart;

import org.datanaliz.expression.ExpSet;
import org.datanaliz.expression.GeneExp;
import org.datanaliz.stat.Summary;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author Ozgun Babur
 */
public class ExpressionDistributionChart extends ApplicationFrame
{
	ExpSet expSet;
	JList geneList;
	ChartPanel chartPanel;
	JComboBox probeSetBox;
	JComboBox sampleBox;
	DefaultComboBoxModel probeModel;
	DefaultComboBoxModel groupModel;
	DefaultComboBoxModel sampleModel;
	ProbesetListener probeListener;
	SampleListener sampleListener;
	Map<String, GeneExp> sm2ge;
	Map<String, String> group2sample;
	TitledBorder groupBorder;
	
	private static final String NONE = "None";
	
	/**
	 * Constructs a new application frame.
	 */
	public ExpressionDistributionChart(ExpSet expSet)
	{
		super("Distribution of Expressions");
		this.expSet = expSet;
	}
	
	protected void init()
	{
		initDefaultProbesets();
		getContentPane().setLayout(new BorderLayout());
		
		// Gene list at the left
		
		geneList = new JList();
		JScrollPane scroll = new JScrollPane(geneList);
		List<String> list = new ArrayList<String>(expSet.getSymbols());
		Collections.sort(list);
		geneList.setListData(list.toArray(new String[list.size()]));
		getContentPane().add(scroll, BorderLayout.WEST);
		geneList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		geneList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateProbesets(selectedSymbol());
			}
		});
		
		// Upper combo boxes
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBackground(Color.WHITE);

		JPanel probePanel = new JPanel();
		probePanel.setBackground(Color.WHITE);
		probePanel.setBorder(new TitledBorder("Probeset"));
		probeSetBox = new JComboBox();
		probeModel = new DefaultComboBoxModel();
		probeListener = new ProbesetListener();
		probeSetBox.addActionListener(probeListener);
		probeSetBox.setModel(probeModel);
		probePanel.add(probeSetBox);
		controlPanel.add(probePanel, BorderLayout.WEST);

		if (expSet.hasSubgroups())
		{
			JPanel groupPanel = new JPanel(new FlowLayout());
			groupPanel.setBackground(Color.WHITE);
			int size = expSet.getExpname().length;
			groupBorder = new TitledBorder("Group -- (" + size + "/" + size + ")");
			groupPanel.setBorder(groupBorder);
			List<String> groups = expSet.getSubgroups();
			groups.add(0, NONE);
			JComboBox groupCombo = new JComboBox();
			groupModel = new DefaultComboBoxModel(groups.toArray());
			groupCombo.setModel(groupModel);
			groupCombo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					updateGroupTitle();
					updateSamples();
					updateChart();
				}
			});
			groupPanel.add(groupCombo);
			controlPanel.add(groupPanel, BorderLayout.CENTER);
		}

		group2sample = new HashMap<String, String>();
		JPanel samplePanel = new JPanel(new FlowLayout());
		samplePanel.setBackground(Color.WHITE);
		samplePanel.setBorder(new TitledBorder("Sample"));
		sampleModel = new DefaultComboBoxModel();
		sampleBox = new JComboBox(sampleModel);
		sampleListener = new SampleListener();
		updateSamples();
		samplePanel.add(sampleBox);
		controlPanel.add(samplePanel, BorderLayout.EAST);

		getContentPane().add(controlPanel, BorderLayout.NORTH);
		
		// Chart

		chartPanel = new ChartPanel(null);
		getContentPane().add(chartPanel, BorderLayout.CENTER);

		// Fire
		geneList.setSelectedIndex(0);
	}
	
	private void initDefaultProbesets()
	{
		sm2ge = new HashMap<String, GeneExp>();
		for (String sm : expSet.getSymbols())
		{
			sm2ge.put(sm, selectMostVaried(expSet.get(sm)));
		}
	}

	private GeneExp selectMostVaried(List<GeneExp> list)
	{
		if (list.size() == 1) return list.get(0);
		double max = -1;
		GeneExp select = null;
		boolean natural = expSet.isNatural();
		for (GeneExp ge : list)
		{
			double var = natural ? ge.variance() : ge.varExped();
			if (var > max)
			{
				max = var;
				select = ge;
			}
		}
		return select;
	}
	
	private String selectedSymbol()
	{
		return geneList.getSelectedValue().toString();
	}
	
	private GeneExp selectedProbeset()
	{
		return (GeneExp) probeModel.getSelectedItem();
	}

	private String selectedGroup()
	{
		if (groupModel == null) return null;
		Object item = groupModel.getSelectedItem();
		if (item == NONE) return null;
		return item.toString();
	}
	
	private String selectedSample()
	{
		Object item = sampleModel.getSelectedItem();
		if (item == NONE || item == null) return null;
		return item.toString();
	}
	
	protected JFreeChart createChart()
	{
		GeneExp ge = selectedProbeset();
		HistogramDataset hd = new HistogramDataset();
		hd.setType(HistogramType.FREQUENCY);

		double min = Summary.min(ge.getValues());
		double max = Summary.max(ge.getValues());
		int bins = 20;

		String sample = selectedSample();
		if (sample != null)
		{
			double v = ge.getValues()[expSet.indexOf(sample)];
			hd.addSeries(sample, new double[]{v}, bins, min, max);
		}
		
		String group = selectedGroup();
		if (group != null)
		{
			hd.addSeries(group, ge.getSubset(expSet.getGroupIndex(group)), bins, min, max);
		}
		hd.addSeries("all samples", ge.getValues(), bins, min, max);
		JFreeChart chart = ChartFactory.createHistogram("Distribution of Expression", "expression",
			"frequency", hd, PlotOrientation.VERTICAL, true, true, true);

		chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		XYPlot plot = chart.getXYPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

		int sampleIndex = sample != null ? 0 : -1;
		int groupIndex = group == null ? -1 : sample == null ? 0 : 1;
		int allIndex = Math.max(sampleIndex, groupIndex) + 1;

		if (sampleIndex >= 0)
		{
			renderer.setSeriesPaint(sampleIndex, Color.YELLOW);
			renderer.setSeriesOutlinePaint(sampleIndex, Color.BLACK);
		}
		if (groupIndex >= 0) renderer.setSeriesPaint(groupIndex, new Color(250, 50, 50));
		renderer.setSeriesPaint(allIndex, new Color(50, 50, 250));
		renderer.setShadowVisible(true);
		renderer.setDrawBarOutline(true);
		return chart;
	}
	
	private void updateProbesets(String sm)
	{
		probeSetBox.removeActionListener(probeListener);
		probeModel.removeAllElements();
		for (GeneExp ge : expSet.get(sm))
		{
			probeModel.addElement(ge);
		}
		probeSetBox.addActionListener(probeListener);
		probeSetBox.setSelectedItem(sm2ge.get(sm));
	}
	
	private void updateChart()
	{
		chartPanel.setChart(createChart());
	}

	private void updateGroupTitle()
	{
		int total = expSet.getExpname().length;
		String group = selectedGroup();
		int sub = group == null ? total : expSet.getSubgroupSize(group);
		
		groupBorder.setTitle("Group -- (" + sub + "/" + total + ")");
	}

	private void updateSamples()
	{
		String sample = selectedSample();
		sampleBox.removeActionListener(sampleListener);
		String group = selectedGroup();
		List<String> exps = new ArrayList<String>();
		if (group == null)
		{
			exps.addAll(Arrays.asList(expSet.getExpname()));
		}
		else
		{
			exps.addAll(expSet.getSubgroup(group));
		}
		exps.add(0, NONE);
		sampleModel.removeAllElements();
		for (String exp : exps)
		{
			sampleModel.addElement(exp);
		}
		if (group != null && group2sample.containsKey(group))
		{
			sampleModel.setSelectedItem(group2sample.get(group));
		}
		else if (sample != null && (group == null || expSet.getSubgroup(group).contains(sample)))
		{
			sampleModel.setSelectedItem(sample);
		}

		sampleBox.addActionListener(sampleListener);
	}
	
	public void open()
	{
		init();
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}

	class ProbesetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
//			sm2ge.put(selectedSymbol(), selectedProbeset());
			updateChart();
		}
	}
	class SampleListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String group = selectedGroup();
			String sample = selectedSample();
			if (group != null)
			{
				if (sample == null) group2sample.remove(group);
				else group2sample.put(group, sample);
			}

			updateChart();
		}
	}
}
