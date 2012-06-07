package org.datanaliz.expression;

import org.datanaliz.Conf;
import org.junit.Test;

import java.io.File;

public class ExpressionFileMergerTest
{
	@Test
	public void mergeTest()
	{
		ExpressionFileMerger m = new ExpressionFileMerger();
		boolean b = m.merge(Conf.DATA_FOLDER + "CCLEExpData.txt",
			Conf.DATA_FOLDER + "SKMEL.txt",
			Conf.DATA_FOLDER + "CCLEPlusSKMEL.txt");
		System.out.println("b = " + b);
	}
}
