package org.datanaliz.expression;

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

	Map<String, String> exp2group;
	Map<String, Set<String>> group2exp;
	
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
	
	public int indexOf(String expName)
	{
		for (int i = 0; i < expname.length; i++)
		{
			if (expname[i].equals(expName)) return i;
		}
		return -1;
	}

	public void setSubgroups(Map<String, String> exp2group)
	{
		this.exp2group = exp2group;
		cleanNonExistingExpInGroups();

		group2exp = new HashMap<String, Set<String>>();
		for (String exp : exp2group.keySet())
		{
			String group = exp2group.get(exp);
			if (!group2exp.containsKey(group)) group2exp.put(group, new HashSet<String>());
			group2exp.get(group).add(exp);
		}
	}

	protected void cleanNonExistingExpInGroups()
	{
		for (String exp : new HashSet<String>(exp2group.keySet()))
		{
			if (indexOf(exp) < 0) exp2group.remove(exp);
		}
	}
	
	public boolean hasSubgroups()
	{
		return exp2group != null;
	}
	
	public List<String> getSubgroups()
	{
		if (!hasSubgroups()) return Collections.emptyList();

		// Below lines ensure that groups will be in the order of appearance of samples.

		List<String> groups = new ArrayList<String>(group2exp.keySet().size());
		for (String exp : expname)
		{
			String group = exp2group.get(exp);
			if (group != null && !groups.contains(group))
			{
				groups.add(group);
			}
		}
		return groups;
	}
	
	public List<String> getSubgroup(String group)
	{
		if (!hasSubgroups() || !group2exp.containsKey(group)) return Collections.emptyList();

		// Below lines ensure that groups will be in the order of appearance of samples.

		List<String> elements = new ArrayList<String>(group2exp.get(group).size());
		for (String exp : expname)
		{
			String gr = exp2group.get(exp);
			if (gr != null && gr.equals(group))
			{
				elements.add(exp);
			}
		}
		return elements;
	}
	
	public int getSubgroupSize(String group)
	{
		if (group2exp.containsKey(group)) return group2exp.get(group).size();
		return 0;
	}


	public int[] getGroupIndex(String group)
	{
		assert group2exp.containsKey(group);

		int[] ind = new int[group2exp.get(group).size()];

		int i = 0;
		for (String exp : group2exp.get(group))
		{
			ind[i++] = indexOf(exp);
			assert ind[i-1] >= 0;
		}
		Arrays.sort(ind);
		return ind;
	}

	public Set<String> getSymbols()
	{
		return sm2gene.keySet();
	}
}
