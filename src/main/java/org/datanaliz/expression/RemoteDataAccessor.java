package org.datanaliz.expression;

import org.datanaliz.Conf;

import java.io.*;
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
	protected abstract String getURL();

	protected abstract boolean isResourceZipped();

	protected void download(String urlString, String filename) throws IOException
	{
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();

		BufferedReader reader;
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

		reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String currentRead;

		while((currentRead = reader.readLine()) != null)
		{
			writer.write(currentRead + "\n");
		}

		reader.close();
		writer.close();
	}

	protected void downloadZipped(String urlString, String filename) throws IOException
	{
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();

		GZIPInputStream in = new GZIPInputStream(con.getInputStream());

		// Open the output file
		OutputStream out = new FileOutputStream(filename);

		// Transfer bytes from the compressed file to the output file
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
	}
}
