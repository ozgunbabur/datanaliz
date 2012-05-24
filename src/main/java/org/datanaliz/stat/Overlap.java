package org.datanaliz.stat;

import java.util.*;

/**
 * @author Ozgun Babur
 */
public class Overlap
{
	public static double calcPVal(int n, int a, int b, int o)
	{
		// Make sure that all parameters are non-negative
		
		if (n < 0 || a < 0 || b < 0 || o < 0) throw new IllegalArgumentException(
			"All parameters should be non-negative. n="+n+" a="+a+" b="+b+" o="+o);
		
		// Make sure that n >= a >= b >= o

		if (o > a) throw new IllegalArgumentException("Overlap cannot be more than a");
		if (o > b) throw new IllegalArgumentException("Overlap cannot be more than b");
		if (a > n) throw new IllegalArgumentException("a cannot be greater than sample size");
		if (b > n) throw new IllegalArgumentException("b cannot be greater than sample size");
		if (o < b-(n-a)) throw new IllegalArgumentException("o cannot be lower than b-(n-a)");

		if (n == 0) return 1;
		
		if (b > a)
		{
			int t = b;
			b = a;
			a = t;
		}

		double e = (a * b) / (double) n;

		double pval = 0;

		if (o >= e)
		{
			for (int i = o; i <= b; i++)
			{
				pval += calcProb(n, a, b, i);
			}
			return pval;
		}
		else // (o < e)
		{
			for (int i = 0; i <= o; i++)
			{
				pval += calcProb(n, a, b, i);
			}
			return -pval;
		}
	}

	/**
	 * Calculated the probability that sets a and b have exactly x overlaps.
	 * @param n
	 * @param a
	 * @param b
	 * @param x
	 * @return
	 */
	protected static double calcProb(int n, int a, int b, int x)
	{
		FactorialSolver s = new FactorialSolver(
			new ArrayList<Integer>(Arrays.asList(a, b, (n-a), (n-b))),
			new ArrayList<Integer>(Arrays.asList(n, x, (a-x), (b-x), (n-a-b+x))));
		
		return s.solve();
	}

	public static void main(String[] args)
	{
		System.out.println("pval = " + calcPVal(3, 3, 1, 1));
	}
}
