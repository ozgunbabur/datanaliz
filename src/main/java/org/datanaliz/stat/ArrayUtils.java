package org.datanaliz.stat;

import java.util.Arrays;

/**
 * @author Ozgun Babur
 */
public class ArrayUtils
{
	public static int[] getRanks(double[] v)
	{
		Holder[] h = new Holder[v.length];
		for (int i = 0; i < v.length; i++)
		{
			h[i] = new Holder(v[i], i);
		}
		Arrays.sort(h);
		int[] rank = new int[v.length];
		for (int i = 0; i < v.length; i++)
		{
			rank[h[i].index] = i;
		}
		return rank;
	}

	public static int[] getRankOrderedIndexes(double[] v)
	{
		int[] r = getRanks(v);
		int[] ind = new int[r.length];
		for (int i = 0; i < r.length; i++)
		{
			ind[r[i]] = i;
		}
		return ind;
	}

	public static void reverseRank(int [] rank)
	{
		for (int i = 0; i < rank.length; i++)
		{
			rank[i] = rank.length - 1 - rank[i];
		}
	}

	public static void setPosToRankRange(int[] rank, boolean[] pos, int start, int stop)
	{
		assert rank.length == pos.length;
		assert start >= 0;
		assert start < rank.length;
		assert stop > 0;
		assert stop <= rank.length;
		assert start < stop;

		for (int i = 0; i < pos.length; i++)
		{
			pos[i] = rank[i] >= start && rank[i] < stop;
		}
	}

	public static double[] getPortion(double [] v, boolean [] pos)
	{
		int cnt = countTrue(pos);
		double[] port = new double[cnt];
		int j = 0;
		for (int i = 0; i < v.length; i++)
		{
			if (pos[i]) port[j++] = v[i];
		}
		assert j == cnt;
		return port;
	}

	public static int countTrue(boolean[] b)
	{
		int t = 0;
		for (boolean b1 : b)
		{
			if (b1) t++;
		}
		return t;
	}

	public static boolean[] unite(boolean [] ... pos)
	{
		boolean[] u = new boolean[pos[0].length];

		for (int i = 0; i < u.length; i++)
		{
			for (int j = 0; j < pos.length; j++)
			{
				if (pos[j][i])
				{
					u[i] = true;
					break;
				}
			}
		}
		return u;
	}

	public static void reverse(double[] x)
	{
		for (int i = 0; i < x.length / 2; i++)
		{
			double temp = x[i];
			x[i] = x[x.length - i - 1];
			x[x.length - i - 1] = temp;
		}
	}

	public static boolean[][] getSlidingWindow(double[] values, boolean[] pos, int window)
	{
		assert values.length == pos.length;

		Holder[] h = new Holder[countTrue(pos)];

		assert h.length >= window;

		int j = 0;
		for (int i = 0; i < values.length; i++)
		{
			if (pos[i])
			{
				h[j++] = new Holder(values[i], i);
			}
		}
		Arrays.sort(h);

		// number of windows
		int wn = h.length - window + 1;

		boolean[][] w = new boolean[wn][pos.length];

		for (int i = 0; i < wn; i++)
		{
			w[i] = new boolean[pos.length];

			for (j = i; j < window + i; j++)
			{
				w[i][h[j].index] = true;
			}
		}
		return w;
	}

	public static int getIndexOf(String[] array, String name)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i].equals(name)) return i;
		}
		return -1;
	}

	static class Holder implements Comparable
	{
		Double value;
		int index;

		Holder(Double value, int index)
		{
			this.value = value;
			this.index = index;
		}

		public int compareTo(Object o)
		{
			return value.compareTo(((Holder) o).value);
		}
	}
}
