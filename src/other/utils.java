package other;

import plot.plot;
import structure.*;

import java.lang.management.*;
import java.text.DecimalFormat;

public class utils {
	// different functions
	
	public static int timeToInt(String t){
		//return the value in s of t for the format "HH:mm:ss"
		String[] temp = t.split(":");
		int h = Short.valueOf(temp[0]).shortValue();
		int m = Short.valueOf(temp[1]).shortValue();
		int s = Short.valueOf(temp[2]).shortValue();
		return 3600*h+60*m+s;
	}

	public static double minTT(List<Edge> l) {
		Browse<Edge> temp = l.browse();
		double res = 0;
		while (temp.hasNext()) {
			Edge e = temp.next();
			res += e.minTT;
		}
		return res;
	}
	
	private static double distribution(Edge e, double x) {
		double res = 0;
			res +=  Math.exp(-(x - e.average) * (x - e.average)
							/ (2 * e.average))
					/ (Math.sqrt(2 * Math.PI * e.average));
		
		return res;
	}

	public static double[] distribution(double moy, double var, double disc,
			int nbDisc) {
		double sigma = Math.sqrt(var);
		double[] res = new double[nbDisc];
		int step = 1000;
		double adaptedDisc = Math.min(disc,
				disc / (Math.ceil(disc * step / sigma)));
		int size = 6; //we go until 6*sigma
		for (int i = 0; i < nbDisc; i++) {
			if (i * disc > moy - size * sigma && i * disc < moy + size * sigma) {
				for (int j = 0; j < Math.ceil(disc * step / sigma); j++) {
					double xi = i * disc + j * adaptedDisc;
					res[i] += Math.exp(-(xi - moy) * (xi - moy) / (2 * var))
							/ (Math.sqrt(2 * Math.PI * var)) * adaptedDisc;
				}
			}
		}
		double xi = moy - size * sigma;
		while (xi < 0) {
			res[0] += Math.exp(-(xi - moy) * (xi - moy) / (2 * var))
					/ (Math.sqrt(2 * Math.PI * var)) * sigma / step;
			xi += sigma / step;
		}
		return res;
	}

	public static double roundToSignificantFigures(double num, int n) {
		if (num == 0) {
			return 0;
		}

		final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
		final int power = n - (int) d;

		final double magnitude = Math.pow(10, power);
		final long shifted = Math.round(num * magnitude);
		return shifted / magnitude;
	}

	public static String decimal(double d, int n) {
		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(n);
		return f.format(d);

	}

	public static long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean
				.getCurrentThreadCpuTime() : 0L;
	}



	public static int positionOf(double[] t, double val) {
		for (int i = 0; i < t.length; i++) {
			if (t[i] == val) {
				return i;
			}
		}
		return -1;
	}

	public static double[] minAndMax(double[] t) {
		//give the min and the max of t
		double[] res = new double[2];
		res[0] = t[0];
		res[1] = t[0];
		for (int i = 1; i < t.length / 2; i++) {
			double m, M;
			if (t[2 * i] > t[2 * i - 1]) {
				M = t[2 * i];
				m = t[2 * i - 1];
			} else {
				m = t[2 * i];
				M = t[2 * i - 1];
			}
			res[0] = Math.min(res[0], m);
			res[1] = Math.max(res[1], M);
		}
		if (t.length % 2 == 0) {
			res[0] = Math.min(res[0], t[t.length - 1]);
			res[1] = Math.max(res[1], t[t.length - 1]);
		}
		return res;
	}

	public static double sum(double[] t) {
		double res = 0;
		for (double d : t) {
			res += d;
		}
		return res;
	}

	public static int[] resize(double[] sizeX, double[] sizeY, double x1,
			double x2, double y1, double y2, int w, int h) {
		int[] res = new int[4];
		res[0] = (int) ((x1 - sizeX[0]) * w / (sizeX[1] - sizeX[0]));
		res[1] = (int) (h - (y1 - sizeY[0]) * h / (sizeY[1] - sizeY[0]));
		res[2] = (int) ((x2 - sizeX[0]) * w / (sizeX[1] - sizeX[0]));
		res[3] = (int) (h - (y2 - sizeY[0]) * h / (sizeY[1] - sizeY[0]));
		return res;
	}

	public static double argument(double a, double b) {
		// return the argument of a+i*b between -PI, PI
		if (a == 0) {
			if (b > 0) {
				return Math.PI / 2;
			} else {
				return -Math.PI / 2;
			}
		} else {
			double theta = Math.atan(b / a);
			if (a > 0) {
				return theta;
			} else {
				if (theta >= 0) {
					return theta - Math.PI;
				} else {
					return theta + Math.PI;
				}
			}
		}
	}
}
