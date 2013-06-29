/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package other;


import java.util.Random;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import structure.*;

/**
 * 
 * @author Samitha
 */
public class Transform {

	public static double convolutionAlongPath(List<Edge> P, double budget) {
		if (P.size() > 0) {
			Browse<Edge> temp = P.browse();
			int nbDisc = (int) (main.main.Tmax / main.main.deltaT);
			double[] distribution = new double[nbDisc];
			Edge e1=temp.next();
			double[] list1 = e1.getDistribution();
			int firstNonNull = 0;
			while (list1[firstNonNull] == 0 && firstNonNull < list1.length) {
				firstNonNull++;
			}
			int tot = firstNonNull;
			System.arraycopy(list1, 0, distribution,e1.realMinTT, list1.length);

			while (temp.hasNext()) {
				Edge e = temp.next();
				list1 = e.getDistribution();
				firstNonNull = 0;
				while (firstNonNull < list1.length && list1[firstNonNull] == 0) {
					firstNonNull++;
				}
				tot += firstNonNull;
				distribution = basicConvolution(e, distribution, 0, distribution.length);

			}
			double res = 0;
			for (int i = 0; i < budget / main.main.deltaT && i<main.main.Tmax; i++) {
				res += distribution[i];
			}
			return res;
		} else {
			return 0;
		}
	}

	public static double[] fftConvolutionZeroDelay(double[] list1,
			double[] list2, double[] cumulZeroDelay, int tauBefore, int tauNow) {

		// Decide which operations to do in the time interval considered
		int powerOf2;
		int myLength;
		if (tauBefore == 0) {
			cumulZeroDelay[0] = list1[0] * list2[0];
		}
		for (int i = tauBefore; i < tauNow; i++) {
			if (i >= 1) {
				// Convolve most recent element by simple product
				cumulZeroDelay[i] += list1[i] * list2[0];
				cumulZeroDelay[i] += list1[i - 1] * list2[1];
				// Selection of FFT to be done
				powerOf2 = (int) Math.floor(Math.log(i) / Math.log(2));
				for (int j = 1; j <= powerOf2; j++) {
					if (i % (Math.pow(2, j)) == 0) {
						myLength = (int) Math.pow(2, j);
						// Selects correct components of the two inputs
						DoubleFFT_1D sameAsfftList = new DoubleFFT_1D(
								myLength * 2);
						double[] tempLeft = new double[myLength * 2];
						double[] tempRight = new double[myLength * 2];
						System.arraycopy(list1, i - myLength, tempLeft, 0,
								myLength);
						System.arraycopy(list2, myLength, tempRight, 0,
								myLength);
						// Do the convolution
						fftConvolution(tempLeft, tempRight, sameAsfftList);
						// Add the result to the cumulative sum
						for (int k = 0; k < myLength * 2; k++) {
							if (i + k < cumulZeroDelay.length) {
								cumulZeroDelay[i + k] += tempLeft[k];
							}
						}
					}
				}
			}
		}
		System.arraycopy(cumulZeroDelay, 0, list1, 0, tauNow);
		return list1;
	}
	
	public static double[] fftConvolution(double[] list1, double[] list2,
			DoubleFFT_1D fftList1) {
		int len1 = list1.length;
		fftList1.realForward(list1);
		fftList1.realForward(list2);
		if (len1 % 2 == 0) {
			list1 = complexMultEven(list1, list2);
		} else {
			list1 = complexMultOdd(list1, list2);
		}
		fftList1.realInverse(list1, true);
		return list1;
	}
	/**
	 * returns the basic O(n^2) convolution of two lists. It is assumed that
	 * both lists contain data starting at time index zero.
	 */
	public static double[] basicConvolution(Edge e, double[] list2, int start, int end) {
		// before startsupport and after endsupport list1 is null
		// we only want the convolution between start and end
		double[] list1 = e.getDistribution();
		int len2 = list2.length;
		double result[] = new double[len2];
		for (int i = start; i < Math.min(end, len2); i++) {
			for (int j = e.realMinTT; j <= i && j <= e.realMaxTT; j++) {
				result[i] = result[i] + list1[j-e.realMinTT] * list2[i - j];
			}
		}
		return result;
	}

	/**
	 * 
	 * returns the zero-delay convolution - list1 is the cumulative on-time
	 * arrival probability - list2 is the link travel-time density -
	 * cumulZeroDelay stores all previous convolutions (twice size of data) -
	 * tauBefore is the discretized time up to which the convolution has been
	 * computed - tauNow is the discretized time up to which the convolution is
	 * currently being computed
	 */
	

	/**
	 * returns the fft based O(nlogn) convolution of two lists. It is assumed
	 * that both lists contain data starting at time index zero.
	 */
	

	/**
	 * returns the pointwise multiplication of two even length complex arrays
	 * given by jtransforms.fft.realForward. Both arrays need to be of the same
	 * length.
	 */
	private static double[] complexMultEven(double[] list1, double[] list2) {
		int len1 = list1.length;
		int end1 = len1 - 1;
		double temp;
		list1[0] = list1[0] * list2[0];
		for (int i = 2; i < end1; i = i + 2) {
			temp = list1[i];
			list1[i] = temp * list2[i] - list1[i + 1] * list2[i + 1];
			list1[i + 1] = temp * list2[i + 1] + list1[i + 1] * list2[i];
		}
		list1[1] = list1[1] * list2[1];
		return list1;
	}

	/**
	 * returns the pointwise multiplication of two odd length complex arrays
	 * given by jtransforms.fft.realForward. Both arrays need to be of the same
	 * length.
	 */
	private static double[] complexMultOdd(double[] list1, double[] list2) {
		int len1 = list1.length;
		int end1 = len1 - 1;
		double temp;
		list1[0] = list1[0] * list2[0];
		for (int i = 2; i < end1; i = i + 2) {
			temp = list1[i];
			list1[i] = temp * list2[i] - list1[i + 1] * list2[i + 1];
			list1[i + 1] = temp * list2[i + 1] + list1[i + 1] * list2[i];
		}
		temp = list1[end1];
		list1[end1] = list1[end1] * list2[end1] - list1[1] * list2[1];
		list1[1] = temp * list2[1] + list1[1] * list2[end1];
		return list1;
	}

}