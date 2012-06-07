package org.datanaliz.stat;

import org.datanaliz.Conf;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class QuantileNormalizerTest
{
	@Test
	public void normalizationTest() throws IOException
	{
		QuantileNormalizer.normalize(Conf.DATA_FOLDER + "CCLEPlusSKMEL.txt",
			Conf.DATA_FOLDER + "CCLEPlusSKMELNormalized.txt");
	}
}
