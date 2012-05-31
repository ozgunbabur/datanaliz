package org.datanaliz.expression;

import org.datanaliz.chart.ExpressionDistributionChart;
import org.datanaliz.stat.Histogram;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Ozgun Babur
 */
public class CCLETest
{
	@Test
	public void loadTestCCLE()
	{
	}

	public static void main(String[] args)
	{
		Collection<String> filter = Arrays.asList(GENE_NAMES_1.split("\n"));
		CCLE ccle = new CCLE("CCLEPlusSKMELNormalized", filter);
		ExpSet expSet = ccle.getExpSet();
		ExpressionDistributionChart chart = new ExpressionDistributionChart(expSet);
		chart.open();
	}
	private static final String GENE_NAMES_1 =
		"EIF4EBP1\n" +
		"RB1\n" +
		"MAPK3" +
		"MAPK1\n" +
		"MAP2K1\n" +
		"RPS6\n" +
		"SERPINE1\n" +
		"AKT1\n" +
		"PRKAA1" +
		"PRKAA2\n" +
		"CTNNB1\n" +
		"BCL2L11\n" +
		"CAV1\n" +
		"CCNB1\n" +
		"CCND1\n" +
		"GSK3A\n" +
		"GSK3B\n" +
		"IGFBP2\n" +
		"MAPK14\n" +
		"TP53\n" +
		"RPS6KB1\n" +
		"SRC\n" +
		"STAT3\n" +
		"TSC2\n" +
		"YAP1\n" +
		"ACACA\n" +
		"AKT1\n" +
		"AKT1\n" +
		"CTNNB1\n" +
		"FN1\n" +
		"HSPB1\n" +
		"IGF1R\n" +
		"IRS1\n" +
		"CDKN1B\n" +
		"MYC\n" +
		"SMAD3\n" +
		"STAT3\n" +
		"STAT5A\n" +
		"STAT5A\n" +
		"PTGS2\n" +
		"PAX2\n" +
		"PLK1\n" +
		"YBX1\n" +
		"CHEK2\n" +
		"RPS6\n" +
		"RPS6\n" +
		"AKT1\n" +
		"CDK4\n" +
		"JAK1\n" +
		"JAK2\n" +
		"JAK3\n" +
		"BRAF\n" +
		"MTOR\n" +
		"PRKCA\n" +
		"PIK3CG\n" +
		"PIK3CA\n" +
		"SRC\n" +
		"MDM2\n" +
		"MAP2K1\n" +
		"STAT3";
}



