package org.datanaliz.expression;

import org.datanaliz.util.DelimFileParser;

import java.util.*;

/**
 * @author Ozgun Babur
 */
public class CCLE extends MAS5Output
{
	public CCLE()
	{
		this(null);
	}

	public CCLE(Collection<String> geneFilter)
	{
		super("CCLEExpData", geneFilter);
		convertExpnamesToCellLines();
		readTissues();
	}

	@Override
	public String getPlatform()
	{
		return "GPL570";
	}

	protected void convertExpnamesToCellLines()
	{
		DelimFileParser p = new DelimFileParser(
			this.getClass().getClassLoader().getResource("CCLE_sample_info.txt").getFile());

		Map<String,String> map = p.getOneToOneMap("Expression arrays", "Cell line primary name");
		String[] names = new String[expSet.getExpname().length];
		for (int i = 0; i < names.length; i++)
		{
			names[i] = map.get(expSet.getExpname()[i].replace(".CEL", ""));
			assert names[i] != null;
		}
		expSet.setExpname(names);
	}

	protected void readTissues()
	{
		DelimFileParser p = new DelimFileParser(
			this.getClass().getClassLoader().getResource("CCLE_sample_info.txt").getFile());

		expSet.setSubgroups(p.getOneToOneMap("Cell line primary name", "Site Primary"));
	}

	@Override
	protected String getURL()
	{
		return "http://cbio.mskcc.org/~ozgun/" + id + ".txt.gz";
	}

	@Override
	protected boolean isResourceZipped()
	{
		return true;
	}
}
