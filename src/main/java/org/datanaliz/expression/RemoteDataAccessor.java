package org.datanaliz.expression;

import org.datanaliz.Conf;
import org.datanaliz.util.Download;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * @author Ozgun Babur
 */
public abstract class RemoteDataAccessor
{
	protected void init()
	{
		try
		{
			if (!inCache()) download();

			load();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected abstract String getFileName();

	protected void ensureDataFolderPresent()
	{
		File file = new File(Conf.DATA_FOLDER);
		if (!file.exists())
		{
			if (!file.mkdirs())
			{
				throw new RuntimeException("Cannot create data folder for some reason. Path = " +
					file.getPath());
			}
		}
	}

	protected boolean inCache()
	{
		ensureDataFolderPresent();
		return new File(getFileName()).exists();
	}

	protected void download() throws IOException
	{
		if (isResourceZipped())
		{
			downloadZipped(getURL(), getFileName());
		}
		else
		{
			download(getURL(), getFileName());
		}
	}

	protected abstract void load() throws IOException;
	protected abstract String[] getURL();

	protected abstract boolean isResourceZipped();

	protected boolean download(String[] urlStrings, String filename) throws IOException
	{
		for (String urlString : urlStrings)
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

			try
			{
				System.out.print("Downloading data from " + urlString + " ... ");
				URL url = new URL(urlString);
				URLConnection con = url.openConnection();

				BufferedReader reader = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
	
				String currentRead;
	
				while((currentRead = reader.readLine()) != null)
				{
					writer.write(currentRead + "\n");
				}
	
				reader.close();
				writer.close();
				System.out.println("ok");

				File file = new File(filename);
				if (file.exists())
				{
					if (file.length() > 0) return true;
					else if (!file.delete()) throw new RuntimeException(
						"Cannot delete empty file " + file.getPath());
				}
			}
			catch (IOException e)
			{
				System.out.println("failed!");
				writer.close();
			}
		}
		return false;
	}

	protected boolean downloadZipped(String[] urlStrings, String filename) throws IOException
	{
		for (String urlString : urlStrings)
		{
			System.out.print("Downloading compressed data from " + urlString + " ... ");

			Download.downloadAndUncompress(urlString, filename);

			System.out.println("ok");

			File file = new File(filename);
			if (file.exists())
			{
				if (file.length() > 0) return true;
				else if (!file.delete()) throw new RuntimeException(
					"Cannot delete empty file " + file.getPath());
			}
		}
		return false;
	}
}
