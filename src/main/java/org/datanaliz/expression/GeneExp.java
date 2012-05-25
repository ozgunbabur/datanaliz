package org.datanaliz.expression;

import org.datanaliz.conv.EntrezGene;
import org.datanaliz.stat.Summary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Ozgun Babur
 */
public class GeneExp
{
	String id;
	String gb;
	List<String> egIds;
	List<String> symbols;

	double[] val;
	
	public static final double LOG2 = Math.log(2);

	public GeneExp(String id, double[] val)
	{
		this.id = id;
		this.val = val;
	}

	/**
	 * The row should be tab delimited. Should start with the ID, following values.
	 * @param id ID
	 */
	public GeneExp(String id)
	{
		this.id = id;

		symbols = new ArrayList<String>();
		egIds = new ArrayList<String>();
	}
	
	public void setValues(String vals)
	{
		String[] valStr = vals.split("\t");
		val = new double[valStr.length];
		for (int i = 0; i < val.length; i++)
		{
			val[i] = Double.parseDouble(valStr[i]);
		}
	}
	
	public void addSymbol(String sym)
	{
		if (!symbols.contains(sym))
		{
			symbols.add(sym);
			String eg = EntrezGene.getEGID(sym);
			if (eg != null) egIds.add(eg);
		}
	}

	public void addSymbols(Collection<String> syms)
	{
		for (String sym : syms)
		{
			addSymbol(sym);
		}
	}

	public void addEGID(String eg)
	{
		if (!egIds.contains(eg))
		{
			egIds.add(eg);
			String sym = EntrezGene.getSymbol(eg);
			if (sym != null) symbols.add(sym);
		}
	}

	public void addEGIDs(Collection<String> egs)
	{
		for (String eg : egs)
		{
			addEGID(eg);
		}
	}

	private String getPrintable(List<String> list)
	{
		if (list.isEmpty()) return "";
		String s = list.get(0);
		for (int i = 1; i < list.size(); i++)
		{
			s += ", " + list.get(i);
		}
		return s;
	}
	
	public String getGb()
	{
		return gb;
	}

	public void setGb(String gb)
	{
		this.gb = gb;
	}

	public boolean isAmong(Collection<String> coll)
	{
		for (String sm : symbols)
		{
			if (coll.contains(sm)) return true;
		}
		for (String eg : egIds)
		{
			if (coll.contains(eg)) return true;
		}

		return (gb != null && coll.contains(gb));
	}

	public void log()
	{
		for (int i = 0; i < val.length; i++)
		{
			val[i] = Math.log(val[i]) / LOG2;
		}
	}
	
	public void unlog()
	{
		for (int i = 0; i < val.length; i++)
		{
			val[i] = Math.pow(2, val[i]);
		}
	}	
	
	public double getMax()
	{
		return Summary.max(val);
	}
	
	public double[] getSubset(int[] ind)
	{
		double[] v = new double[ind.length];

		for (int i = 0; i < ind.length; i++)
		{
			v[i] = val[ind[i]];
		}
		return v;
	}
	
	@Override
	public String toString()
	{
		return id + " | " + gb + " | " + 
			(symbols == null ? "" : getPrintable(symbols)) + " | " +
			(egIds == null ? "" : getPrintable(egIds));
	}
}
