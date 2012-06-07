package org.datanaliz.util;

import org.datanaliz.Conf;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ozgun Babur
 */
public class FileUtil
{
	public static BufferedReader getReader(String filename) throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(filename));
	}

	public static BufferedWriter getWriter(String filename) throws IOException
	{
		return new BufferedWriter(new FileWriter(filename));
	}

	public static String getFirstLine(String filename) throws IOException
	{
		BufferedReader reader = getReader(filename);
		String line = reader.readLine();
		reader.close();
		return line;
	}

	public static String getColumnsLine(String filename) throws IOException
	{
		BufferedReader reader = getReader(filename);

		String line = reader.readLine();
		for (; line != null; line = reader.readLine())
		{
			if (line.startsWith("^") || line.startsWith("#") ||
				line.startsWith("!") || line.length() == 0)
			{
				//continue
			}
			else break;
		}

		reader.close();
		return line;
	}

	public static int getLineNumber(String filename) throws IOException
	{
		BufferedReader reader = getReader(filename);

		int cnt = 0;
		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			cnt++;
		}

		reader.close();
		return cnt;
	}

	public static String[] getColumnsArray(String filename) throws IOException
	{
		return getColumnsLine(filename).replace("\"", "").split("\t");
	}

	public static boolean columnsLineContains(String filename, String column) throws IOException
	{
		return getColumnsLine(filename).contains(column);
	}

	public static void printLines(String file, int start, int stop) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));

		int i = 0;
		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			i++;
			if (i < start) continue;
			if (i > stop) break;

			System.out.println(line);
//			System.out.println(line.split("\t").length);
		}

		reader.close();
	}

	public static void printLines(String file, String substr) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));

		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			if (line.contains(substr))
			{
				System.out.println(line);
			}
		}

		reader.close();
	}

	public static void transpose(String inFile, String outFile) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(inFile));

		List<List<String>> content = new ArrayList<List<String>>();

		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{

			String[] token = line.split("\t", Integer.MAX_VALUE);
			System.out.println("token.length = " + token.length);

			int i = 0;
			for (String s : token)
			{
				if (i > content.size() - 1)
				{
					content.add(new ArrayList<String>());
				}
				List<String> row = content.get(i);
				row.add(s);
				i++;
			}
		}

		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

		for (List<String> row : content)
		{
			for (String token : row)
			{
				writer.write(token + "\t");
			}
			writer.write("\n");
		}

		writer.close();
	}

	public static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}


	public static void main(String[] args) throws IOException
	{
//		transpose("resource/expdata/expo/stages.txt",
//			"resource/expdata/expo/stages.txt");
//
//		String file = Conf.DATA_FOLDER + "CCLEPlusSKMELNormalized.txt";
		String file = "/home/ozgun/Desktop/GSE2109_series_matrix-3.txt";
//		System.out.println("getLineNumber(file) = " + getLineNumber(file));
		printLines(file, 1,200);
	}
}