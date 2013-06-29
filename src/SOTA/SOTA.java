/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SOTA;

import main.*;
import structure.*;
import other.FoundException;
import other.Transform;
import other.utils;

import java.util.Map;
import other.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import plot.*;

public abstract class SOTA {

	public boolean fft;// different parameters
	public boolean zeroDelay;
	public boolean loop;
	public boolean withOptOrder;
	public boolean landmarks;
	public boolean updateForAllT;// do we want u[s][budget] or u[s] for all t?
	public Graph graph;// the graph pruned
	protected Graph originalGraph;// the graph
	protected int[] equivalent; // the id relation between nodes in the two
								// graphs
	public List<Node> origins;// the origin(s)
	public Node destination;// the destination
	public double[][] u;// u[i][t] is the probability to reach destination from
						// i in t
	public int[] lastUpdate;
	public double[][][] cumulZeroDelay;
	public short[][] nextNode;// the direction to take
	public short[] tau;// the current tau
	public short[] tauIni;// the first tau
	public short[] tauMax;// the value of tau after which we don't need to
							// compute
							// u
	public double budget;
	public List<Pair<Node, Integer>> optOrder;
	public double computationalTime;

	public int nbUpdate; // different measures used to compare the efficiency of
							// the algorithms
	public int nbReupdate;
	public int nbUsefulUpdate;
	public int lengthUsefulUpdate;
	public int lengthUpdate;
	public int lengthReupdate;
	public int updateUseOnlyOneSon;
	public short[] nbUpdatePerNode;

	public int[] minTTfromOr;
	public int[] minTTtoDest;
	public double[] minAverageTTfromOr;
	public double[] minAverageTTtoDest;
	public int L;
	public int networkSize;
	public List<Node> possibleNodes;

	public abstract boolean prune(Node n, int t);

