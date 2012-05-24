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
public class HGNC
{
	private static Map<String, Integer> sym2id;
	private static Map<Integer, String> id2sym;

	public static void main(String[] args)
	{
		System.out.println(getHGNCID("BAX"));
	}

	/**
	 * Provides HGNC ID of the given approved gene symbol.
	 * @param symbol
	 * @return
	 */
	public static Integer getHGNCID(String symbol)
	{
		return sym2id.get(symbol);
	}

	public static String getSymbol(Integer hgncID)
	{
		return id2sym.get(hgncID);
	}

	static
	{
		try
		{
			sym2id = new HashMap<String, Integer>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				HGNC.class.getResourceAsStream("hgnc.txt")));
			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				String[] token = line.split("\t");
				String sym = token[1];
				Integer id = Integer.parseInt(token[0]);
				sym2id.put(sym, id);
			}
			reader.close();

			id2sym = new HashMap<Integer, String>();
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
