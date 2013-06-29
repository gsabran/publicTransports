package SOTA;

import main.*;
import other.p;
import other.utils;
import plot.plot;
import other.Transform;
import structure.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public abstract class oneSourceSOTA extends SOTA {
	public String type = "";

	public void result() {
		Node origin = this.origins.get(0);
		int L = u[0].length;
		List<Node> l = new List<Node>();
		Browse<Pair<Node, Integer>> temp = optOrder.browse();
		while (temp.hasNext()) {
			Node n = graph.nodes[temp.next().x.id];
			l.add(n);
		}
		possibleNodes = possibleNodes(origin, false);
		List<Edge> e = new withAverageTT().dijkstra(graph, origin.id,
				destination.id, false);
		type += fft ? "Fft" : "";
		type += this.zeroDelay ? "0d" : "";
		type += loop ? "L" : "";
		type += withOptOrder ? "OptO" : "";
		List<Node> list = new List<Node>();
		list.add(origin);
		list.add(destination);
		float[] probaPossibleNodes = probabilityToUseTheNode();
		plot.plotGraph(
				originalGraph,
				null,
				l,
				possibleNodes,
				probaPossibleNodes,
				list,
				true,
				null,// this.nbUpdatePerNode,
				type
						+ " #"
						+ equivalent[origin.id]
						+ " to #"
						+ equivalent[destination.id]
						+ " in "
						+ budget
						+ "s, CT: "
						+ utils.roundToSignificantFigures(computationalTime, 2)
						+ ", u: "
						+ utils.roundToSignificantFigures(u[origin.id][L - 1],
								4), 0, 0, 0, 0, 0, 0, 0, 0, 0, equivalent, budget);
		double p = Transform.convolutionAlongPath(e, budget);
		double t = utils.minTT(e);
		System.out
				.println("LET path gives p="
						+ p);
		System.out
				.println("The minTT in the LET path is "
						+ t);
		System.out.println("The probability to arrive on time is "
				+ u[origin.id][L - 1]);
		other.p.w("total length of updates: " + lengthUpdate);
	}

	public float[] probabilityToUseTheNode() {
		// this don't give the exact probability but a general idea (else, we
		// had to consider some ordering to guaranty exactness)
		int networkSize = graph.nodes.length;
		float[] res = new float[networkSize];
		double[][] proba = new double[networkSize][L];
		int[] initialized = new int[networkSize];
		initialized[origins.get(0).id] = L - 1;
		proba[origins.get(0).id][L - 1] = 1;
		for (int t = L - 1; t >= 0; t--) {
			for (int i = 0; i < networkSize; i++) {
				if (initialized[i] >= t && nextNode[i][t] != -1) {
					double p = proba[i][t];
					Edge e = graph.getEdgeByExtremities(i, nextNode[i][t]);
					if (initialized[e.end.id] == 0) {
						initialized[e.end.id] = t - e.realMinTT;
					}
					double[] distribution = e.getDistribution();
					for (int t2 = e.realMinTT; t2 <= e.realMaxTT && t2 <= t; t2++) {
						proba[e.end.id][t - t2] += p * distribution[t2-e.realMinTT];
					}
				}
			}
		}
		double probaMax = 0;
		for (int i = 0; i < networkSize; i++) {
			for (int t = 0; t < L; t++) {
				res[i] += proba[i][t];
			}
			if (res[i] >= probaMax) {
				probaMax = res[i];
			}
		}

		for (int i = 0; i < networkSize; i++) {
			res[i] /= probaMax;
		}
		return res;
	}

	public void pruneIniFromOrigin() {
		//do an additional pruning to pruneIniFromDest when we know the source
		Node origin = origins.get(0);
		HashMap<Integer, Integer> ini = new HashMap<Integer, Integer>();
		ini.put(origin.id, 0);
		HashMap<Integer, Integer> set = new HashMap<Integer, Integer>();
		int currentClosest = 0;
		while (!ini.isEmpty() && currentClosest <= (int) (budget / main.deltaT)) {
			Set<Integer> keys = ini.keySet();
			Iterator<Integer> it = keys.iterator();
			int idClosest = -1;
			int distClosest = Integer.MAX_VALUE;
			while (it.hasNext()) {
				int a = it.next();
				int d = ini.get(a);
				if (distClosest > d) {
					idClosest = a;
					distClosest = d;
				}
			}
			assert idClosest != -1;
			ini.remove(idClosest);
			set.put(idClosest, distClosest);
			Browse<Edge> br = graph.going[idClosest].browse();
			while (br.hasNext()) {
				Edge e = br.next();
				if (!set.containsKey(e.end.id)) {
					if (ini.containsKey(e.end.id)) {
						int previous = ini.get(e.end.id);
						ini.put(e.end.id,
								Math.min(previous, distClosest + (int) e.minTT));
					} else if (distClosest + (int) e.minTT + minTTtoDest[e.end.id] <= L) {
						//this is where there is a change
						ini.put(e.end.id, distClosest + (int) e.minTT);
					}
				}
			}
		}
		int prunedNetworkSize = set.size();
		Node[] n = new Node[prunedNetworkSize];
		List<Edge>[] g = new List[prunedNetworkSize];
		List<Edge>[] c = new List[prunedNetworkSize];
		minTTfromOr = new int[prunedNetworkSize];
		int[] equivalentTemp = new int[prunedNetworkSize];
		int[] equivalentTemp2 = new int[prunedNetworkSize];
		//equi give the id relation between the first pruning to the original
		//equiT from the second pruning the original
		//equiT2 from the second pruning the first
		Set<Integer> keys = set.keySet();
		Iterator<Integer> it = keys.iterator();
		int pos = 0;
		while (it.hasNext()) {
			int a = it.next();
			n[pos] = graph.nodes[a];
			minTTfromOr [pos] = set.get(a);
			equivalentTemp[pos] = equivalent[a];
			equivalentTemp2[pos]=a;
			pos++;
		}
		for (int i = 0; i < n.length; i++) {
			g[i] = new List<Edge>();
		}
		for (int i = 0; i < n.length; i++) {
			Browse<Edge> br = graph.going[equivalentTemp2[i]].browse();
			while (br.hasNext()) {
				Edge e = br.next();
				if (set.containsKey(e.end.id)) {
					g[i].add(e);
				}
			}
		}
		for (int i = 0; i < n.length; i++) {
			c[i] = new List<Edge>();
			n[i].id = i;
		}
		for (int i = 0; i < n.length; i++) {
			Browse<Edge> br = g[i].browse();
			while (br.hasNext()) {
				Edge e = br.next();
				c[e.end.id].add(e);
			}
		}
		equivalent = equivalentTemp;
		graph = new Graph(n,g,c,graph.size);
	}
}
