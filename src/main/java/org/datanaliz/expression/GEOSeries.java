package org.datanaliz.expression;

import org.datanaliz.Conf;
import org.datanaliz.util.DelimFileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Ozgun Babur
 */
public class GEOSeries extends RemoteDataAccessor
{
	protected final static String PLATFORM_LINE_INDICATOR = "!Series_platform_id\t";

	protected final static String SERIES_URL_PREFIX =
		"ftp://ftp.ncbi.nih.gov/pub/geo/DATA/SeriesMatrix/";

	protected final static String SERIES_URL_SUFFIX = "_series_matrix.txt.gz";

	String id;
	ExpSet expSet;

	Collection<String> geneFilter;
	
	/**
	 * @param id GEO series ID (GSEXXXX)
	 */
	public GEOSeries(String id)
	{
		this(id, null);
	}

	public GEOSeries(String id, Collection<String> geneFilter)
	{
		this.id = id;
		this.geneFilter = geneFilter;
		init();
	}

	@Override
	protected String getFileName()
	{
		return Conf.DATA_FOLDER + File.separator + id + ".txt";
	}

	@Override
	protected String[] getURL()
	{
		return new String[]{SERIES_URL_PREFIX + (id.contains("-") ? 
				id.substring(0, id.indexOf("-")) : id) + "/"+ id + SERIES_URL_SUFFIX,
			Conf.REMOTE_RESOURCE + id + ".txt.gz"};
	}

	@Override
	protected boolean isResourceZipped()
	{
		return true;
	}

	@Override
	protected void load() throws IOException
	{
		String platformID = extractPlatformID();
		assert platformID != null;
		GEOPlatform plat = new GEOPlatform(platformID);

		BufferedReader reader = new BufferedReader(new FileReader(getFileName()));

		String line = reader.readLine();

		while (ignoreLine(line)) line = reader.readLine();
		String header = line;

		expSet = new ExpSet();
		expSet.setExpname(header.substring(header.indexOf("\t")+1).replaceAll("\"", "").split("\t"));
		expSet.setName(id);

		for(line = reader.readLine(); line != null; line = reader.readLine())
		{
			if (ignoreLine(line)) continue;

			int tabIndex = line.indexOf("\t");
			String id = line.substring(0, tabIndex).replaceAll("\"", "");
			line = line.substring(tabIndex + 1);
			
			GeneExp gene = new GeneExp(id);
			gene.addEGIDs(plat.getEGIDs(gene.id));
			gene.addSymbols(plat.getSymbols(gene.id));
			gene.setGb(plat.getGB(gene.id));

			if (wannaStore(gene)) 
			{
				gene.setValues(line);
				expSet.addGeneExp(gene);
			}
		}
		if (expSet.isNatural()) expSet.log();

		loadSubgroups();
	}

	protected boolean ignoreLine(String line)
	{
		return line.startsWith("^") || line.startsWith("!") ||
			line.startsWith("#") || line.trim().length() == 0;
	}

	protected void loadSubgroups()
	{
		File file = new File(Conf.DATA_FOLDER + File.separator +  id + Conf.GROUP_FILE_EXTENSION);
		if (!file.exists())
		{
			try
			{
				download(new String[]{Conf.REMOTE_RESOURCE + id + Conf.GROUP_FILE_EXTENSION},
					file.getPath());
				
				if (file.length() == 0) file.delete();
			}
			catch (IOException e)
			{
			}
		}
		if (file.exists())
		{
			DelimFileParser p = new DelimFileParser(file.getPath());
			Map<String,String> map = p.getOneToOneMap("Sample", "Group");
			if (map.size() > 0) expSet.setSubgroups(map);
		}
	}
	
	protected String extractPlatformID() throws IOException
	{
		File gseFile = new File(getFileName());

		BufferedReader br = new BufferedReader(new FileReader(gseFile));

		String currentLine;
		String platformName = null;

		// Find accession number of platform

		while((currentLine = br.readLine()) != null)
		{
			if(currentLine.contains(PLATFORM_LINE_INDICATOR))
			{
				platformName = currentLine.substring(
					currentLine.indexOf("\t") + 1).replaceAll("\"", "");
				break;
			}
		}

		return platformName;
	}

	public ExpSet getExpSet()
	{
		return expSet;
	}
	
	private boolean wannaStore(GeneExp ge)
	{
		return geneFilter == null || ge.isAmong(geneFilter);
	}
}