	public void pruneIniFromDestination() {
		// make a first pruning of the graph, deleting the nodes unreachable
		// within the time budget from destination
		// initialize
		HashMap<Integer, Integer> ini = new HashMap<Integer, Integer>();
		ini.put(destination.id, 0);
		HashMap<Integer, Integer> set = new HashMap<Integer, Integer>();
		int currentClosest = 0;
		while (!ini.isEmpty() && currentClosest <= L) {
			// update like in Dijkstra algo
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
			currentClosest = distClosest;
			ini.remove(idClosest);
			set.put(idClosest, distClosest);
			Browse<Edge> br = originalGraph.coming[idClosest].browse();
			while (br.hasNext()) {
				Edge e = br.next();
				if (!set.containsKey(e.start.id)) {
					if (ini.containsKey(e.start.id)) {
						int previous = ini.get(e.start.id);
						ini.put(e.start.id,
								Math.min(previous, distClosest + (int) e.minTT));
					} else {
						ini.put(e.start.id, distClosest + (int) e.minTT);
					}
				}
			}
		}
		// create a new graph with only the interesting nodes
		int prunedNetworkSize = set.size();
		Node[] n = new Node[prunedNetworkSize];
		List<Edge>[] g = new List[prunedNetworkSize];
		List<Edge>[] c = new List[prunedNetworkSize];
		minTTtoDest = new int[prunedNetworkSize];
		equivalent = new int[prunedNetworkSize];// equivalent keep the relation
												// between the id
		HashMap<Integer, Integer> equivalentReverse = new HashMap<Integer, Integer>();
		Set<Integer> keys = set.keySet();
		Iterator<Integer> it = keys.iterator();
		int pos = 0;
		while (it.hasNext()) {
			int a = it.next();
			n[pos] = originalGraph.nodes[a].clone();
			n[pos].id = pos;
			minTTtoDest[pos] = set.get(a);
			equivalent[pos] = a;
			equivalentReverse.put(a, pos);
			pos++;
		}
		for (int i = 0; i < n.length; i++) {
			g[i] = new List<Edge>();
			c[i] = new List<Edge>();
		}
		for (int i = 0; i < n.length; i++) {
			Browse<Edge> br = originalGraph.going[equivalent[i]].browse();
			while (br.hasNext()) {
				Edge e = br.next();
				if (set.containsKey(e.end.id)) {
					Edge e2 = e.clone();
					e2.start = n[i];
					e2.end = n[equivalentReverse.get(e.end.id)];
					g[i].add(e2);
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
		destination = n[equivalentReverse.get(destination.id)];
		Browse<Node> br = origins.browse();
		List<Node> orTemp = new List<Node>();
		while (br.hasNext()) {
			Node o = br.next();
			if (set.containsKey(o.id)) {
				orTemp.add(n[equivalentReverse.get(o.id)]);
			}
		}
		origins = orTemp;
		graph = new Graph(n, g, c, originalGraph.size);
	}

	public abstract void pruneIniFromOrigin();

	// define a prune function according to the mode used.
	// basically, we prune the unreachable nodes

	public void initialize(double budget, List<Node> origins, Node destination,
			Graph g) {
		// initialized L, budget, graph, dest, nbUpdate, networkSize, minTTO/D,
		// tau, u, nextNode, cumulZeroDelay, lastUpdate, tauIni to there initial
		// values
		L = (int) Math.ceil(budget / main.deltaT); // number of discrete time
		this.budget = budget;
		this.originalGraph = g;
		this.origins = origins;
		this.destination = destination;
		this.nbUpdate = 0;

		pruneIniFromDestination();
		for (int i = 0; i < graph.nodes.length; i++) {
			Browse<Edge> b = graph.going[i].browse();
			while (b.hasNext()) {
				assert b.next().realMaxTT >= 0;
			}
		}
		if (!this.origins.isEmpty()) {
			optOrder = new List<Pair<Node, Integer>>();
			networkSize = (short) graph.nodes.length;
			if (this.origins.size() == 1) {
				pruneIniFromOrigin();
				networkSize = (short) graph.nodes.length;
				double[] minTTfromOrTemp = new withMinTT().minTTfromOrigin(
						graph, this.origins.get(0));
				minTTfromOr = new int[networkSize];
				for (int i = 0; i < networkSize; i++) {
					minTTfromOr[i] = (int) minTTfromOrTemp[i];
				}
				double[] minTTtoDestTemp = new withMinTT().minTTtoDestination(
						graph, this.destination);
				minTTtoDest = new int[networkSize];
				for (int i = 0; i < networkSize; i++) {
					minTTtoDest[i] = (int) minTTtoDestTemp[i];
				}
				minAverageTTfromOr = new withAverageTT().minTTfromOrigin(graph,
						this.origins.get(0));
				
			} else {
				minTTfromOr = new int[networkSize];
				minAverageTTfromOr = new double[networkSize];
			}
			for (int i = 0; i < graph.nodes.length; i++) {
				Browse<Edge> b = graph.going[i].browse();
				while (b.hasNext()) {
					assert b.next().realMaxTT >= 0;
				}
			}
			minAverageTTtoDest = new withAverageTT().minTTtoDestination(graph,
					this.destination);

			double[] minMaxTTtoDest = new withMaxTT().minTTtoDestination(graph,
					this.destination);

			// Initialize the tables
			this.tau = new short[networkSize];
			this.tauMax = new short[networkSize];
			this.u = new double[networkSize][L];
			this.nextNode = new short[networkSize][L];
			nbUpdatePerNode = new short[networkSize];
			if (zeroDelay) {
				this.cumulZeroDelay = new double[networkSize][10][2 * L];
			}
			if (loop) {
				lastUpdate = new int[networkSize];
				tauIni = new short[networkSize];
			}

			for (int i = 0; i < networkSize; i++) {
				this.tau[i] = (short) minTTtoDest[i];
				int minMaxTT = (int) minMaxTTtoDest[i];
				if (minMaxTT < L && minMaxTT != -1) {
					tauMax[i] = (short) minMaxTT;
					for (int t = 0; t < minMaxTT; t++) {
						this.nextNode[i][t] = -1;
					}
					int previous = -1;
					Browse<Edge> b = graph.going[i].browse();
					while (b.hasNext()) {
						Edge e = b.next();
						assert e.realMaxTT >= 0;
						if (minMaxTTtoDest[e.end.id] + e.maxTT() == minMaxTT) {
							previous = e.end.id;
						}
						assert e.maxTT() >= 0;
					}
					assert (previous != -1 || i == this.destination.id);
					for (int t = minMaxTT; t < L; t++) {
						this.nextNode[i][t] = (short) previous;
						this.u[i][t] = 1;
					}
				} else {
					tauMax[i] = (short) L;
					for (int t = 0; t < L; t++) {
						this.nextNode[i][t] = -1;
					}
				}

				if (prune(graph.nodes[i], L)) {
					this.tau[i] = (short) L;
				}
				if (tau[i] > L) {
					tau[i] = (short) L;
				}
				if (loop) {
					this.tauIni[i] = this.tau[i];
				}
			}
			// Initialize probability of reaching destination from destination
			// to 1
			this.tau[this.destination.id] = (short) L;
			for (int t = 0; t < L; t++) {
				this.u[this.destination.id][t] = 1;
			}
			p.w("minMaxTT: "+(int) minMaxTTtoDest[this.origins.get(0).id]);
		}
	}

	public void order() {
		if (loop) {
			optOrder = nodesOrderedByMinTTtoDestination();
		} else {
			// compute the optimal ordering
			List<Pair<Node, Integer>> set = new List<Pair<Node, Integer>>();
			ListPair<Node> initialized = new ListPair<Node>();

			Browse<Node> or = origins.browse();
			while (or.hasNext()) {
				Node n = or.next();
				initialized.add(n, tauMax[n.id]);
			}
			int currentTime = L;
			int count = 1;
			int[] idLastUpdate = new int[0];
			if (loop) {
				idLastUpdate = new int[networkSize];
			}
			while (!initialized.isEmpty()) {
				// find the first to update
				Pair<Node, Integer> maxKey = null;
				int maxValue = -1;
				Browse<Pair<Node, Integer>> it = initialized.browse();
				while (it.hasNext()) {
					Pair<Node, Integer> p = it.next();
					int value = p.y;
					if (value >= maxValue) {
						maxKey = p;
						maxValue = value;
					}
				}
				currentTime = maxValue;
				if (loop) {
					idLastUpdate[maxKey.x.id] = count;
					count++;
				} else {
					set.add(maxKey);
				}
				// change this
				initialized.remove(maxKey.x);

				// add necessary nodes to initialized
				Browse<Edge> l = graph.going[maxKey.x.id].browse();
				while (l.hasNext()) {
					Edge e = l.next();
					int firstArrival = (int) (currentTime - e.minTT);
					int nextImportantValue = Math.min(firstArrival,
							tauMax[e.end.id]);// after tauMax, we don't need to
												// compute u
					if (!prune(e.end, nextImportantValue)
							&& (initialized.contains(e.end) == 0 || initialized
									.get(e.end) < nextImportantValue)
							&& nextImportantValue >= tau[e.end.id]) {
						assert nextImportantValue <= L;
						initialized.add(e.end, nextImportantValue);
					}
				}
			}
			optOrder = set;
		}
	};

	protected void computeUpdate() {
		Browse<Pair<Node, Integer>> temp = optOrder.browse();
		computationalTime = utils.getCpuTime();
		while (temp.hasNext()) {
			Pair<Node, Integer> p = temp.next();
			int i = p.x.id; // node to update
			int tauNext = p.y; // update the node to this value
			if (loop) {
				nbUpdate = (short) updateNoLoop(i, tau[i], tauNext, nbUpdate);
			} else {
				this.lengthUpdate += tauNext - tau[i];
				this.update(i, tau[i], tauNext);
			}

		}
		computationalTime = utils.getCpuTime() - computationalTime;
	}

	public abstract void result();

	protected Pair<Integer, Integer> update(int i, int start, int tauNext) {
		assert tauNext <= L;
		// update i from start (included) to tauNext (excluded)
		int firstChange = L;
		int lastChange = 0;
		Browse<Edge> children = graph.going[i].browse();
		int nbChildren = graph.going[i].size();
		double[][] uTemp = new double[0][0];
		if (nbChildren > 0 && (tauNext - start > 0)) {
			int length = (int) (tauNext - start);
			uTemp = new double[nbChildren][tauNext];
			// iterate over the children of the link
			int j = 0;
			while (children.hasNext() && length > 0) {
				Edge e = children.next();

				double[] ufftchild = new double[tauNext * 2];
				double[] p = e.getDistribution();
				// START IS NOT USED TO IMPROVE THE CONVOLUTION SPEED
				if (zeroDelay) {
					double[] pfft = new double[tauNext * 2];
					System.arraycopy(u[e.end.id], 0, ufftchild, 0,
							Math.min(tauNext, u[e.end.id].length));
					System.arraycopy(p, 0, pfft, 0, Math.min(tauNext, p.length));
					ufftchild = Transform.fftConvolutionZeroDelay(ufftchild,
							pfft, cumulZeroDelay[i][j], start, tauNext);
					// the fft seems to do some rounding mistakes and gives some
					// probabilities slightly higher than 1:
					for (int k = 0; k < ufftchild.length; k++) {
						if (ufftchild[k] > 1) {
							ufftchild[k] = 1;
						}
					}
				}else{
				ufftchild = Transform.basicConvolution(e, u[e.end.id], start,
						tauNext);
				}
				// copy result from convolution to temp array
				System.arraycopy(ufftchild, 0, uTemp[j], 0, tauNext);
				j++;
			}

			for (int t = (tauNext - length); t < tauNext; t++) {
				double temp = u[i][t];
				children = graph.going[i].browse();
				Edge e = children.next();
				// set u(.) to probability of first child
				u[i][t] = uTemp[0][t];
				// mark the link to visit next as first child
				nextNode[i][t] = u[i][t] < main.zero ? -1 : (short) e.end.id;
				int k = 1;
				while (children.hasNext()) {

					e = children.next();
					if (uTemp[k][t] > u[i][t]) {
						u[i][t] = uTemp[k][t];
						nextNode[i][t] = u[i][t] < main.zero ? -1
								: (short) e.end.id;
					}
					k++;
				}
				assert u[i][t] <= 1 : "error while updating " + i
						+ ":probability higher than 1:" + u[i][t];
				if (temp < u[i][t]) {
					firstChange = Math.min(firstChange, t);
					lastChange = t;
				}
			}
			tau[i] = (short) Math.max(tau[i], tauNext);
			checkUseOnlyOneSon(i);
			return new Pair<Integer, Integer>(firstChange, lastChange);
		} else {
			for (int t = start; t < tauNext; t++) {
				// if there is no child, u=0
				u[i][t] = 0;
			}
			tau[i] = (short) Math.max(tau[i], tauNext);
			return new Pair<Integer, Integer>(L, 0);
		}
	}

	public int updateNoLoop(int i, int start, int end, int nbUpdate) {
		nbUpdatePerNode[i]++;
		nbUpdate++;
		lengthUpdate += end - start;
		lastUpdate[i] = nbUpdate;
		// p.w(((int)
		// (100*nbReupdate/nbUpdate))+"% of the updates are reupdates");
		Pair<Integer, Integer> p = this.update(i, start, end);
		if (p.x < L) {
			assert (p.x >= start && p.y <= end);
			nbUsefulUpdate++;
			lengthUsefulUpdate += end - start;
			// check if it is needed to reupdate the neighboors
			Browse<Edge> l = graph.coming[i].browse();
			while (l.hasNext()) {
				Edge e = l.next();
				Node n = e.start;
				if (lastUpdate[n.id] != 0) {
					int c = check(n.id, i, p.x);
					int minTT = (int) (e.minTT);
					if (c >= p.x + minTT && c < tau[n.id] && c < end + minTT) {
						Pair<Integer, Integer> p2 = check(n.id, i, p);
						nbReupdate++;
						lengthReupdate += tau[n.id] - c;
						nbUpdate = updateNoLoop(n.id, p2.x,
								Math.min(tau[n.id], p2.y + 1), nbUpdate);
					}
				}
			}
		}
		return nbUpdate;
	}

	public int updateNoLoop(int i, Pair<Integer, Integer> range, int nbUpdate) {
		// update for the time in toUpdate
		int start = range.x;
		int end = range.y;

		nbUpdatePerNode[i]++;
		nbUpdate++;
		lengthUpdate += end - start;
		lastUpdate[i] = nbUpdate;
		// p.w(((int)
		// (100*nbReupdate/nbUpdate))+"% of the updates are reupdates");
		Pair<Integer, Integer> p = this.update(i, range.x, range.y);
		if (p.x < L) {
			assert (p.x >= start && p.y <= end);
			nbUsefulUpdate++;
			lengthUsefulUpdate += end - start;
			// check if it is needed to reupdate the neighboors
			Browse<Edge> l = graph.coming[i].browse();
			while (l.hasNext()) {
				Edge e = l.next();
				Node n = e.start;
				if (lastUpdate[n.id] != 0) {
					Pair<Integer, Integer> c = check(n.id, i, p);
					int minTT = (int) (e.minTT);
					if (c.x >= p.x + minTT && c.x < tau[n.id]
							&& c.x < end + minTT) {

						nbReupdate++;
						lengthReupdate += c.y - c.x;
						nbUpdate = updateNoLoop(n.id, c, nbUpdate);
					}
				}
			}
		}
		return nbUpdate;
	}

	public int check(int a, int b, int start) {
		// return the time from which we may need to reupdate b
		Edge e = graph.getEdgeByExtremities(a, b);
		int minTT = (int) e.minTT;

		int t = start + minTT;
		while (t < tau[a] && t < tau[b] + minTT
				&& (u[a][t] >= upperBound(e, t))) {
			t++;
		}
		return t;
	}

	public Pair<Integer, Integer> check(int a, int b, Pair<Integer, Integer> p) {

		int firstChange = p.x;
		int lastChange = p.y;

		// return the time from which we may need to reupdate b
		Edge e = graph.getEdgeByExtremities(a, b);
		int minTT = (int) e.minTT;

		int t0 = firstChange + minTT;
		while (t0 < tau[a] && t0 < tau[b] + minTT
				&& (u[a][t0] >= upperBound(e, t0))) {
			t0++;
		}
		int t1 = tau[a];// Math.min(lastChange + e.realMaxTT,tau[a] - 1);
		/*
		 * while (t1 >= firstChange + minTT && (u[a][t1] >= upperBound(e, t1)))
		 * { //assert(u[b][t1-minTT]!=0); t1--; }
		 */// some buggs here
		return new Pair<Integer, Integer>(t0, t1);
	}

	/*
	 * public int updateNoLoop(int i, int[] toUpdate) { //update for the time in
	 * toUpdate int start =toUpdate[0]; int end = toUpdate[toUpdate.length-1];
	 * 
	 * nbUpdatePerNode[i]++; nbUpdate++; lengthUpdate += end - start;
	 * lastUpdate[i] = nbUpdate; // p.w(((int) //
	 * (100*nbReupdate/nbUpdate))+"% of the updates are reupdates"); int changed
	 * = this.update(i, toUpdate); if (changed < L) { assert (changed >= start);
	 * nbUsefulUpdate++; lengthUsefulUpdate += end - start; // check if it is
	 * needed to reupdate the neighboors Browse<Edge> l =
	 * graph.coming[i].browse(); while (l.hasNext()) { Edge e = l.next(); Node n
	 * = e.start; if (lastUpdate[n.id] != 0) { int[] c = check(n.id, i,
	 * changed); int minTT = (int) (e.w); if (c >= changed + minTT && c <
	 * tau[n.id] && c < end + minTT) {
	 * 
	 * nbReupdate++; lengthReupdate += tau[n.id] - c; nbUpdate =
	 * updateNoLoop(n.id, c, tau[n.id], nbUpdate); } } } } return nbUpdate; }
	 * 
	 * public int[] check(int a, int b, int firstChange, int lastChange) { //
	 * return the time from which we may need to reupdate b Edge e =
	 * graph.getEdgeByExtremities(a, b); int minTT = (int) e.w;
	 * 
	 * int t = start + minTT; while (t < tau[a] && t < tau[b] + minTT &&
	 * (u[a][t] >= upperBound(e, t))) { t++; } return t; }
	 */

	public double upperBound(Edge e, int t) {
		// give an upper bound of u[e.start][t] using e.end
		int a = e.start.id;
		int b = e.end.id;
		int minTT = (int) e.minTT;
		if (u[a][t] < u[b][t - minTT]) {
			// give an upper bound of u[a] is we go through b=e.end
			if (e.quantiles != null && main.nbQuantiles != 0) {
				double res = 0;
				int nbQuantiles = e.quantiles.length;
				for (int i = 0; i < nbQuantiles; i++) {
					res += u[b][Math.max(t - e.quantiles[i], 0)] / nbQuantiles;
				}
				return res;
			} else {
				return u[b][t - minTT];
			}
		} else {
			return u[b][t - minTT];
		}
	}

	public List<Node> possibleNodes(Node origin, boolean all) {
		// compute the possible nodes from next node
		// ListSorted<Node> toSet = new ListSorted<Node>(origin, -nbDisc);
		HashMap<Node, Integer> toSet = new HashMap<Node, Integer>();
		toSet.put(origin, L);
		List<Node> res = new List<Node>();
		while (!toSet.isEmpty()) {
			Node maxKey = null;
			int maxValue = 0;
			for (Map.Entry<Node, Integer> entry : toSet.entrySet()) {
				int value = entry.getValue();
				if (value > maxValue) {
					maxKey = entry.getKey();
					maxValue = value;
				}
			}
			toSet.remove(maxKey);
			Node n = maxKey;
			int t = maxValue; // t is the maximal time budget we can have at
								// node n
			res.add(n);
			int limit = (!all && t == L) ? Math.max(0, t - 2) : 0;
			// for some unknown reason, the algorithm stop at t-1..
			List<Integer> temp = new List<Integer>();
			for (int k = t - 1; k >= limit; k--) {
				int next = nextNode[n.id][k];

				if (next != -1 && temp.contains(next) == 0) {
					temp.add(next);
					if (res.contains(graph.nodes[next]) == 0
							&& !toSet.containsKey(graph.nodes[next])) {
						double w = 0;
						Browse<Edge> l = graph.going[n.id].browse();
						while (l.hasNext()) {
							Edge e = l.next();
							if (e.end.id == next) {
								w = e.minTT;
							}
						}
						int val = k + 1 - (int) (w);
						if (toSet.containsKey(graph.nodes[next])) {
							val = Math.max(toSet.get(graph.nodes[next]), val);
						}
						toSet.put(graph.nodes[next], val);
					}
				}
			}
		}
		return res;
	}

	public List<Node> possibleNodes(Node origin, int startTime, int endTime) {
		// compute the possible nodes from next node
		// ListSorted<Node> toSet = new ListSorted<Node>(origin, -nbDisc);
		HashMap<Node, Integer> toSet = new HashMap<Node, Integer>();
		toSet.put(origin, startTime);
		List<Node> res = new List<Node>();
		while (!toSet.isEmpty()) {
			Node maxKey = null;
			int maxValue = 0;
			for (Map.Entry<Node, Integer> entry : toSet.entrySet()) {
				int value = entry.getValue();
				if (value > maxValue) {
					maxKey = entry.getKey();
					maxValue = value;
				}
			}
			toSet.remove(maxKey);
			Node n = maxKey;
			int t = maxValue; // t is the maximal time budget we can have at
								// node n
			res.add(n);
			int limit = t == startTime ? Math.max(endTime, t - 2) : endTime;
			// for some unknown reason, the algorithm stop at t-1..
			for (int k = t - 1; k >= limit; k--) {
				int next = nextNode[n.id][k];

				if (next != -1) {
					if (res.contains(graph.nodes[next]) == 0
							&& !toSet.containsKey(graph.nodes[next])) {
						double w = 0;
						Browse<Edge> l = graph.going[n.id].browse();
						while (l.hasNext()) {
							Edge e = l.next();
							if (e.end.id == next) {
								w = e.minTT;
							}
						}
						int val = k + 1 - (int) (w);
						if (toSet.containsKey(graph.nodes[next])) {
							val = Math.max(toSet.get(graph.nodes[next]), val);
						}
						toSet.put(graph.nodes[next], val);
					}
				}
			}
		}
		return res;
	}

	public void newSOTA(double budget, List<Node> origins, Node destination,
			Graph g) {
		// return the SOTA solution of a discretized graph with no edges with
		// mintT smaller than delta
		this.initialize(budget, origins, destination, g);
		if (!this.origins.isEmpty()) {
			this.order();
			this.computeUpdate();
			this.result();
		} else {
			p.w("It is not possible to reach the destination within " + budget);
		}
	}

	public void newSOTA(double budget, Node origin, Node destination, Graph g) {
		List<Node> origins = new List<Node>();
		origins.add(origin);
		this.initialize(budget, origins, destination, g);
		if (this.origins.isEmpty()) {
			p.w("it is not possible to reach destination");
		} else {
			if (tauMax[this.origins.get(0).id] >= L || this.updateForAllT) {
				// else we already know the solution
				long timer1 = System.currentTimeMillis();
				this.order();
				p.w("order computed in "
						+ (System.currentTimeMillis() - timer1) / 1000 + "s");
				p.w(optOrder.size() + " nodes to update");
				this.computeUpdate();
			}
			this.result();
		}
	}

	public void newSOTA(double budget, int origin, int destination, Graph g) {
		List<Node> origins = new List<Node>();
		origins.add(g.nodes[origin]);
		this.initialize(budget, origins, g.nodes[destination], g);
		if (this.origins.isEmpty()) {
			int avTT = (int) other.utils.minTT(new withAverageTT().dijkstra(g,
					origin, destination, false));
			p.w("it is not possible to reach destination\naverageTT is " + avTT);
			List<Node> list = new List<Node>();
			list.add(g.nodes[origin]);
			list.add(g.nodes[destination]);
			plot.plotGraph(g, null, new List<Node>(), possibleNodes, null,
					list, true,
					null,// this.nbUpdatePerNode,
					"It is not possible to reach #" + destination + " from #"
							+ origin + " within " + budget, 0, 0, 0, 0, 0, 0,
					0, 0, 0, equivalent, budget);
		} else {
			if (tauMax[this.origins.get(0).id] >= L || this.updateForAllT) {

				// else we already know the solution
				long timer1 = System.currentTimeMillis();
				this.order();
				p.w("order computed in "
						+ (System.currentTimeMillis() - timer1) / 1000 + "s");
				p.w(optOrder.size() + " nodes to update");
				this.computeUpdate();
			} else {
			}
			this.result();
		}
	}

	private List<Pair<Node, Integer>> nodesOrderedByMinTTtoDestination() {
		// return the node by increasing distance from destination
		assert minTTfromOr[destination.id] != -1;
		// initialisation
		List<Node> initialized = new List<Node>();
		initialized.add(destination);
		List<Pair<Node, Integer>> set = new List<Pair<Node, Integer>>();
		set.add(new Pair<Node, Integer>(destination,
				(int) (L - minTTfromOr[destination.id])));
		double[] dist = new double[networkSize];
		int[] precursor = new int[networkSize];

		for (int i = 0; i < networkSize; i++) {
			dist[i] = -1;
			precursor[i] = -1;
		}
		dist[destination.id] = 0;
		// *****
		Node closest = destination;
		int idClosest = closest.id;
		try {
			while (!initialized.isEmpty()) {
				// uptdate of the distances
				Browse<Edge> it = graph.coming[idClosest].browse();
				while (it.hasNext()) {
					Edge e = it.next();
					if (!landmarks || e.landmark) {
						Node dir = e.start;
						if (dist[dir.id] == -1
								&& !prune(dir,
										(int) (L - minTTfromOr[idClosest]))) {
							dist[dir.id] = dist[idClosest] + metric(e);
							initialized.add(dir);
							precursor[dir.id] = idClosest;
						} else if (dist[dir.id] > dist[idClosest] + metric(e)) {
							dist[dir.id] = dist[idClosest] + metric(e);
							precursor[dir.id] = idClosest;
						}
					}
				}

				set.add(new Pair<Node, Integer>(graph.nodes[idClosest], Math
						.min((int) (L - minTTfromOr[idClosest]),
								tauMax[idClosest])));
				initialized.remove(graph.nodes[idClosest]);
				if (initialized.isEmpty()) {
					throw new FoundException();
				}
				// finding the nearest
				Browse<Node> ini = initialized.browse();
				closest = ini.next();
				idClosest = closest.id;
				double min = dist[idClosest];
				while (ini.hasNext()) {
					Node n = ini.next();
					if (dist[n.id] < min && dist[n.id] != -1) {
						min = dist[n.id];
						closest = n;
					}
				}
				idClosest = closest.id;
			}
		} catch (FoundException e) {
		}
		set.reverse();
		return set;
	}

	public double metric(Edge e) {
		return main.metricForLoops == "average" ? e.average() : e.minTT;
	}

	public SOTA() {
	}

	public void checkUseOnlyOneSon(int node) {
		int next = -1;
		boolean justOne = true;
		for (int j = 0; j < L; j++) {
			if (next == -1) {
				next = nextNode[node][j];
			} else if (nextNode[node][j] != next && nextNode[node][j] != -1) {
				justOne = false;
			}
		}
		if (justOne) {
			this.updateUseOnlyOneSon++;
		}
	}

};