package org.datanaliz.conv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ozgun Babur
 */
public class EntrezGene
{
	private static Map<String, String> id2sym;
	private static Map<String, String> sym2id;

	public static void main(String[] args)
	{
		System.out.println(getEGID("BAX"));
		System.out.println(getSymbol("367"));
	}

	/**
	 * Provides HGNC ID of the given approved gene symbol.
	 * @param symbol
	 * @return
	 */
	public static String getEGID(String symbol)
	{
		return sym2id.get(symbol);
	}

	/**
	 * Provides HGNC ID of the given approved gene symbol.
	 * @param egid
	 * @return
	 */
	public static String getSymbol(String egid)
	{
		return id2sym.get(egid);
	}

	static
	{
		try
		{
			sym2id = new HashMap<String, String>();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
				EntrezGene.class.getResourceAsStream("EntrezGene.txt")));

			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				String[] token = line.split("\t");
				if (token.length < 2) continue;
				String sym = token[0];
				String id = token[1];
				sym2id.put(sym, id);
			}
			reader.close();

			id2sym = new HashMap<String, String>();
			for (String key : sym2id.keySet())
			{
				id2sym.put(sym2id.get(key), key);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
