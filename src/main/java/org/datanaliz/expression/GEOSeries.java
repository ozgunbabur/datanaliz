package org.datanaliz.expression;

import org.datanaliz.Conf;
import org.datanaliz.util.DelimFileParser;
import org.datanaliz.util.Download;

import java.io.*;
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
	
	boolean multiFile;
	
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
		
		multiFile = findIfMultiFile();
		
		init();
	}

	@Override
	protected String getFileName()
	{
		return Conf.DATA_FOLDER + id + ".txt";
	}

	@Override
	protected String[] getURL()
	{
		return new String[]{SERIES_URL_PREFIX + (id.contains("-") ? 
				id.substring(0, id.indexOf("-")) : id) + "/"+ id + SERIES_URL_SUFFIX};
//			Conf.REMOTE_RESOURCE + id + ".txt.gz"};
	}

	@Override
	protected boolean isResourceZipped()
	{
		return true;
	}

	protected boolean findIfMultiFile()
	{
		for (String url : getURL())
		{
			if (url.startsWith(SERIES_URL_PREFIX) && !Download.urlExists(url))
			{
				url = url.substring(0, url.lastIndexOf(".t")) + "-1.txt.gz";
				if (Download.urlExists(url)) return true;
			}
		}
		return false;
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

		System.out.println("Size of dataset = " + expSet.expname.length);
		loadSubgroups();
		loadCalls();
	}



	protected boolean ignoreLine(String line)
	{
		return line.startsWith("^") || line.startsWith("!") ||
			line.startsWith("#") || line.trim().length() == 0;
	}

	protected void loadSubgroups()
	{
		File file = new File(Conf.DATA_FOLDER + id + Conf.GROUP_FILE_EXTENSION);
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
			Map<String, String[]> map = p.readInStringArrays();
			if (map.size() > 0) expSet.setSubgroups(map);
		}
	}
	
	protected void loadCalls()
	{
		File file = new File(Conf.DATA_FOLDER + id + Conf.CALL_FILE_EXTENSION);

		if (file.exists())
		{
			expSet.loadCalls(file.getAbsolutePath());
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

		br.close();
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
	
	protected void downloadMultiFile() throws IOException
	{
		String url = getURL()[0];
		url = url.substring(0, url.lastIndexOf(".t"));
		
		int i = 0;
		while(downloadZipped(new String[]{url + "-" + (++i) + ".txt.gz"}, 
			Conf.DATA_FOLDER + id + "-" + i + ".txt"));
	}

	@Override
	protected void download() throws IOException
	{
		if (multiFile)
		{
			downloadMultiFile();
			mergeMultiFile();
		}
		else
		{
			super.download();
		}
	}
	
	protected int getFileCount()
	{
		int i = 0;
		while (new File(Conf.DATA_FOLDER + id + "-" + (++i) + ".txt").exists());
		return i-1;
	}
	
	protected void mergeMultiFile() throws IOException
	{
		System.out.print("Merging data files for " + id + " ... ");

		int count = getFileCount();
		if (count == 0) return;

		BufferedReader[] reader = new BufferedReader[count];
		for (int i = 0; i < count; i++)
		{
			reader[i] = new BufferedReader(new FileReader(
				Conf.DATA_FOLDER + id + "-" + (i+1) + ".txt"));
		}

		String[] line = new String[count];

		BufferedWriter writer = new BufferedWriter(new FileWriter(getFileName()));

		for (int i = 0; i < count; i++)
		{
			do
			{
				line[i] = reader[i].readLine();
				if (i == 0 && line[i].startsWith(PLATFORM_LINE_INDICATOR))
				{
					writer.write(line[i]);
				}
			}
			while(ignoreLine(line[i]));
		}

		do
		{
			if (!ignoreLine(line[0]))
			{
				String id = line[0].substring(0, line[0].indexOf("\t"));

				writer.write("\n" + id);

				for (int i = 0; i < count; i++)
				{
					writer.write(line[i].substring(line[i].indexOf("\t")));

					assert id.equals(line[i].substring(0, line[i].indexOf("\t")));
				}
			}

			for (int i = 0; i < count; i++)
			{
				line[i] = reader[i].readLine();
			}
		}
		while(line[0] != null);
		

		writer.close();
		for (BufferedReader r : reader) r.close();

		for (int i = 0; i < count; i++)
		{
			new File(Conf.DATA_FOLDER + id + "-" + (i+1) + ".txt").delete();
		}

		System.out.println("ok");
	}
}
