package org.datanaliz.chart;

import org.datanaliz.expression.ExpSet;
import org.datanaliz.expression.GeneExp;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
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
	DefaultComboBoxModel probeModel;
	DefaultComboBoxModel groupModel;
	ProbesetListener probeListener;
	Map<String, GeneExp> sm2ge;

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
		
		JPanel controlPanel = new JPanel(new FlowLayout());
		JLabel probeLabel = new JLabel("Probeset");
		controlPanel.add(probeLabel);
		probeSetBox = new JComboBox();
		probeModel = new DefaultComboBoxModel();
		probeListener = new ProbesetListener();
		probeSetBox.addActionListener(probeListener);
		probeSetBox.setModel(probeModel);
		controlPanel.add(probeSetBox);
		if (expSet.hasSubgroups())
		{
			JLabel groupLab = new JLabel("   Group:");
			controlPanel.add(groupLab);
			List<String> groups = expSet.getSubgroups();
			groups.add(0, NONE);
			JComboBox groupCombo = new JComboBox();
			groupModel = new DefaultComboBoxModel(groups.toArray());
			groupCombo.setModel(groupModel);
			groupCombo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					updateChart();
				}
			});
			controlPanel.add(groupCombo);
		}
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
	
	protected JFreeChart createChart()
	{
		GeneExp ge = selectedProbeset();
		HistogramDataset hd = new HistogramDataset();
		hd.setType(HistogramType.SCALE_AREA_TO_1);
		hd.addSeries("All samples", ge.getValues(), 20);
		String group = selectedGroup();
		if (group != null)
		{
			hd.addSeries(group, ge.getSubset(expSet.getGroupIndex(group)), 20);
		}
		return ChartFactory.createHistogram("Distribution of Expression", "expression", "density",
			hd, PlotOrientation.VERTICAL, true, true, true);
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
}
