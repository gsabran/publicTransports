package structure;

import main.*;
import other.*;
import plot.plot;

public class Graph {
	// principal structure. nodes is the array of nodes, going[i] is the list of
	// edge going from node#i...
	// the edges contain the distribution data -> see Edge
	// size contains the max/min longitude/latitude for the drawing
	public Node[] nodes;
	public List<Edge>[] going;
	public List<Edge>[] coming;
	public double[] size;

	public void reverse() {
		// reverse the edges
		for (int i = 0; i < nodes.length; i++) {
			Browse<Edge> g = going[i].browse();
			Browse<Edge> c = coming[i].browse();
			going[i] = new List<Edge>();
			coming[i] = new List<Edge>();

			while (c.hasNext()) {
				Edge e = c.next();
				e.reverse();
				going[i].add(e);
			}

			while (g.hasNext()) {
				Edge e = g.next();
				coming[i].add(e);
			}
		}
	}

	public boolean check() {
		// check if the graph is consistent (every edge appears only one time
		// and is represented once in going and coming)
		// may take some time
		boolean res = true;
		for (int i = 0; i < nodes.length; i++) {
			assert nodes[i].id == i : "Node " + nodes[i] + " has a wrong id: "
					+ i;
			Node n = nodes[i];
			for (int j = i + 1; j < nodes.length; j++) {
				if (nodes[j] == n) {
					res = false;
					System.out.println("nodes " + i + " and " + j
							+ " are equals");
				}
			}
		}
		for (int i = 0; i < nodes.length; i++) {
			Browse<Edge> l = going[i].browse();
			while (l.hasNext()) {
				Edge e = l.next();
				int g = 0;
				int c = 0;
				for (int j = 0; j < nodes.length; j++) {
					Browse<Edge> l2 = going[j].browse();
					while (l2.hasNext()) {
						Edge e2 = l2.next();
						if (e == e2) {
							g++;
						}
					}
					Browse<Edge> l3 = coming[j].browse();
					while (l3.hasNext()) {
						Edge e3 = l3.next();
						if (e == e3) {
							c++;
						}
					}
				}
				if (g != 1) {
					res = false;
					System.out.println("edge " + e + "appears " + g
							+ " times in going");
				}
				if (c != 1) {
					res = false;
					System.out.println("edge " + e + "appears " + c
							+ " times in coming");
				}
			}

			l = coming[i].browse();
			while (l.hasNext()) {
				Edge e = l.next();
				int g = 0;
				int c = 0;
				for (int j = 0; j < nodes.length; j++) {
					Browse<Edge> l2 = going[j].browse();
					while (l2.hasNext()) {
						Edge e2 = l2.next();
						if (e == e2) {
							g++;
						}
					}
					Browse<Edge> l3 = coming[j].browse();
					while (l3.hasNext()) {
						Edge e3 = l3.next();
						if (e == e3) {
							c++;
						}
					}
				}
				if (g != 1) {
					res = false;
					System.out.println("edge " + e + "appears " + g
							+ " times in going");
				}
				if (c != 1) {
					res = false;
					System.out.println("edge " + e + "appears " + c
							+ " times in coming");
				}
			}
		}
		return res;
	}

	public Edge getEdgeByExtremities(int s, int d) {
		// return the edge from s to d
		Edge res = null;
		Browse<Edge> b = this.going[s].browse();
		while (b.hasNext()) {
			Edge e = b.next();
			if (e.end.id == d) {
				res = e;
			}
		}
		return res;
	}

	public int nbEdge() {
		// get the number of edges of the graph
		int res = 0;
		for (int i = 0; i < this.nodes.length; i++) {
			res += going[i].size();
		}
		return res;
	}

