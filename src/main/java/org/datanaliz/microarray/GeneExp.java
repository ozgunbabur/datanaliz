package org.datanaliz.microarray;

import org.datanaliz.conv.EntrezGene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Ozgun Babur
 */
public class GeneExp
{
	String id;
	List<String> egIds;
	List<String> symbols;

	double[] val;

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
		return false;
	}
}
