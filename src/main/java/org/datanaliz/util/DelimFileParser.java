package org.datanaliz.util;

import java.io.*;
import java.util.*;

/**
 * @author Ozgun Babur
 */
public class DelimFileParser
{
	private BufferedReader reader;
	private int columnCnt;
	private String delim;

	public DelimFileParser(String filename)
	{
		try
		{
			this.reader = new BufferedReader(new FileReader(filename));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		delim = "\t";
	}

	public DelimFileParser(InputStream is)
	{
		this.reader = new BufferedReader(new InputStreamReader(is));
		delim = "\t";
	}

	public void setDelim(String delim)
	{
		this.delim = delim;
	}

	public Map<String, String> getOneToOneMap(String keyColumn, String valueColumn)
	{
		try
		{
			Map<String, String> map = new HashMap<String, String>();

			String line = reader.readLine();

			while (line.startsWith("^") || line.startsWith("!") || line.startsWith("#") ||
				line.length() == 0)
			{
				line = reader.readLine();
			}

			columnCnt = count(line, delim) + 1;
			String[] header = parseRow(line);
			int keyIndex = indexOf(keyColumn, header);
			int valueIndex = indexOf(valueColumn, header);

			int lastIndex = Math.max(keyIndex, valueIndex);

			while ((line = reader.readLine()) != null)
			{
				String[] row = parseRow(line);

				if (row.length > lastIndex && row[keyIndex] != null && row[keyIndex].length() > 0)
				{
					map.put(row[keyIndex], row[valueIndex]);
				}
			}
			reader.close();
			return map;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Set<String>> getOneToManyMap(String keyColumn, String valueColumn)
	{
		return getOneToManyMap(keyColumn, valueColumn, null);
	}

	public Map<String, Set<String>> getOneToManyMap(String keyColumn, String valueColumn, String delim)
	{
		try
		{
			Map<String, Set<String>> map = new HashMap<String, Set<String>>();

			String line = reader.readLine();

//			while (line.startsWith("^") || line.startsWith("!") || line.startsWith("#") || line.length() == 0)
//			{
//				line = reader.readLine();
//			}

			columnCnt = count(line, delim) + 1;
			String[] header = parseRow(line);
			int keyIndex = indexOf(keyColumn, header);
			int valueIndex = indexOf(valueColumn, header);

			int lastIndex = Math.max(keyIndex, valueIndex);

			while ((line = reader.readLine()) != null)
			{
				String[] row = parseRow(line);

				if (row.length > lastIndex)
				{
					if (row[keyIndex] == null || row[keyIndex].equals("")) continue;
					if (row[valueIndex] == null || row[valueIndex].equals("")) continue;

					if (delim == null)
					{
						if (!map.containsKey(row[keyIndex]))
						{
							map.put(row[keyIndex], new HashSet<String>());
						}

						map.get(row[keyIndex]).add(row[valueIndex]);
					}
					else
					{
						for (String s1 : row[keyIndex].split(delim))
						{
							for (String s2 : row[valueIndex].split(delim))
							{
								if (!map.containsKey(s1))
								{
									map.put(s1, new HashSet<String>());
								}

								map.get(s1).add(s2);
							}
						}
					}
				}
			}
			reader.close();
			return map;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Set<String> getColumnSet(String column)
	{
		Set<String> set = new HashSet<String>();
		readIntoCollection(column, set);
		return set;
	}

	public static Set<String> getColumnSet(String filename, int columnNo)
	{
		DelimFileParser p = new DelimFileParser(filename);
		return p.getColumnSet(columnNo);
	}

	public Set<String> getColumnSet(int columnNo)
	{
		columnCnt = columnNo + 1;
		Set<String> set = new HashSet<String>();
		readIntoCollection(columnNo, set);
		return set;
	}

	public List<String> getColumnList(String column)
	{
		List<String> list = new ArrayList<String>();
		readIntoCollection(column, list);
		return list;
	}

	public void readIntoCollection(String column, Collection<String> coll)
	{
		try
		{
			String line = reader.readLine();

//			while (line.startsWith("#") || line.startsWith("^") ||
//				line.startsWith("!") || line.length() == 0)
//			{
//				line = reader.readLine();
//			}

			columnCnt = count(line, delim) + 1;
			String[] header = parseRow(line);
			int index = indexOf(column, header);

			readIntoCollection(index, coll);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param columnNo starts from 0
	 */
	public void readIntoCollection(int columnNo, Collection<String> coll)
	{
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#") || line.length() == 0) continue;

				String[] row = parseRow(line);

				if (row.length > columnNo && row[columnNo] != null)
				{
					coll.add(row[columnNo].trim());
				}
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String[] parseRow(String line)
	{
		String[] row = new String[columnCnt];

		int i = 0;

		StringTokenizer tokenizer = new StringTokenizer(line, delim, true);

		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();

			if (token.equals(delim))
			{
				i++;
			}
			else
			{
				row[i] = token.trim();
				if (row[i].startsWith("\"") && row[i].endsWith("\""))
				{
					row[i] = row[i].substring(1, row[i].length() - 1).trim();
				}
			}

			if (i == columnCnt) break;
		}
		return row;
	}

	private int indexOf(String token, String[] row)
	{
		int i = 0;
		for (String s : row)
		{
			if (s != null && s.equals(token))
			{
				return i;
			}
			else
			{
				i++;
			}
		}
		return -1;
	}

	private int count(String line, String q)
	{
		int i = 0;
		int a = 0;

		while (line.indexOf(q, a) >= 0)
		{
			i++;
			a = line.indexOf(q, a) + 1;
		}

		return i;
	}
}