	public boolean equals(Graph g) {
		// test if two graph are equals
		boolean res = g.nodes.length == nodes.length
				&& g.going.length == going.length
				&& g.coming.length == coming.length;
		if (res) {
			try {
				for (int i = 0; i < g.nodes.length; i++) {
					if (!g.nodes[i].equals(nodes[i])) {
						throw new TextException("node " + i + " different");
					}
					if (going[i].size() != g.going[i].size()) {
						throw new TextException("going " + i
								+ " of different size : " + going[i].size()
								+ " vs " + g.going[i].size());
					}

					Browse<Edge> e1 = g.going[i].browse();
					Browse<Edge> e2 = going[i].browse();
					while (e1.hasNext()) {
						Edge a = e1.next();
						Edge b = e2.next();
						res = res && going[i].contains(a) == 1
								&& g.going[i].contains(b) == 1;
					}

					if (!res) {
						throw new TextException("going " + i + " different");
					}
					if (coming[i].size() != g.coming[i].size()) {
						throw new TextException("coming " + i
								+ " of different size");
					}

					e1 = g.coming[i].browse();
					e2 = coming[i].browse();
					while (e1.hasNext()) {
						Edge a = e1.next();
						Edge b = e2.next();
						res = res && coming[i].contains(a) == 1
								&& g.coming[i].contains(b) == 1;
					}
					if (!res) {
						throw new TextException("coming " + i + " different");
					}
				}
			} catch (TextException e) {
				System.out.println(e.s);
				res = false;
			}
		}
		return res;
	}

