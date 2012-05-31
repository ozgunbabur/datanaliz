package org.datanaliz.stat;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Ozgun Babur
 */
public class QuantileNormalizer
{
	/**
	 * Decimal format for printing double values.
	 */
	public static final DecimalFormat fmt = new DecimalFormat("0.##");

	/**
	 * Format of datafile: Each row contains geneID followed by experiment values. First row is
	 * header, and remains unchanged.
	 */
	public static void normalize(String datafile, String outfile) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(datafile));
		List<String> ids = new ArrayList<String>();
		List<float[]> vals = new ArrayList<float[]>();

		String header = reader.readLine();
		String prelines = "";
		while(header.length() == 0 || header.startsWith("!"))
		{
			prelines += header + "\n";
			header = reader.readLine();
		}

		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			if (line.startsWith("!")) continue;
			
			String id = line.substring(0, line.indexOf("\t"));
			line = line.substring(line.indexOf("\t") + 1);
			String[] token = line.split("\t");

			float[] val = new float[token.length];
			for (int i = 0; i < val.length; i++)
			{
				val[i] = Float.parseFloat(token[i]);
			}

			ids.add(id);
			vals.add(val);
		}
		reader.close();
		System.out.println("Read the data");

		float[][] m = vals.toArray(new float[vals.size()][]);
		int[][] o = getOrdering(m);
		System.out.println("Got ordering");

		quantileNormalize(m, o);
		System.out.println("Normalized");

		assert m.length == ids.size();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		writer.write(prelines);
		writer.write(header);

		for (int i = 0; i < ids.size(); i++)
		{
			writer.write("\n" + ids.get(i));

			for (int j = 0; j < m[i].length; j++)
			{
				writer.write("\t" + fmt.format(m[i][j]));
//				writer.write("\t" + fmt.format(Math.log(m[i][j])));
			}
		}

		writer.close();
	}

	private static void quantileNormalize(float[][] m, int[][] o)
	{
		int genecnt = o[0].length;
		int colcnt = o.length;
		for (int i = 0; i < genecnt; i++)
		{
			float avg = 0;
			for (int j = 0; j < colcnt; j++)
			{
				avg += m[o[j][i]][j];
			}
			avg /= colcnt;
			for (int j = 0; j < colcnt; j++)
			{
				m[o[j][i]][j] = avg;
			}
		}
	}

	private static int[][] getOrdering(float[][] m)
	{
		int[][] ord = new int[m[0].length][m.length];
		for (int i = 0; i < m[0].length; i++)
		{
			ord[i] = getOrdering(m, i);
		}
		return ord;
	}

	private static int[] getOrdering(float[][] m, int col)
	{
		List<Holder> list = new ArrayList<Holder>();
		for (int i = 0; i < m.length; i++)
		{
			list.add(new Holder(i, m[i][col]));
		}

		Collections.sort(list);
		int i = 0;
		int[] ord = new int[m.length];
		for (Holder h : list)
		{
			ord[i++] = h.index;
		}
		return ord;
	}

	static class Holder implements Comparable
	{
		int index;
		Float value;

		Holder(int index, Float value)
		{
			this.index = index;
			this.value = value;
		}

		public int compareTo(Object o)
		{
			return value.compareTo(((Holder) o).value);
		}
	}

	//---------- Merging files before normalization, if necessary ----------------------------------

	/**
	 * The directory should contain series matrix files of experiments from the same platform.
	 */
	private static void merge(String dir) throws IOException
	{
		Map<String, String> map = new HashMap<String, String>();

		File direc = new File(dir);

		String header = null;

		for (File file : direc.listFiles())
		{
			if (!file.getName().endsWith(".txt")) continue;

			System.out.println("Reading file = " + file);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			boolean headerProcessed = false;

			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				if (line.startsWith("!") || line.length() == 0) continue;

				String id = line.substring(0, line.indexOf("\t")).trim();
				if (id.startsWith("\"")) id = id.substring(1, id.length()-1).trim();
				line = line.substring(line.indexOf("\t") + 1).trim();

				if (!headerProcessed)
				{
					if (header == null) header = id + "\t" + line;
					else header += "\t" + line;
					headerProcessed = true;
				}
				else
				{
					if (!map.containsKey(id)) map.put(id, line);
					else map.put(id, map.get(id) + "\t" + line);
				}
			}

			reader.close();
		}

		System.out.println("Writing output");
		BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "merged.txt"));

		writer.write(header);
		int expcnt = header.split("\t").length - 1;

		for (String id : map.keySet())
		{
			int cnt = map.get(id).split("\t").length;

			assert expcnt == cnt :
				"ID: " + id + " has less experiments (" + cnt + ") than " + expcnt;

			writer.write("\n" + id + "\t" + map.get(id));
		}

		writer.close();
	}


	//------ Taking exponent ----------------------------------------------------------------------|

	public static void revertLoggedData(String infile, String outfile) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(infile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

		boolean headerProcessed = false;

		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			if (line.startsWith("!") || line.length() == 0) continue;

			if (!headerProcessed)
			{
				writer.write(line);
				headerProcessed = true;
			}
			else
			{
				String id = line.substring(0, line.indexOf("\t"));
				writer.write("\n" + id);
				String[] valStr = line.substring(line.indexOf("\t") + 1).split("\t");
				double[] val = new double[valStr.length];

				for (int i = 0; i < val.length; i++)
				{
					val[i] = Double.parseDouble(valStr[i]);
					val[i] = Math.pow(2, val[i]);
					writer.write("\t" + fmt.format(val[i]));
				}
			}
		}

		reader.close();
		writer.close();
	}
}
