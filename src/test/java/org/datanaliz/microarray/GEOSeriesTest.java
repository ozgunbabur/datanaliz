package org.datanaliz.microarray;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Ozgun Babur
 */
public class GEOSeriesTest
{
	@Test
	public void loadTest()
	{
		String id = "GSE3325";
		Collection<String> filter = Arrays.asList("AR", "TP53");
		GEOSeries ser = new GEOSeries(id, filter);
		ExpSet expSet = ser.getExpSet();

		for (String s : filter)
		{
			for (GeneExp ge : expSet.getBySymbol(s))
			{
				System.out.println("ge = " + ge);
			}
		}
	}
}