	public Graph cut(double[] border) {
		// return the sub graph included in the intersection with x0<x<x1 &&
		// y0<y<y1 where border=(x0,x1,y0,y1)
		Node[] n = new Node[nodes.length];
		List<Edge>[] g = new List[nodes.length];
		List<Edge>[] c = new List[nodes.length];

		for (int i = 0; i < nodes.length; i++) {
			n[i] = nodes[i];
			g[i] = going[i];
			c[i] = coming[i];
		}
		int nNode = nodes.length;
		int[] transition = new int[nNode];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].lat < border[0] || nodes[i].lat > border[1]
					|| nodes[i].lon < border[2] || nodes[i].lon > border[3]) {
				nNode--;
				n[i] = null;
				Browse<Edge> l = going[i].browse();
				while (l.hasNext()) {
					Edge e = l.next();
					c[e.end.id].remove(e);
				}
				l = coming[i].browse();
				while (l.hasNext()) {
					Edge e = l.next();
					g[e.start.id].remove(e);
				}
			}
		}
		List<Edge>[] gTemp = new List[nNode];
		List<Edge>[] cTemp = new List[nNode];
		Node[] nTemp = new Node[nNode];
		int[] reverse = new int[nNode];
		int count = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (n[i] != null) {
				transition[i] = count;
				reverse[count] = i;
				nTemp[count] = n[i];
				nTemp[count].id = count;
				count++;
			}
		}
		for (int i = 0; i < nNode; i++) {
			gTemp[i] = g[reverse[i]];
			cTemp[i] = c[reverse[i]];
		}
		double[] s = new double[4];
		s[0] = Math.max(size[0], border[0]);
		s[1] = Math.min(size[1], border[1]);
		s[2] = Math.max(size[2], border[2]);
		s[3] = Math.min(size[3], border[3]);
		System.out.println("Graph cut\n");
		return new Graph(nTemp, gTemp, cTemp, s);
	}

	public Graph cut(double prop) {
		double[] border = new double[4];
		border[0] = size[0];
		border[1] = size[0] + prop * (size[1] - size[0]);
		border[2] = size[2];
		border[3] = size[2] + prop * (size[3] - size[2]);
		return this.cut(border);
	}

	public Graph cut(float[] prop) {
		double[] border = new double[4];
		border[0] = size[0] + prop[0] * (size[1] - size[0]);
		border[1] = size[0] + prop[1] * (size[1] - size[0]);
		border[2] = size[2] + prop[2] * (size[3] - size[2]);
		border[3] = size[2] + prop[3] * (size[3] - size[2]);
		return this.cut(border);
	}

	public void remove(Node n) {
		// remove the edges from and to n, but don't remove n
		Browse<Edge> b = going[n.id].browse();
		while (b.hasNext()) {
			Edge e = b.next();
			coming[e.end.id].remove(e);
		}
		b = coming[n.id].browse();
		while (b.hasNext()) {
			Edge e = b.next();
			going[e.start.id].remove(e);
		}
		going[n.id] = new List<Edge>();
		coming[n.id] = new List<Edge>();
	}

	public void remove(Edge e) {
		// remove the edge e
		coming[e.end.id].remove(e);
		going[e.start.id].remove(e);
	}

	public String toString() {
		String s = "";
		s += "min lat : " + size[0] + " max lat : " + size[1] + " min lon : "
				+ size[2] + " max lon : " + size[3];
		for (int i = 0; i < nodes.length; i++) {
			s += going[i].isEmpty() ? "" : "\n" + going[i].get(0).toString();
		}
		return s;
	}

	public void toText(String networkName) {
		// write the graph in a text file so that it will be load more quickly
		// next time
		// constructions of the graph
		String fileNameEdge = "adaptedToStructure/" + networkName + "/Edges"
				+ networkName + ".csv";
		String fileNameNode = "adaptedToStructure/" + networkName + "/Nodes"
				+ networkName + ".csv";
		Writer.eraseFile(fileNameNode);
		Writer.eraseFile(fileNameEdge);
		int nNode = this.nodes.length;
		for (int i = 0; i < nNode; i++) {
			nodes[i].toText(fileNameNode);
			Browse<Edge> edge = this.going[i].browse();
			while (edge.hasNext()) {
				edge.next().toText(fileNameEdge);
			}
		}
	}
	
	public static Graph fromText(String networkName){
		double[][] n = other.GetDonnees.getTab("data/adaptedToStructure/" + networkName + "/Nodes"
				+ networkName + ".csv");
		Node[] nodes = new Node[n.length];
		for(int i=0; i<n.length; i++){
			nodes[i] = Node.fromText(n[i]);
		}
		double[][] e = other.GetDonnees.getTab("data/adaptedToStructure/" + networkName + "/Edges"
				+ networkName + ".csv");
		Edge[] edges = new Edge[e.length];
		for(int i=0; i<e.length; i++){
			edges[i] = Edge.fromText(e[i],nodes);
		}
		Graph g =new Graph(nodes,edges);
		g.computeDistribution();
		return g;
	}

	public Graph(Node[] n, List<Edge>[] g, List<Edge>[] c, double[] s) {
		nodes = n;
		going = g;
		coming = c;
		size = s;
	}

	public Graph(Node[] n, Edge[] e) {
		nodes = n;
		List<Edge>[] g = new List[n.length];
		List<Edge>[] c = new List[n.length];
		for (int i = 0; i < n.length; i++) {
			g[i] = new List<Edge>();
			c[i] = new List<Edge>();
		}
		double[] s = new double[4];
		s[0] = n[0].lat;
		s[1] = n[1].lat;
		s[2] = n[2].lon;
		s[3] = n[3].lon;
		for (int i = 0; i < n.length; i++) {
			s[0] = Math.min(s[0], n[i].lat);
			s[1] = Math.max(s[1], n[i].lat);
			s[2] = Math.min(s[2], n[i].lon);
			s[3] = Math.max(s[3], n[i].lon);
		}
		for (int i = 0; i < e.length; i++) {
		g[e[i].start.id].add(e[i]);
			c[e[i].end.id].add(e[i]);
		}
		going = g;
		coming = c;
		size = s;
	}

	public Graph() {
		// build the graph from the data transformed in python
		// load the data
		double[][] n = GetDonnees.getTab("data/source/" + main.networkName
				+ "/transformed/stops.txt");
		Node[] nodes = new Node[n.length];
		for (int i = 0; i < n.length; i++) {
			nodes[i] = new Node((int) n[i][0], n[i][1], n[i][2]);
			nodes[i].id = i;
		}
		BusLine[] bus = GetDonnees.getBusLines("data/source/"
				+ main.networkName + "/transformed/bus_lignes.txt", nodes);

		// merges the same bus lines (in the data, a bus line is represented
		// once for every travel)
		// -> go from 27458 to 358 lines
		BusLine[] temp = new BusLine[bus.length];
		int[] count = new int[bus.length];// used to get the period
		int[] firstTravel = new int[bus.length];
		int[] lastTravel = new int[bus.length];
		int numberOfLines = 0;
		for (int i = 0; i < bus.length - 1; i++) {
			try {
				for (int j = 0; j < numberOfLines; j++) {
					if (bus[i].followSamePath(temp[j])) {
						if (utils.timeToInt(bus[i].stopTime.get(0)) != utils
								.timeToInt(temp[j].stopTime.get(0))) {
							count[j]++;
							int time = utils.timeToInt(bus[i].stopTime.get(0));
							firstTravel[j] = Math.min(firstTravel[j], time);
							lastTravel[j] = Math.max(lastTravel[j], time);
						}
						throw new FoundException();
					}
				}
				temp[numberOfLines] = bus[i];
				int time = utils.timeToInt(bus[i].stopTime.get(0));
				firstTravel[numberOfLines] = time;
				lastTravel[numberOfLines] = time;
				count[numberOfLines] = 1;
				numberOfLines++;
			} catch (FoundException e) {

			}
		}
		bus = new BusLine[numberOfLines];
		for (int i = 0; i < numberOfLines; i++) {
			bus[i] = temp[i];
			bus[i].period = count[i] == 1 ? 86400
					: (lastTravel[i] - firstTravel[i]) / (count[i] - 1);
			if (bus[i].period > main.maxPeriod) {
				bus[i] = null;
			}
			assert (bus[i] == null ||(bus[i].period != 0 && bus[i].period <= 86400 && bus[i].period<=main.maxPeriod));
		}

		int additionalStations = 0;
		for (int i = 0; i < bus.length - 1; i++) {
			additionalStations += bus[i] != null ? bus[i].stops.size() : 0;
		}
		// find the border of the graph (used for the plot)
		size = new double[4];
		size[0] = nodes[0].lat;
		size[1] = nodes[0].lat;
		size[2] = nodes[0].lon;
		size[3] = nodes[0].lon;
		this.nodes = new Node[nodes.length + additionalStations];
		for (int i = 0; i < nodes.length; i++) {
			this.nodes[i] = nodes[i];
			size[0] = Math.min(size[0], nodes[i].lat);
			size[1] = Math.max(size[1], nodes[i].lat);
			size[2] = Math.min(size[2], nodes[i].lon);
			size[3] = Math.max(size[3], nodes[i].lon);
		}

		// convert the bus lines into edges
		this.going = new List[nodes.length + additionalStations];
		this.coming = new List[nodes.length + additionalStations];
		for (int i = 0; i < nodes.length + additionalStations; i++) {
			going[i] = new List<Edge>();
			coming[i] = new List<Edge>();
		}
		int nextNodeIndex = nodes.length;

		for (int i = 0; i < bus.length - 1; i++) {
			if (bus[i] != null) {
				bus[i].position.reverse();
				Browse<Integer> br = bus[i].position.browse();
				// checked if the stops are well ordered
				boolean ordered = true;
				int current = bus[i].position.get(0);
				while (br.hasNext()) {
					int a = br.next();
					ordered = ordered && a >= current;
					current = a;
				}
				assert (ordered);
				bus[i].stops.reverse();
				bus[i].stopTime.reverse();
				// create the edges
				Browse<Node> b = bus[i].stops.browse();
				Browse<String> b2 = bus[i].stopTime.browse();
				Node startStation = b.next();
				Node startLine = startStation.clone();
				this.nodes[nextNodeIndex] = startLine;
				startLine.id = nextNodeIndex;
				startLine.lineID = bus[i].idPrimary;
				int startTime = other.utils.timeToInt(b2.next());
				float r = (float) Math.random();
				float sigma = (float) ( 0.5 * r) * bus[i].period / 2;
				float var = sigma*sigma;
				Edge e = new Edge(startStation, startLine, Math.max((float) (bus[i].period / 2-1.5*sigma),0),
						var, bus[i].period / 2);
				assert(e.minTT<3000);
				going[startStation.id].add(e);
				coming[nextNodeIndex].add(e);
				nextNodeIndex++;

				while (b.hasNext()) {
					Node station = b.next();
					Node line = station.clone();
					this.nodes[nextNodeIndex] = line;
					line.id = nextNodeIndex;
					line.lineID = bus[i].idPrimary;
					int time2 = other.utils.timeToInt(b2.next());

					r = (float) Math.random();
					sigma = (float) (0.5 * r) * bus[i].period / 2;
					 var = sigma*sigma;
					Edge e3 = new Edge(station, line, Math.max((float) (bus[i].period / 2-1.5*sigma),0),
							var, bus[i].period / 2);
					assert(e3.minTT<3000);
					going[station.id].add(e3);
					coming[line.id].add(e3);

					sigma = (float) (0.25 * r) * (time2 - startTime);
					 var = sigma*sigma;
					Edge e1 = new Edge(startLine, line, (float) (time2 - startTime-1.5*sigma),
							var, time2 - startTime);
					assert(e1.minTT<3000) : i;
					Edge e2 = new Edge(line, station, 0, 0,0);
					assert(e2.minTT<3000);
					going[startLine.id].add(e1);
					coming[line.id].add(e1);
					going[line.id].add(e2);
					coming[station.id].add(e2);

					startStation = station;
					startLine = line;
					startTime = time2;
					nextNodeIndex++;
				}
			}
		}
		this.discretize();
		// graph.check();
		p.w("computing the distributions");
		this.computeDistribution();
		/*
		 * p.w("checking the graph..."); assert(this.check());
		 * p.w("everything's great!");
		 */
	}
	
	private class computeDistribution extends parallelComputation {
		public void prepare() {
			this.nb_threads=main.nbThreads;
		}
		public void prepare2(){
		}
		public void pruneIniFromOrigin(){}

		public void map(int numThread) {
			// the function called by each worker in the map phase
			int d = numThread;
			while (d < nodes.length) {
				Browse<Edge> b = going[d].browse();
				while (b.hasNext()) {
					Edge e = b.next();
					if(e.minTT==0 && e.var==0){
						e.distribution=new double[1];
						e.distribution[0]=1;
						e.realMaxTT=0;
						e.realMinTT=0;
					}else{
					double[] a = e.getDistribution();
					if (e.realMinTT > 0) {
						e.minTT = e.realMinTT;
						assert e.minTT<3000;
					}}
				}
				d = nextToDo;
				nextToDo++;
			}
		}

		public void reduce(String rep) {
		}

		public boolean prune(Node n, int t) {
			return false;
		}

		public void result() {

		}

		public computeDistribution() {
			computation();
		}
	}
	
	public void aproximizeEdge() {
		// fusion the nodes separated by a unsignificant edge
		int idDeleted = -1;
		int idStart = -1;

		try {
			for (int i = 0; i < nodes.length; i++) {

				Browse<Edge> l = going[i].browse();
				while (l.hasNext()) {
					Edge e = l.next();
					if (e.minTT < main.deltaT) {

						idDeleted = e.end.id;
						idStart = e.start.id;
						Browse<Edge> g = going[idDeleted].browse();

						while (g.hasNext()) {
							Edge ed = g.next();
							if (ed.end.id != idStart) {

								ed.start = e.start;
								going[idStart].add(ed);
							} else {
								coming[idStart].remove(ed);
							}
						}

						Browse<Edge> c = coming[idDeleted].browse();

						while (c.hasNext()) {
							Edge temp = c.next();
							if (temp.start.id != idStart) {
								temp.end = e.start;
								coming[idStart].add(temp);
							}
						}

						going[idStart].remove(e);
						coming[idDeleted].remove(e);

						// test
						for (int k = 0; k < nodes.length; k++) {
							Browse<Edge> iter = coming[k].browse();
							while (iter.hasNext()) {
								Edge a = iter.next();
								if (a == e) {
									p.w(a);
									p.w(k);
								}
							}
						}
						throw new FoundException();
					}
				}
			}
		} catch (FoundException e) {
			List<Edge>[] g = new List[going.length - 1];
			List<Edge>[] c = new List[going.length - 1];
			Node[] n = new Node[going.length - 1];
			for (int i = 0; i < idDeleted; i++) {
				g[i] = going[i];
				c[i] = coming[i];
				n[i] = nodes[i];
			}
			for (int i = idDeleted + 1; i < going.length; i++) {
				g[i - 1] = going[i];
				c[i - 1] = coming[i];
				n[i - 1] = nodes[i];
				n[i - 1].id = i - 1;
			}
			going = g;
			coming = c;
			nodes = n;
			this.aproximizeEdge();
		}
	}

	public void discretize() {
		for (int i = 0; i < this.nodes.length; i++) {
			Browse<Edge> it = this.going[i].browse();
			while (it.hasNext()) {
				Edge e = it.next();
				e.minTT = (int) (e.minTT / main.deltaT);
			}
		}
	}
	
	public void computeDistribution() {
		// compute the distributions
		new computeDistribution();
	}

	public Graph(double[][] network) {
		// only used to create a background graph
		// the lat and longitude are reversed :
		for (int i = 0; i < network.length; i++) {
			double temp = network[i][5];
			network[i][5] = network[i][6];
			network[i][6] = temp;
			temp = network[i][7];
			network[i][7] = network[i][8];
			network[i][8] = temp;
		}

		// built the graph from the json data
		int nEdgesReal = network.length;
		Node[] n = new Node[nEdgesReal * 2];
		List<Edge>[] g = new List[2 * nEdgesReal];
		List<Edge>[] c = new List[2 * nEdgesReal];
		for (int i = 0; i < g.length; i++) {
			g[i] = new List<Edge>();
			c[i] = new List<Edge>();
		}
		size = new double[4];
		double minLat = network[0][5];
		double maxLat = network[0][5];
		double minLon = network[0][6];
		double maxLon = network[0][6];
		int limit = 0;
		// limit := currentMaxNodeID
		for (int i = 0; i < nEdgesReal; i++) {
			Node n1 = new Node((int) (network[i][1] * 10 + network[i][2]),
					network[i][6] / 10000, network[i][5] / 10000);
			Node n2 = new Node((int) (network[i][3] * 10 + network[i][4]),
					network[i][8] / 10000, network[i][7] / 10000);
			if (network[i][9] == 0) {
				System.out.println("division by 0 at " + i);
			}
			double w = network[i][0] / network[i][9];
			double sl = network[i][9];
			// creation of the Edge form the datas of the different
			// files
			int id1 = (int) network[i][10];
			int id2 = (int) network[i][11];
			Edge se = new Edge(n1, n2, (float) w,0,0);

			int posN1 = 0;
			int posN2 = 0;
			// adding Nodes at the good position
			try {
				for (int j = 0; j < limit; j++) {
					if (n[j].equals(n2)) {
						posN2 = j;
						throw new FoundException();
					}
				}
				posN2 = limit;
				n[limit] = n2;
				n[limit].id = limit;
				limit++;
				minLat = Math.min(minLat, n2.lat);
				maxLat = Math.max(maxLat, n2.lat);
				minLon = Math.min(minLon, n2.lon);
				maxLon = Math.max(maxLon, n2.lon);
			} catch (FoundException a) {
			}
			try {
				for (int j = 0; j < limit; j++) {
					if (n[j].equals(n1)) {
						posN1 = j;
						throw new FoundException();
					}
				}
				posN1 = limit;
				n[limit] = n1;
				n[limit].id = limit;
				limit++;
				minLat = Math.min(minLat, n2.lat);
				maxLat = Math.max(maxLat, n2.lat);
				minLon = Math.min(minLon, n2.lon);
				maxLon = Math.max(maxLon, n2.lon);
			} catch (FoundException a) {
			}

			// adding Edge
			se.start = n[posN1];
			se.end = n[posN2];
			g[posN1].add(se);
			c[posN2].add(se);
		}
		// delete unused cells (nb Node <= 2* nb Edge)
		Node[] nTemp = new Node[limit];
		List<Edge>[] gTemp = new List[limit];
		List<Edge>[] cTemp = new List[limit];
		for (int i = 0; i < limit; i++) {
			nTemp[i] = n[i];
			gTemp[i] = g[i];
			cTemp[i] = c[i];
		}
		nodes = nTemp;
		going = gTemp;
		coming = cTemp;
		size[0] = minLat;
		size[1] = maxLat;
		size[2] = minLon;
		size[3] = maxLon;

	}

	public Graph clone() {
		// give a copy of the graph
		Node[] n = new Node[nodes.length];
		List<Edge>[] g = new List[nodes.length];
		List<Edge>[] c = new List[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			n[i] = nodes[i].clone();
			g[i] = going[i].clone();
			c[i] = coming[i].clone();
		}
		double[] s = new double[4];
		System.arraycopy(size, 0, s, 0, 4);
		return new Graph(n, g, c, s);
	}

	public List<Node> findBorderNodesInside(int grillLength, int grillHeight,
			int x, int y) {
		// return the nodes on the frontiere (inside) the square (x,y) in the
		// grill
		double[] s = size;
		List<Node> n = new List<Node>();
		double horizontalLeft = (s[1] - s[0]) * x / grillLength + s[0];
		double horizontalRight = (s[1] - s[0]) * (x + 1) / grillLength + s[0];
		double verticalBottom = (s[3] - s[2]) * y / grillHeight + s[2];
		double verticalTop = (s[3] - s[2]) * (y + 1) / grillHeight + s[2];
		for (int i = 0; i < nodes.length; i++) {
			Browse<Edge> l = going[i].browse();
			while (l.hasNext()) {
				Edge e = l.next();
				if (e.start.lat < horizontalLeft
						&& (e.end.lat >= horizontalLeft && e.end.lat <= horizontalRight)
						&& (e.end.lon <= verticalTop && e.end.lon >= verticalBottom)) {
					n.addOnce(e.end);
				} else if (e.end.lat < horizontalLeft
						&& (e.start.lat >= horizontalLeft && e.start.lat <= horizontalRight)
						&& (e.start.lon <= verticalTop && e.start.lon >= verticalBottom)) {
					n.addOnce(e.start);
				} else if (e.start.lon < verticalBottom
						&& (e.end.lon >= verticalBottom && e.end.lon <= verticalTop)
						&& (e.end.lat <= horizontalRight && e.end.lat >= horizontalLeft)) {
					n.addOnce(e.end);
				} else if (e.end.lon < verticalBottom
						&& (e.start.lon >= verticalBottom && e.start.lon <= verticalTop)
						&& (e.start.lat <= horizontalRight && e.start.lat >= horizontalLeft)) {
					n.addOnce(e.start);
				} else if (e.start.lat > horizontalRight
						&& (e.end.lat <= horizontalRight && e.end.lat >= horizontalLeft)
						&& (e.end.lon >= verticalBottom && e.end.lon <= verticalTop)) {
					n.addOnce(e.end);
				} else if (e.end.lat > horizontalRight
						&& (e.start.lat <= horizontalRight && e.start.lat >= horizontalLeft)
						&& (e.start.lon >= verticalBottom && e.start.lon <= verticalTop)) {
					n.addOnce(e.start);
				} else if (e.start.lon > verticalTop
						&& (e.end.lon <= verticalTop && e.end.lon >= verticalBottom)
						&& (e.end.lat >= horizontalLeft && e.end.lat <= horizontalRight)) {
					n.addOnce(e.end);
				} else if (e.end.lon > verticalTop
						&& (e.start.lon <= verticalTop && e.start.lon >= verticalBottom)
						&& (e.start.lat >= horizontalLeft && e.start.lat <= horizontalRight)) {
					n.addOnce(e.start);
				}
			}
		}
		return n;
	}

	public List<Node> findNodesInside(int grillLength, int grillHeight, int x,
			int y) {
		// return the nodes on the frontiere (inside) the square (x,y) in the
		// grill
		double[] s = size;
		List<Node> res = new List<Node>();
		double horizontalLeft = (s[1] - s[0]) * x / grillLength + s[0];
		double horizontalRight = (s[1] - s[0]) * (x + 1) / grillLength + s[0];
		double verticalBottom = (s[3] - s[2]) * y / grillHeight + s[2];
		double verticalTop = (s[3] - s[2]) * (y + 1) / grillHeight + s[2];
		for (int i = 0; i < nodes.length; i++) {
			Node n = nodes[i];
			if (n.lat >= horizontalLeft && n.lat <= horizontalRight
					&& n.lon >= verticalBottom && n.lon <= verticalTop) {
				res.add(n);
			}
		}
		return res;
	}
}
