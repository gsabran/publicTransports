package structure;

import main.main;
import other.*;

public class Edge {
	public Node start;
	public Node end;
	public float minTT;
	public double[] distribution;
	public float average;
	public float var;

	private double disc;
	public int[] quantiles;
	public int realMinTT;// this value correspond to the chosen discretization
	public int realMaxTT;
	public boolean landmark;
	public boolean[] landmarks;

	public double[] getDistribution() {
		int nbDisc = (int) (main.Tmax / main.deltaT);
		double disc = main.deltaT;
		if (this.distribution != null && this.disc == disc) {
			return this.distribution;
		} else {
			if (average == 0 && var == 0) {
				this.distribution=new double[1];
				this.distribution[0]=1;
				realMaxTT=0;
				realMinTT=0;
				this.disc=disc;
				return this.distribution;
			} else {
				double[] res = new double[nbDisc];
				average = 0;
				double overSpeedLimit = 0;
				double[] dis = utils.distribution(average, var, disc, nbDisc);
				for (int j = 0; j < nbDisc; j++) {
					if (j * disc < minTT) {
						overSpeedLimit += dis[j];
					} else if ((j - 1) * disc < minTT) {
						average += (dis[j] + overSpeedLimit) * j * disc;
						res[j] += dis[j] + overSpeedLimit;
					} else {
						average += (dis[j]) * j * disc;
						res[j] += dis[j];
					}
				}
				double s = utils.sum(res);
				if (s > 1) {
					// HERE THEY CAN BE SOME RONUDING MISTAKES. IT IS VERY
					// IMPORTANT
					// TO HAVE s<=1
					// we renormalized the distribution
					// s<=1 is ok because it means that Tmax is too small
					for (int i = 0; i < res.length; i++) {
						res[i] /= s;
					}
					s = utils.sum(res);
					if (s > 1) {
						int indexMax = 0;
						double max = 0;
						for (int i = 0; i < res.length; i++) {
							assert res[i] >= 0 : "probability non positive";
							if (res[i] > max && res[i] > s - 1) {
								max = res[i];
								indexMax = i;
							}
						}
						res[indexMax] -= s - 1;
						s = utils.sum(res);
						assert s <= 1 : "probability higer than 1";
					}
				}
				if (main.nbQuantiles > 0) {
					quantiles = new int[main.nbQuantiles];
				}
				double current = 0;
				int toSet = 0;
				// p.w("*");
				for (int i = 0; i < res.length; i++) {
					current += res[i];
					while (main.nbQuantiles > 0
							&& current > ((double) toSet / (double) main.nbQuantiles)) {
						quantiles[toSet] = i;
						// p.w(quantiles[toSet]);
						toSet++;
					}
					assert res[i] >= 0 : "probability non positive";
				}
				assert utils.sum(res) <= 1 : "probability higer than 1";

				// computing realMinTT & realMaxTT
				int i = 0;
				while (i < nbDisc && res[i] == 0) {
					i++;
				}
				realMinTT = i;
				i = nbDisc - 1;
				while (i >= 0 && res[i] == 0) {
					i--;
				}
				realMaxTT = i;
				this.distribution = new double[Math.max(0, realMaxTT
						- realMinTT + 1)];
				System.arraycopy(res, realMinTT, distribution, 0,
						Math.max(0, realMaxTT - realMinTT + 1));
				this.disc = disc;
				return res;
			}
		}
	}

	public double average() {
		assert realMaxTT >= 0;
		getDistribution();
		assert realMaxTT >= 0;
		return this.average;
	}

	public double maxTT() {
		getDistribution();
		return this.realMaxTT;
	}

	public void reverse() {
		Node temp = start;
		start = end;
		end = temp;
	}

	public Edge clone() {
		Node n1 = start.clone();
		Node n2 = end.clone();
		Edge e = new Edge(n1, n2, minTT, average, var);
		e.average = average;
		e.disc = disc;
		e.distribution = distribution;
		e.landmark = landmark;
		e.landmarks = landmarks;
		e.quantiles = quantiles;
		e.realMaxTT = realMaxTT;
		e.realMinTT = realMinTT;
		return e;
	}

	public Edge(Node n1, Node n2, float TT, float var, float average) {
		start = n1;
		end = n2;
		this.minTT = TT;
		this.var = var;
		this.average = average;
	}

	public Edge() {
	}

	public String toString() {
		String s = "start node #" + start.id + " end node #" + end.id
				+ " min TT " + minTT + "\n";
		return s;
	}

	public void toText(String fileName) {
		String s = start.id + ";" + end.id + ";" + minTT + ";" + var + ";"
				+ average + ";";
		s += "\n";
		Writer.WriteFile(s, fileName);
	}

	public static Edge fromText(double[] t, Node[] n) {
		Node s = n[(int) t[0]];
		Node e = n[(int) t[1]];
		float TT = (float) t[2];
		float var = (float) t[3];
		float average = (float) t[4];
		Edge res = new Edge(s, e, TT, var, average);
		return res;
	}
}