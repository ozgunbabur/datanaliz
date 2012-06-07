package org.datanaliz.expression;

import org.datanaliz.Conf;
import org.datanaliz.util.DelimFileParser;

import java.util.*;

/**
 * @author Ozgun Babur
 */
public class CCLE extends MAS5Output
{
	public CCLE(String id)
	{
		this(id, null);
	}

	public CCLE(String id, Collection<String> geneFilter)
	{
		super(id, geneFilter);
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
		DelimFileParser p = new DelimFileParser(getClass().getResourceAsStream(
			"CCLE_sample_info.txt"));

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
		DelimFileParser p = new DelimFileParser(getClass().getResourceAsStream(
			"CCLE_sample_info.txt"));

		Map<String, String> one2one = p.getOneToOneMap("Cell line primary name", "Site Primary");
		Map<String, String[]> one2many = new HashMap<String, String[]>();
		for (String key : one2one.keySet())
		{
			one2many.put(key, new String[]{one2one.get(key)});
		}
		expSet.setSubgroups(one2many);
	}

	@Override
	protected String[] getURL()
	{
		return new String[]{Conf.REMOTE_RESOURCE + id + ".txt.gz"};
	}

	@Override
	protected boolean isResourceZipped()
	{
		return true;
	}
}
