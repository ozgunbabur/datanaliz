package org.datanaliz.expression;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ozgun Babur
 */
public class ExpressionFileMerger
{
	public boolean merge(String mainFile, String addition, String output)
	{
		try
		{
			BufferedReader reader1 = new BufferedReader(new FileReader(mainFile));
			BufferedReader reader2 = new BufferedReader(new FileReader(addition));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			String line;
			for (line = reader1.readLine(); line != null; line = reader1.readLine())
			{
				if (ignoreLine(line)) writer.write(line + "\n");
				else break;
			}
			
			String header1 = line;

			do {line = reader2.readLine();} while(ignoreLine(line));
			
			String header2 = line;

			writer.write(header1 + header2.substring(header2.indexOf("\t")));

			Map<String, String> map = new HashMap<String, String>();

			for (line = reader1.readLine(); line != null; line = reader1.readLine())
			{
				if (!ignoreLine(line)) 
				{
					String id = line.substring(0, line.indexOf("\t"));
					map.put(id, line);
				}
			}

			System.out.println("File " + mainFile + " contains " + map.size() + " rows");

			int i = 0;
			for (line = reader2.readLine(); line != null; line = reader2.readLine())
			{
				if (ignoreLine(line)) continue;

				String id = line.substring(0, line.indexOf("\t"));
				if (map.containsKey(id))
				{
					writer.write("\n" + map.get(id) + line.substring(line.indexOf("\t")));
					i++;
				}
			}

			System.out.println("Intersecting rows = " + i);

			reader1.close();
			reader2.close();
			writer.close();
			
			return true;
		} 
		catch (IOException e)
		{
			return false;
		}
	}
	
	private boolean ignoreLine(String line)
	{
		return line.startsWith("#") || line.startsWith("^") || line.startsWith("!") ||
			line.length() == 0;
	}
}
