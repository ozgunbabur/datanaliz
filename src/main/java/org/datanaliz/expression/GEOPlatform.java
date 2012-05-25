package org.datanaliz.expression;

import org.datanaliz.Conf;

import java.io.*;
import java.util.*;

/**
 * @author Ozgun Babur
 */
public class GEOPlatform extends RemoteDataAccessor
{
	protected final static String PLATFORM_URL_PREFIX =
		"http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?targ=self&form=text&view=data&acc=";

	protected final static String[] GB_NAMES = new String[]{"GB_ACC"};
	protected final static String[] EG_NAMES = new String[]{"ENTREZ_GENE_ID", "GENE"};
	protected final static String[] SYMBOL_NAMES = new String[]{"Gene Symbol", "GENE_SYMBOL"};

	String id;

	Map<String, String> id2egs;
	Map<String, String> id2syms;
	Map<String, String> id2gb;
	
	/**
	 * @param id GEO platform ID (GPLXXX)
	 */
	public GEOPlatform(String id)
	{
		this.id = id;
		init();
	}

	protected void load() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(getFileName()));

		String line = reader.readLine();

		while (ignoreLine(line)) line = reader.readLine();

		String[] col = line.split("\t");

		int egIndex = getColIndex(col, EG_NAMES);
		int symIndex = getColIndex(col, SYMBOL_NAMES);
		int gbIndex = getColIndex(col, GB_NAMES);

		if (egIndex < 0 && symIndex < 0) throw new RuntimeException("Entrez Gene or Gene Symbol " +
			"column not recognized. Header = " + line);

		id2egs = new HashMap<String, String>();
		id2syms = new HashMap<String, String>();
		id2gb = new HashMap<String, String>();

		for(line = reader.readLine(); line != null; line = reader.readLine())
		{
			if (ignoreLine(line)) continue;

			String[] s = line.split("\t");
			if (s.length <= egIndex && s.length <= symIndex) continue;

			String id = s[0];
			String eg = egIndex > 0 ? s[egIndex].trim() : null;
			String sym = symIndex > 0 ? s[symIndex].trim() : null;
			String gb = gbIndex > 0 ? s[gbIndex].trim() : null;

			if (eg != null && eg.length() > 0) id2egs.put(id, eg);
			if (sym != null && sym.length() > 0) id2syms.put(id, sym);
			if (gb != null && gb.length() > 0) id2gb.put(id, gb);
		}

		reader.close();
	}

	protected boolean ignoreLine(String line)
	{
		return line.startsWith("^") || line.startsWith("!") ||
			line.startsWith("#") || line.trim().length() == 0;
	}

	protected int getColIndex(String[] cols, String[] possibleNames)
	{
		for (String name : possibleNames)
		{
			int ind = indexOf(cols, name);
			if (ind > 0) return ind;
		}
		return -1;
	}

	protected int indexOf(String[] array, String s)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i].equals(s)) return i;
		}
		return -1;
	}

	@Override
	protected String getURL()
	{
		return PLATFORM_URL_PREFIX + id;
	}

	@Override
	protected boolean isResourceZipped()
	{
		return false;
	}

	protected String getFileName()
	{
		return Conf.DATA_FOLDER + File.separator + id + ".txt";
	}

	public List<String> getEGIDs(String id)
	{
		return getValues(id, id2egs);
	}
	
	public List<String> getSymbols(String id)
	{
		return getValues(id, id2syms);
	}
	
	public String getGB(String id)
	{
		return id2gb.get(id);
	}

	private List<String> getValues(String id, Map<String, String> valueMap)
	{
		String s = valueMap.get(id);
		if (s != null)
		{
			String[] vals = s.split("[/ ]+");
			return Arrays.asList(vals);
		}
		return Collections.emptyList();
	}
}
