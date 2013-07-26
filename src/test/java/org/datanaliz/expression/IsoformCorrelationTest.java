package org.datanaliz.expression;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Ozgun Babur
 */
public class IsoformCorrelationTest
{
	@Test
	@Ignore
	public void printCorrelations()
	{
		String sym = "FN1";
		GEOSeries ser = new GEOSeries("GSE32474", Collections.singleton(sym));
		System.out.println(ser.getExpSet().getIsoformCorrelationsInString(sym));
	}
}
