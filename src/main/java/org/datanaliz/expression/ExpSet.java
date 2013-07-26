package org.datanaliz.expression;

import org.datanaliz.stat.Pearson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
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

	Map<String, String[]> exp2group;
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

	public void setSubgroups(Map<String, String[]> exp2group)
	{
		this.exp2group = exp2group;
		cleanNonExistingExpInGroups();

		group2exp = new HashMap<String, Set<String>>();
		for (String exp : exp2group.keySet())
		{
			String[] groups = exp2group.get(exp);
			for (String group : groups)
			{
				if (!group2exp.containsKey(group)) group2exp.put(group, new HashSet<String>());
				group2exp.get(group).add(exp);
			}
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

	public boolean hasCalls()
	{
		return id2gene.values().iterator().next().getCall() != null;
	}

	public List<String> getSubgroups()
	{
		if (!hasSubgroups()) return Collections.emptyList();

		// Below lines ensure that groups will be in the order of appearance of samples.

		List<String> groups = new ArrayList<String>(group2exp.keySet().size());
		for (String exp : expname)
		{
			String[] groupArr = exp2group.get(exp);
			
			if (groupArr != null)
			{
				for (String group : groupArr)
				{
					if (group != null && !groups.contains(group))
					{
						groups.add(group);
					}
				}
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
			String[] grs = exp2group.get(exp);
			if (grs != null)
			{
				for (String gr : grs)
				{
					if (gr != null && gr.equals(group))
					{
						elements.add(exp);
					}
				}
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

	public void loadCalls(String filename)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));

			String line = reader.readLine();

			String[] token = line.split("\t");

			assert token.length == expname.length;

			for (int i = 0; i < token.length; i++)
			{
				if (token[i].contains("_"))
				{
					token[i] = token[i].substring(0, token[i].indexOf("_"));
				}
			}

			int[] expmap = getExpnameMapping(token);

			for (line = reader.readLine(); line != null; line = reader.readLine())
			{
				String id = line.substring(0, line.indexOf("\t"));

				GeneExp exp = id2gene.get(id);

				if (exp == null)
				{
					continue;
				}

				token = line.split("\t");

				Call[] call = new Call[token.length - 1];
				for (int i = 1; i < token.length; i++)
				{
					if (token[i].equals("A")) call[expmap[i-1]] = Call.ABSENT;
					else if (token[i].equals("M")) call[expmap[i-1]] = Call.MARGINAL;
					else if (token[i].equals("P")) call[expmap[i-1]] = Call.PRESENT;
					else System.err.println("Unknown call value = " + token[i]);
				}

				exp.setCall(call);
			}

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	private int[] getExpnameMapping(String[] other)
	{
		assert expname.length == other.length;
		assert new HashSet<String>(Arrays.asList(other)).size() == other.length;

		List<String> list = Arrays.asList(expname);
		int[] index = new int[other.length];
		for (int i = 0; i < other.length; i++)
		{
			index[i] = list.indexOf(other[i]);
			assert index[i] >= 0;
		}
		return index;
	}

	/**
	 * Calculates the matrix of Pearson correlations of the probeset values
	 */
	public double[][] calcIsoformCorrelations(String symbol)
	{
		List<GeneExp> exps = sm2gene.get(symbol);

		if (exps == null) return null;
		if (exps.size() == 1) return new double[][]{{1D}};

		int size = exps.size();
		double[][] c = new double[size][size];

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				c[i][j] = (i == j ? 0 : Pearson.calcCorrelation(exps.get(i).val, exps.get(j).val));
			}
		}
		return c;
	}

	public String[] getIsoformIDs(String symbol)
	{
		List<GeneExp> exps = sm2gene.get(symbol);

		if (exps == null) return null;

		String[] s = new String[exps.size()];

		for (int i = 0; i < s.length; i++)
		{
			s[i] = exps.get(i).id;
		}
		return s;
	}

	private static final DecimalFormat format = new DecimalFormat("0.00");

	public String getIsoformCorrelationsInString(String symbol)
	{
		String[] ids = getIsoformIDs(symbol);
		if (ids == null) return null;

		String s = "";

		for (String id : ids)
		{
			s += "\t" + id;
		}

		double[][] c = calcIsoformCorrelations(symbol);

		for (int i = 0; i < c.length; i++)
		{
			s += "\n" + ids[i];

			for (int j = 0; j < c[i].length; j++)
			{
				s += "\t" + format.format(c[i][j]);
			}
		}
		return s;
	}
}
