package org.datanaliz.expression;

import java.io.*;
import java.util.Collection;

/**
 * This class contains methods that can be helpful to work on bioconductor affy package MAS5 output
 *
 * @author Ozgun Babur
 */
public abstract class MAS5Output extends GEOSeries
{
	private static final String TMP_EXTENSION = ".mas5outputtmpfile";

	public MAS5Output(String id)
	{
		super(id);
	}

	public MAS5Output(String id, Collection<String> geneFilter)
	{
		super(id, geneFilter);
	}

	public abstract String getPlatform();

	@Override
	protected String getURL()
	{
		throw new UnsupportedOperationException("Cannot download this mas5 output from a URL");
	}

	@Override
	protected String extractPlatformID() throws IOException
	{
		String plat = super.extractPlatformID();
		if (plat == null && getPlatform() != null)
		{
			addPlatformInfo();
			return super.extractPlatformID();
		}
		return plat;
	}

	protected void addPlatformInfo()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(getFileName()));
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(getFileName() + TMP_EXTENSION));

			writer.write(GEOSeries.PLATFORM_LINE_INDICATOR + getPlatform() + "\n");

			String line = reader.readLine();
			writer.write("ID\t" + line);

			for (line = reader.readLine(); line != null; line = reader.readLine())
			{
				writer.write("\n" + line);
			}

			reader.close();
			writer.close();

			File oldFile = new File(getFileName());
			File newFile = new File(getFileName() + TMP_EXTENSION);
			if (newFile.exists() && newFile.length() > oldFile.length())
			{
				oldFile.delete();
				newFile.renameTo(new File(getFileName()));
			}
		} catch (IOException e) { throw new RuntimeException(e); }
	}
}
