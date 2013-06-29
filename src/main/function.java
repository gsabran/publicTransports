package main;

import javax.swing.JFrame;
import other.FoundException;
import other.p;
import plot.*;
import structure.*;

public abstract class function {
	// different dijkstra algorithms

	public abstract double metric(Edge e);
	
	public Node getNodeDistant(Node origin, double distance, Graph g){

		// return the min travel time to destination (by doing a dijkstra
		// algorithm)
		int nNode = g.nodes.length;

		// initialisation
		List<Node> initialized = new List<Node>();
		initialized.add(origin);
		List<Node> set = new List<Node>();
		set.add(origin);
		double[] dist = new double[nNode];
		int[] precursor = new int[nNode];

		for (int i = 0; i < nNode; i++) {
			dist[i] = -1;
			precursor[i] = -1;
		}
		dist[origin.id] = 0;
		// *****
		Node closest = origin;
		Node res = null;
		int idClosest = closest.id;
		boolean ended = false;
		try {
			while (!ended) {
				// uptdate of the distances
				Browse<Edge> it =g.going[idClosest].browse();
				while(it.hasNext()){
					Edge e = it.next();
					Node dir = e.end;
					if (dist[dir.id] == -1) {
						dist[dir.id] = dist[idClosest] + metric(e);
						initialized.add(dir);
						precursor[dir.id] = idClosest;
					} else if (dist[dir.id] > dist[idClosest] + metric(e)) {
						dist[dir.id] = dist[idClosest] + metric(e);
						precursor[dir.id] = idClosest;
					}
				}
				if(dist[idClosest]>=distance){
					res = g.nodes[idClosest];
					ended=true;
				}
				set.add(g.nodes[idClosest]);
				if(initialized.size()==1){
					res=initialized.get(0);
				}
				initialized.remove(g.nodes[idClosest]);
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
						closest=n;
					}
				}
				idClosest = closest.id;
			}
		} catch (FoundException e) {
		}
		return res;

	}
	
	
	public double[] minTTtoDestination(Graph g, Node dest) {
		// return the min travel time to destination (by doing a dijkstra
		// algorithm)
		int nNode = g.nodes.length;

		// initialisation
		List<Node> initialized = new List<Node>();
		initialized.add(dest);
		List<Node> set = new List<Node>();
		set.add(dest);
		double[] dist = new double[nNode];
		int[] precursor = new int[nNode];

		for (int i = 0; i < nNode; i++) {
			dist[i] = -1;
			precursor[i] = -1;
		}
		dist[dest.id] = 0;
		// *****
		Node closest = dest;
		int idClosest = closest.id;
		try {
			while (!initialized.isEmpty()) {
				// uptdate of the distances
				Browse<Edge> it =g.coming[idClosest].browse();
				while(it.hasNext()){
					Edge e = it.next();
					Node dir = e.start;
					if (dist[dir.id] == -1) {
						dist[dir.id] = dist[idClosest] + metric(e);
						initialized.add(dir);
						precursor[dir.id] = idClosest;
					} else if (dist[dir.id] > dist[idClosest] + metric(e)) {
						dist[dir.id] = dist[idClosest] + metric(e);
						precursor[dir.id] = idClosest;
					}
				}

				set.add(g.nodes[idClosest]);
				initialized.remove(g.nodes[idClosest]);
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
						closest=n;
					}
				}
				idClosest = closest.id;
			}
		} catch (FoundException e) {
		}
		return dist;

	}

	public double[] minTTfromOrigin(Graph g, Node origin) {
		

		// return the min travel time to destination (by doing a dijkstra
		// algorithm)
		int nNode = g.nodes.length;

		// initialisation
		List<Node> initialized = new List<Node>();
		initialized.add(origin);
		List<Node> set = new List<Node>();
		set.add(origin);
		double[] dist = new double[nNode];
		int[] precursor = new int[nNode];

		for (int i = 0; i < nNode; i++) {
			dist[i] = -1;
			precursor[i] = -1;
		}
		dist[origin.id] = 0;
		// *****
		Node closest = origin;
		int idClosest = closest.id;
		try {
			while (!initialized.isEmpty()) {
				// uptdate of the distances
				Browse<Edge> it =g.going[idClosest].browse();
				while(it.hasNext()){
					Edge e = it.next();
					Node dir = e.end;
					if (dist[dir.id] == -1) {
						dist[dir.id] = dist[idClosest] + metric(e);
						initialized.add(dir);
						precursor[dir.id] = idClosest;
					} else if (dist[dir.id] > dist[idClosest] + metric(e)) {
						dist[dir.id] = dist[idClosest] + metric(e);
						precursor[dir.id] = idClosest;
					}
				}

				set.add(g.nodes[idClosest]);
				initialized.remove(g.nodes[idClosest]);
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
						closest=n;
					}
				}
				idClosest = closest.id;
			}
		} catch (FoundException e) {
		}
		return dist;
	}

	public  double distance(Node a, Node b) {
		// return the euclidian distance
		return (double) Math.floor(Math.sqrt((a.lat - b.lat) * (a.lat - b.lat)
				+ (a.lon - b.lon) * (a.lon - b.lon)));
	}
	
	public  List<Edge> dijkstra(Graph g, int s, int t, boolean print) {
		// Return the shortest path from s to t
		// s and t are reduced if needed
		s = s % g.nodes.length;
		t = t % g.nodes.length;
		if (print) {
			System.out.println("Dijkstra started from node #" + s
					+ " to node #" + t + "...");
		}
		long time1 = System.currentTimeMillis();
		List<Node> l = new List<Node>();
		l.add(g.nodes[s]);
		int nNode = g.nodes.length;

		// initialization
		List<Node> initialized = new List<Node>();
		initialized.add(g.nodes[s]);
		List<Node> set = new List<Node>();
		set.add(g.nodes[s]);
		double[] dist = new double[nNode];
		int[] precursor = new int[nNode];

		for (int i = 0; i < nNode; i++) {
			dist[i] = -1;
			precursor[i] = -1;
		}
		dist[s] = 0;
		// *****
		int idMin = s;
		boolean ended = false;
		try {
			while (!ended) {
				// update of the distances
				Browse<Edge> edges = g.going[idMin].browse();
				while (edges.hasNext()) {
					Edge e = edges.next();
					int dir = e.end.id;
					if (dist[dir] == -1) {
						dist[dir] = dist[idMin] + metric(e);

						initialized.add(g.nodes[dir]);
						precursor[dir] = idMin;
					} else if (dist[dir] > dist[idMin] + metric(e)) {
						dist[dir] = dist[idMin] + metric(e);
						precursor[dir] = idMin;
					}
				}
				initialized.remove(g.nodes[idMin]);
				if (initialized.isEmpty()) {
					throw new FoundException();
				}
				set.add(g.nodes[idMin]);
				// finding the nearest
				Browse<Node> ini = initialized.browse();
				idMin = initialized.get(0).id;
				double min = dist[idMin];
				while (ini.hasNext()) {
					Node n = ini.next();
					if (dist[n.id] < min && dist[n.id] != -1) {
						min = dist[n.id];
						idMin = n.id;
					}
				}
				l.add(g.nodes[idMin]);
				ended = idMin == t;
			}
		} catch (FoundException e) {
			if (print) {
				System.out.println("aucun chemin possible");
				if (g.going[s] == null) {
					System.out.println("l'origine " + s + " est un cul de sac");
				} else if (g.coming[t] == null) {
					System.out.println("la destination " + t
							+ " est un cul de sac");
				} else {
					System.out
							.println("the origin and destination are not connected");
					/*
					 * plot.plotGraph(g, null, null, null, lTemp, true,
					 * "no path found", 0, 0, 0, 0, 0);
					 */
				}
				return null;
			}
		}
		// rebuilding the path
		int length = 0;
		List<Edge> res = new List<Edge>();
		int here = t;
		int prec = precursor[here];
		while (here != s && prec != -1) {
			Browse<Edge> temp = g.going[prec].browse();
			try {
				while (temp.hasNext()) {
					Edge e = temp.next();
					if (e.end.id == here) {
						res.add(e);
						length += metric(e);
						throw new FoundException();
					}
				}
			} catch (FoundException e) {
			}
			here = prec;
			prec = precursor[prec];
		}
		// printing the result
		Browse<Edge> temp = res.browse();
		while (temp.hasNext()) {
			Edge e = temp.next();
			if (print) {
				System.out.print(e.start.id + "->");
			}
		}
		// ploting the result
		// plot.plotGraph(g, res, l,null, null, true,
		// "Dijkstra from node#"+s+" to node#"+t,0,0,0,0,0);
		long time2 = System.currentTimeMillis();
		if (print) {
			System.out.println("Dijkstra computed in " + (time2 - time1)
					+ " ms, min travel time = " + length);
		}
		return res;
	}

	public  List<Edge> bidirectionalDijkstra(Graph g, int s, int t) {
		// Return the shortest path from s to t
		// s and t are reduced if needed
		System.out.println("Bidirectional Dijkstra started...");
		long time1 = System.currentTimeMillis();
		List<Node> l = new List<Node>(g.nodes[s]);
		int nNode = g.nodes.length;

		// initialization
		List<Integer> initializedForward = new List(s);
		List<Integer> initializedBackward = new List(t);
		List<Integer> setForward = new List(s);
		List<Integer> setBackward = new List(t);
		double[] distForward = new double[nNode];
		int[] precursor = new int[nNode];
		double[] distBackward = new double[nNode];
		int[] next = new int[nNode];

		for (int i = 0; i < nNode; i++) {
			distForward[i] = -1;
			precursor[i] = -1;
			distBackward[i] = -1;
			next[i] = -1;
		}
		distForward[s] = 0;
		distBackward[t] = 0;
		// *****
		int idMinForward = s;
		int idMinBackward = t;
		int connexion = -1;
		boolean ended = false;

		while (!ended) {
			// forward
			// update of the distances
			Browse<Edge> edges = g.going[idMinForward].browse();
			while (edges.hasNext()) {
				Edge e = edges.next();
				int dir = e.end.id;
				if (distForward[dir] == -1) {
					distForward[dir] = distForward[idMinForward] + metric(e);
					initializedForward.add(dir);
					precursor[dir] = idMinForward;
				} else if (distForward[dir] > distForward[idMinForward]
						+ metric(e)) {
					distForward[dir] = distForward[idMinForward] + metric(e);
					precursor[dir] = idMinForward;
				}
			}
			initializedForward.remove(idMinForward);
			setForward.add(idMinForward);
			// finding the nearest
			Browse<Integer> ini = initializedForward.browse();
			idMinForward = ini.next();
			double min = distForward[idMinForward];
			while (ini.hasNext()) {
				int i=ini.next();
				if (distForward[i] < min && distForward[i] != -1) {
					min = distForward[i];
					idMinForward = i;
				}
			}
			l.add(g.nodes[idMinForward]);

			// backward
			// uptdate of the distances
			edges = g.coming[idMinBackward].browse();
			while (edges.hasNext()) {
				Edge e = edges.next();
				int dir = e.start.id;
				if (distBackward[dir] == -1) {
					distBackward[dir] = distBackward[idMinBackward]
							+ metric(e);
					initializedBackward.add(dir);
					next[dir] = idMinBackward;
				} else if (distBackward[dir] > distBackward[idMinBackward]
						+ metric(e)) {
					distBackward[dir] = distBackward[idMinBackward]
							+ metric(e);
					next[dir] = idMinBackward;
				}
			}
			initializedBackward.remove(idMinBackward);
			setBackward.add(idMinBackward);
			// finding the nearest
			ini = initializedBackward.browse();
			idMinBackward = ini.next();
			min = distBackward[idMinBackward];
			while (ini.hasNext()) {
				int i = ini.next();
				if (distBackward[i] < min
						&& distBackward[i] != -1) {
					min = distBackward[i];
					idMinBackward = i;
				}
			}
			l.add(g.nodes[idMinBackward]);
			if (setForward.contains(idMinBackward)==0) {
				connexion = idMinBackward;
				ended = true;
			}
			if (setBackward.contains(idMinForward)==0) {
				connexion = idMinForward;
				ended = true;
			}
		}

		List<Edge> res = null;
		int here = connexion;
		int prec = precursor[here];
		while (prec != s) {
			Browse<Edge> temp = g.going[prec].browse();
			while (temp.hasNext()) {
				Edge e = temp.next();
				if (e.end.id == here) {
					res.add(e);
				}
			}
			here = prec;
			prec = precursor[prec];
		}
		here = connexion;
		prec = next[here];
		while (prec != t) {
			Browse<Edge> temp = g.coming[prec].browse();
			while (temp.hasNext()) {
				Edge e = temp.next();
				if (e.start.id == here) {
					res.add(e);
				}
			}
			
			here = prec;
			prec = next[prec];
		}
		plot.plotGraph(g, res, l, null, null, true,
				"Bidirectionnal Dijkstra");
		long time2 = System.currentTimeMillis();
		System.out.println("Bidirectional Dijkstra ended in " + (time2 - time1)
				+ " ms");
		return res;
	}
}
