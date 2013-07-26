package org.datanaliz.expression;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

public class MAS5OutputTest
{
	@Test
	@Ignore
	public void loadTest()
	{
		MAS5Output set = new MAS5Output("SKMEL", Arrays.asList("CCND1", "FN1"))
		{
			@Override
			public String getPlatform()
			{
				return "GPL96";
			}
		};

		for (String sm : set.expSet.getSymbols())
		{
			for (GeneExp ge : set.expSet.get(sm))
			{
				System.out.println("ge = " + ge);
			}
		}
	}
}
