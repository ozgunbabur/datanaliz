package org.datanaliz.stat;
/**
 * An implementation for calculating pearson correlation between gene expressions.
 *
 * @author Ozgun Babur
 */
public class Pearson
{
	public static int frequency(int[] x, int target)
	{
		int freq = 0;
		for (int v : x)
		{
			if (v == target) freq++;
		}
		return freq;
	}

	public static double calcCorrelation(Double[] a, Double[] b)
	{
		double[][] x = new double[2][a.length];
		for (int i = 0; i < a.length; i++)
		{
			x[0][i] = a[i];
			x[1][i] = b[i];
		}
		return calcCorrelation(x);
	}

	public static double calcCorrelation(double[] a, double[] b)
	{
		return calcCorrelation(new double[][]{a, b});
	}

	public static double calcCorrelation(double[] a, double[] b, boolean[] pos)
	{
		return calcCorrelation(new double[][]{a, b}, pos);
	}

	public static double calcCorrelation(double[][] x)
	{
		double mean0 = Summary.mean(x[0]);
		double mean1 = Summary.mean(x[1]);

		double sd0 = Summary.stdev(x[0]);
		double sd1 = Summary.stdev(x[1]);

		double corr = 0;

		for (int i = 0; i < x[0].length; i++)
		{
			corr += (x[0][i] - mean0) * (x[1][i] - mean1);
		}

		corr /= (x[0].length) * sd0 * sd1;
		if (corr > 1) corr = 1; else if (corr < -1) corr = -1;
		return corr;
	}

	public static double calcCorrelation(double[][] x, boolean[] pos)
	{
		double mean0 = Summary.mean(x[0], pos);
		double mean1 = Summary.mean(x[1], pos);

		double sd0 = Summary.stdev(x[0], pos);
		double sd1 = Summary.stdev(x[1], pos);

		int cnt = 0;

		double corr = 0;

		for (int i = 0; i < x[0].length; i++)
		{
			if (!pos[i]) continue;

			cnt++;
			corr += (x[0][i] - mean0) * (x[1][i] - mean1);
		}

		corr /= (cnt - 1) * sd0 * sd1;
		if (corr > 1) corr = 1; else if (corr < -1) corr = -1;
		return corr;
	}

	//----------------- Correlation significance --------------------------------------------------|

	public static double calcCorrSignificance(double corr, int n)
	{
		// This implementation only handles cases where n >= 6
		if (n < 6) return 1;

		int df = n - 2;
		double t_denom = Math.sqrt((1 - (corr * corr)) / (n - 2));
		double t = corr / t_denom;
		return buzz(t, df) / 2;
	}

	private static final double pj2 = Math.PI / 2;
	private static double buzz(double t, int df)
	{
		t = Math.abs(t);
		double rt = t / Math.sqrt(df);
		double fk = Math.atan(rt);
		if (df == 1) return 1 - fk / pj2;

		double ek = Math.sin(fk);
		double dk = Math.cos(fk);

		if ((df % 2) == 1) return 1 - (fk + ek * dk * zip(dk * dk, 2, df - 3, -1)) / pj2;
		else return 1 - ek * zip(dk * dk, 1, df - 3, -1);
	}

	private static double zip(double q, int i, int j, int b)
	{
		double zz = 1;
		double z = zz;
		int k = i;
		while(k <= j)
		{
			zz = zz * q * k / (k - b);
			z = z + zz;
			k = k + 2;
		}
		return z;
	}

	// http://faculty.vassar.edu/lowry/rdiff.html
	public static double calcSignificanceOfCorrDifference(double ra, int na, double rb, int nb)
	{
		if(ra*ra==1) {ra = ra*.999;}
		if(rb*rb==1) {rb = rb*.999;}
		if(ra*ra>1)
		{
			throw new RuntimeException("A value of r must fall between +1.0 and -1.0, inclusive.");
		}
		if(rb*rb>1)
		{
			throw new RuntimeException("A value of r must fall between +1.0 and -1.0, inclusive.");
		}

		double raplus = 1*ra+1;
		double raminus = 1-ra;
		double rbplus = 1*rb+1;
		double rbminus = 1-rb;

		if(na<4 || nb<4)
		{
			throw new RuntimeException("n must be equal to or greater than 4.");
		}

		double za = (Math.log(raplus)-Math.log(raminus))/2;
		double zb = (Math.log(rbplus)-Math.log(rbminus))/2;

		double se = Math.sqrt((1/(na-3D))+(1/(nb-3D)));
		double z = (za-zb)/se;
		z = Math.round(z*100)/100;

		double z2 = Math.abs(z);

		double p2 =(((((.000005383*z2+.0000488906)*z2+.0000380036)*z2+.0032776263)*z2+.0211410061)*z2+.049867347)*z2+1;

		// two tailed p-value
		p2 = Math.pow(p2, -16);

//		double p1 = p2/2;

		return p2;
	}


	//------------------ Sliding window -----------------------------------------------------------|

	public static double[][] calcCorrInSlidingWindow(double[] v1, double[] v2, double ratio)
	{
		return calcCorrInSlidingWindow(new double[][]{v1, v2}, ratio);
	}

	public static double[][] calcCorrInSlidingWindow(double[][] v, double ratio)
	{
		int window = (int) Math.round(v[0].length * ratio);
		boolean[] pos = new boolean[v[0].length];
		int[] rank = ArrayUtils.getRanks(v[0]);

		double[][] chart = new double[2][rank.length - window + 1];

		for (int i = 0; i <= rank.length - window; i++)
		{
			ArrayUtils.setPosToRankRange(rank, pos, i, i + window);
			double mean = Summary.mean(v[0], pos);
			double cor = calcCorrelation(v, pos);

			chart[0][i] = mean;
			chart[1][i] = cor;
		}
		return chart;
	}

	public static double[][] calcSignificantCorrInSlidingWindow(double[] v1, double[] v2,
		double ratio, double thr)
	{
		return calcSignificantCorrInSlidingWindow(new double[][]{v1, v2}, ratio, thr);
	}

	public static double[][] calcSignificantCorrInSlidingWindow(double[][] v, double ratio, double thr)
	{
		assert thr > 0 && thr <= 1;

		int window = (int) Math.round(v[0].length * ratio);
		boolean[] pos = new boolean[v[0].length];
		int[] rank = ArrayUtils.getRanks(v[0]);

		double[][] chart = new double[2][rank.length - window + 1];

		for (int i = 0; i <= rank.length - window; i++)
		{
			ArrayUtils.setPosToRankRange(rank, pos, i, i + window);
			double mean = Summary.mean(v[0], pos);
			double cor = calcCorrelation(v, pos);
			double pv = calcCorrSignificance(cor, window);

			chart[0][i] = mean;
			chart[1][i] = pv < thr ? cor : 0;
		}
		return chart;
	}

	public static void main(String[] args)
	{
		double[] v1 = new double[]{0, 1, 2};
		double[] v2 = new double[]{0, 1, 2};
		double c = calcCorrelation(v1, v2);
		System.out.println("c = " + c);
	}

}
