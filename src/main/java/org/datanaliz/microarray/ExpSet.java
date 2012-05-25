package org.datanaliz.microarray;

import java.util.*;

/**
 * @author Ozgun Babur
 */
public class ExpSet
{
	Map<String, GeneExp> id2gene;
	Map<String, List<GeneExp>> eg2gene;
	Map<String, List<GeneExp>> sm2gene;

	String[] expname;
	String name;

	public ExpSet()
	{
		id2gene = new HashMap<String, GeneExp>();
		eg2gene = new HashMap<String, List<GeneExp>>();
		sm2gene = new HashMap<String, List<GeneExp>>();
	}
	
	public void addGeneExp(GeneExp gene)
	{
		id2gene.put(gene.id, gene);
		for (String egId : gene.egIds)
		{
			if (!eg2gene.containsKey(egId)) eg2gene.put(egId, new ArrayList<GeneExp>(1));
			eg2gene.get(egId).add(gene);
		}
		for (String sym : gene.symbols)
		{
			if (!sm2gene.containsKey(sym)) sm2gene.put(sym, new ArrayList<GeneExp>(1));
			sm2gene.get(sym).add(gene);
		}
	}

	public String[] getExpname()
	{
		return expname;
	}

	public void setExpname(String[] expname)
	{
		this.expname = expname;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<GeneExp> get(String s)
	{
		return sm2gene.containsKey(s) ? sm2gene.get(s) :
			eg2gene.containsKey(s) ? sm2gene.get(s) :
				Collections.<GeneExp>emptyList();
	}

	@Override
	public String toString()
	{
		return name;
	}
	
	public boolean isNatural()
	{
		Iterator<GeneExp> iter = id2gene.values().iterator();
		int i = 0;
		while (iter.hasNext())
		{
			if (i++ > 5) break;
			GeneExp geneExp = iter.next();
			if (geneExp.getMax() > 30) return true;
		}
		return false;
	}

	public void log()
	{
		for (GeneExp geneExp : id2gene.values())
		{
			geneExp.log();
		}
	}

	public void unlog()
	{
		for (GeneExp geneExp : id2gene.values())
		{
			geneExp.unlog();
		}
	}
}
