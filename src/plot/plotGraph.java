package plot;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import SOTA.*;
import other.p;
import other.utils;
import main.*;
import structure.*;

public class plotGraph extends JPanel implements MouseListener {
	// some parameters to define what is drawn
	public static boolean deadEnd = false;
	public static boolean colorDirection = true;
	public static boolean allowZoom = true;
	public static boolean allowDijkstra = true;
	public static boolean drawCircle = true;
	public static boolean average = true;
	public static boolean allowInteraction = true;
	Graph graph;
	// the path
	List<Edge> edges;
	List<Node> searched;
	Graph searchedGraph;
	List<Node> used;
	float[] proba;
	// some nodes circled
	List<Node> circled;
	// vizualize some metric on the nodes
	int[] metric;
	// the boundaries
	double x0;
	double y0;
	double x1;
	double y1;
	Graphics2D g2d;
	int nbClick;
	int h;
	int w;
	double[] s;
	int grillLength;
	int grillHeight;
	int xGrill;
	int yGrill;
	int[] equivalent;// the relation between the id in the original Graph and
						// the pruned graph
	double budget;

	public void paintComponent(Graphics g) {
		// initialization of the graphic window
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		Dimension size = getSize();
		Insets insets = getInsets();
		// window size
		w = size.width - insets.left - insets.right;
		h = size.height - insets.top - insets.bottom;
		// setting the frame
		if (x1 == 0) {
			x1 = w;
		}
		if (y1 == 0) {
			y1 = h;
		}
		// setting a margin
		double margin = 0.02;
		x0 = x0 - (x1 - x0) * margin;
		y0 = y0 - (y1 - y0) * margin;
		x1 = x1 + (x1 - x0) * margin;
		y1 = y1 + (y1 - y0) * margin;

		s = graph.size;

		searchedGraph = searchedGraph();
		double thick = 1.5;

		final int n = graph.nodes.length;
		double speedMax = 0;
		for (int k = 0; k < graph.nodes.length; k++) {
			Browse<Edge> b = graph.going[k].browse();
			while (b.hasNext()) {
				Edge ed = b.next();
				speedMax = Math.max(speedMax, ed.average);
			}
		}
		for (int i = 0; i < n; i++) {
			// show if the point is a dead end
			if (deadEnd) {
				drawDeadEnd(i);
			}
			// draw the edges

			DrawExitEdges(i, speedMax);
		}

		g2d.setColor(Color.black);
		if (grillLength != 0 && grillHeight != 0) {
			double deltaX = graph.size[1] - graph.size[0];
			double deltaY = graph.size[3] - graph.size[2];

			// draw the grill (used for arcflags)
			for (int i = 0; i <= grillLength; i++) {
				double x = (deltaX * i / grillLength) * w / (s[1] - s[0]);
				draw(g2d, (x - this.x0) * w / (this.x1 - this.x0), 0,
						(x - this.x0) * w / (this.x1 - this.x0), h);
			}
			for (int i = 0; i <= grillHeight; i++) {
				double y = (deltaY * i / grillLength) * h / (s[3] - s[2]);
				draw(g2d, 0, (y - this.y0) * h / (this.y1 - this.y0), w,
						(y - this.y0) * h / (this.y1 - this.y0));
			}
			g2d.setColor(Color.red);
			List<Node> nodes = graph.findBorderNodesInside(grillLength,
					grillHeight, xGrill, yGrill);
			g2d.setColor(Color.red);
			// we circle all the points in cCircle
			Browse<Node> c2 = nodes.browse();
			// length = c2 == null ? 0 : c2.size();
			while (c2.hasNext()) {
				Node node = c2.next();
				double x = (node.lat - s[0]) * w / (s[1] - s[0]);
				double y = h - (node.lon - s[2]) * h / (s[3] - s[2]);
				g2d.drawOval(
						(int) ((x - this.x0) * w / (this.x1 - this.x0)) - 10,
						(int) ((y - this.y0) * h / (this.y1 - this.y0)) - 10,
						20, 20);
			}
		}

		Browse<Node> c2;
		g2d.setColor(Color.blue);
		// we draw all the point in c
		c2 = used == null ? new Browse<Node>() : used.browse();
		// length = c2.isEmpty() ? 0 : c2.size();
		int count = 0;
		while (c2.hasNext()) {
			Node node = c2.next();
			if (proba != null && main.displayUsefulNodes) {
				g2d.setColor(new Color(255 - (int) (255 * proba[node.id]), 0,
						(int) (255 * proba[node.id])));
			}
			double x = (node.lat - s[0]) * w / (s[1] - s[0]);
			double y = h - (node.lon - s[2]) * h / (s[3] - s[2]);
			g2d.fillOval((int) ((x - this.x0) * w / (this.x1 - this.x0))
					- (int) (5 * w / (this.x1 - this.x0)) / 2,
					(int) ((y - this.y0) * h / (this.y1 - this.y0))
							- (int) (5 * h / (this.y1 - this.y0)) / 2,
					(int) (5 * w / (this.x1 - this.x0)),
					(int) (5 * h / (this.y1 - this.y0)));
			count++;
		}

		if (metric != null) {
			int max = 0;
			int sum = 0;
			for (int i : metric) {
				max = Math.max(i, max);
				sum += i;
			}
			max--;
			for (int i = 0; i < metric.length; i++) {
				if (metric[i] > 1) {
					metric[i]--;
					g2d.setColor(new Color(255 * metric[i] / max, 255 - 255
							* metric[i] / max, 255 - 255 * metric[i] / max));
					double x = (graph.nodes[i].lat - s[0]) * w / (s[1] - s[0]);
					double y = h - (graph.nodes[i].lon - s[2]) * h
							/ (s[3] - s[2]);
					g2d.fillOval(
							(int) ((x - this.x0) * w / (this.x1 - this.x0)),
							(int) ((y - this.y0) * h / (this.y1 - this.y0)),
							(int) (5 * w / (this.x1 - this.x0)),
							(int) (5 * h / (this.y1 - this.y0)));
				}
			}
		}

		g2d.setColor(Color.red);
		// we circle all the points in cCircle
		if (drawCircle) {
			c2 = circled == null ? new Browse<Node>() : circled.browse();
			// length = c2 == null ? 0 : c2.size();
			count = 0;
			while (c2.hasNext()) {
				Node node = c2.next();
				double x = (node.lat - s[0]) * w / (s[1] - s[0]);
				double y = h - (node.lon - s[2]) * h / (s[3] - s[2]);
				g2d.drawOval(
						(int) ((x - this.x0) * w / (this.x1 - this.x0)) - 10,
						(int) ((y - this.y0) * h / (this.y1 - this.y0)) - 10,
						20, 20);
				count++;
			}
		}
		Browse<Edge> l2 = edges == null ? new Browse<Edge>() : edges.browse();
		// we draw the path l
		g2d.setColor(Color.blue);
		while (l2.hasNext()) {
			Edge e = l2.next();
			double x1 = (e.start.lat - s[0]) * w / (s[1] - s[0]);
			double y1 = h - (e.start.lon - s[2]) * h / (s[3] - s[2]);
			double x2 = (e.end.lat - s[0]) * w / (s[1] - s[0]);
			double y2 = h - (e.end.lon - s[2]) * h / (s[3] - s[2]);
			draw(g2d, (x1 - this.x0) * w / (this.x1 - this.x0), (y1 - this.y0)
					* h / (this.y1 - this.y0), (x2 - this.x0) * w
					/ (this.x1 - this.x0), (y2 - this.y0) * h
					/ (this.y1 - this.y0));
			double dx = x2 - x1;
			double dy = y2 - y1;
			double l = Math.sqrt(dx * dx + dy * dy);
			double cos = dx / l;
			double sin = dy / l;

			double[] x = new double[4];
			x[0] = (x1 - this.x0) * w / (this.x1 - this.x0)
					- (double) Math.floor(thick * sin);
			x[1] = (x1 - this.x0) * w / (this.x1 - this.x0)
					+ (double) Math.floor(thick * sin);
			x[2] = (x2 - this.x0) * w / (this.x1 - this.x0)
					+ (double) Math.floor(thick * sin);
			x[3] = (x2 - (double) Math.floor(thick * sin) - this.x0) * w
					/ (this.x1 - this.x0);
			double[] y = new double[4];
			y[0] = (y1 - this.y0) * h / (this.y1 - this.y0)
					+ (double) Math.floor(thick * cos);
			y[1] = (y1 - this.y0) * h / (this.y1 - this.y0)
					- (double) Math.floor(thick * cos);
			y[2] = (y2 - this.y0) * h / (this.y1 - this.y0)
					- (double) Math.floor(thick * cos);
			y[3] = (y2 - this.y0) * h / (this.y1 - this.y0)
					+ (double) Math.floor(thick * cos);
			fillPolygon(g2d, x, y);
		}

		addMouseListener(new MouseAdapter() {
			// this is used to interact with the mouse (find path, zoom...)

			int x1Pressed = 0;
			int y1Pressed = 0;
			int x2Pressed = 0;
			int y2Pressed = 0;
			int x1Released = 0;
			int y1Released = 0;
			int idNode1 = 0;
			int idNode2 = 0;
			Dimension size = getSize();
			Insets insets = getInsets();
			int w = size.width - insets.left - insets.right;
			int h = size.height - insets.top - insets.bottom;

			// data recuperation
			double[] s = graph.size;

			@Override
			public void mouseReleased(MouseEvent event) {
				if (allowInteraction) {
					if (event.getButton() == MouseEvent.BUTTON1) {
						x1Released = event.getX();
						y1Released = event.getY();

						if (allowZoom && x1Released != x1Pressed
								&& y1Released != y1Pressed) {
							System.out.println("x" + x1Pressed + "->"
									+ x1Released + "; y " + y1Pressed + "->"
									+ y1Released);
							plot.plotGraph(graph, edges, searched, used, proba,
									circled, drawCircle, metric, "zoom",
									x1Released, y1Released, x1Pressed,
									y1Pressed, 0, grillLength, grillHeight,
									xGrill, yGrill, equivalent, budget);
						} else {
							// computing dijkstra
							if (allowDijkstra && idNode1 != 0 && idNode2 != 0
									&& idNode1 != idNode2) {
								nbClick = 0;
								new fewLoopsSOTA(budget, idNode2, idNode1,
										graph);
							}
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1) {
					if (nbClick < 2) {
						nbClick++;
						x2Pressed = x1Pressed;
						y2Pressed = y1Pressed;
						x1Pressed = event.getX();
						y1Pressed = event.getY();
						idNode2 = idNode1;
						int min1 = Integer.MAX_VALUE;
						int idMin1 = -1;
						for (int i = 0; i < graph.nodes.length; i++) {
							int posX = (int) Math
									.round((graph.nodes[i].lat - s[0]) * w
											/ (s[1] - s[0]));
							int posY = (int) (h - (graph.nodes[i].lon - s[2])
									* h / (s[3] - s[2]));
							if (Math.max(Math.abs(posX - x1Pressed),
									Math.abs(posY - y1Pressed)) < min1) {
								min1 = Math.min(Math.abs(posX - x1Pressed),
										Math.abs(posY - y1Pressed));
								idMin1 = i;
							}
						}
						assert idMin1 != -1;
						idNode1 = idMin1;
					}
					if (nbClick == 2) {
						// finding the two nearest nodes
						int min1 = Integer.MAX_VALUE;
						int idMin1 = -1;
						int min2 = Integer.MAX_VALUE;
						int idMin2 = -1;
						for (int i = 0; i < graph.nodes.length; i++) {
							int posX = (int) Math
									.round((graph.nodes[i].lat - s[0]) * w
											/ (s[1] - s[0]));
							int posY = (int) (h - (graph.nodes[i].lon - s[2])
									* h / (s[3] - s[2]));
							if (Math.max(Math.abs(posX - x1Pressed),
									Math.abs(posY - y1Pressed)) < min1) {
								min1 = Math.min(Math.abs(posX - x1Pressed),
										Math.abs(posY - y1Pressed));
								idMin1 = i;
							}
							if (Math.max(Math.abs(posX - x2Pressed),
									Math.abs(posY - y2Pressed)) < min2) {
								min2 = Math.max(Math.abs(posX - x2Pressed),
										Math.abs(posY - y2Pressed));
								idMin2 = i;
							}
						}
						assert idMin1 != -1 && idMin2 != -1;
						idNode1 = idMin1;
						idNode2 = idMin2;
					}
				}
				if (allowZoom && allowInteraction
						&& event.getButton() == MouseEvent.BUTTON3) {
					plot.plotGraph(graph, edges, searched, used, proba,
							circled, drawCircle, metric, "dezoom", 0, 0, 0, 0,
							0, grillLength, grillHeight, xGrill, yGrill,
							equivalent, budget);

				}
			}
		});
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public plotGraph(Graph g, List<Edge> list, List<Node> searched,
			List<Node> used, float[] proba, List<Node> list2Circle,
			boolean circle, int[] intTab, double X0, double Y0, double X1,
			double Y1, int n, int grillL, int grillH, int x, int y,
			int[] equivalent, double budget) {
		graph = g;
		edges = list;
		this.searched = searched;
		this.used = used;
		this.proba = proba;
		circled = list2Circle;
		drawCircle = circle;
		metric = intTab;
		x0 = Math.min(X0, X1);
		x1 = Math.max(X0, X1);
		y0 = Math.min(Y0, Y1);
		y1 = Math.max(Y0, Y1);
		nbClick = n;
		grillLength = grillL;
		grillHeight = grillH;
		xGrill = x;
		yGrill = y;
		this.equivalent = equivalent;
		this.budget = budget;
	}

	public void draw(Graphics2D g, double x1, double y1, double x2, double y2) {
		g.drawLine((int) (x1), (int) (y1), (int) (x2), (int) (y2));
	}

	public void fillPolygon(Graphics2D g, double[] x, double[] y) {
		int[] x2 = new int[x.length];
		int[] y2 = new int[y.length];
		for (int i = 0; i < x.length; i++) {
			x2[i] = (int) (x[i]);
			y2[i] = (int) (y[i]);
		}
		g.fillPolygon(x2, y2, x.length);
	}

	public Graph searchedGraph() {
		if (searched != null) {
			Graph g2 = graph.clone();
			boolean[] search = new boolean[graph.nodes.length];
			Browse<Node> b = searched.browse();
			while (b.hasNext()) {
				Node n = b.next();
				search[equivalent[n.id]] = true;
			}
			for (int i = 0; i < g2.nodes.length; i++) {
				if (!search[i]) {
					g2.remove(g2.nodes[i]);
				}
			}
			return g2;
		} else {
			return graph;
		}
	}

	public void drawDeadEnd(int i) {
		Browse<Edge> l = graph.coming[i].browse();
		Browse<Edge> c = graph.going[i].browse();
		if (l == null) {
			if (colorDirection) {
				g2d.setColor(Color.green);
				// green is used if we can start from this point but
				// can't go there
			}
			double x = graph.nodes[i].lat;
			double y = graph.nodes[i].lon;
			g2d.fillOval(
					(int) (((x - s[0]) * w / (s[1] - s[0]) - this.x0) * w / (this.x1 - this.x0)),
					(int) (h - ((y - s[2]) * h / (s[3] - s[2]) - this.y0) * h
							/ (this.y1 - this.y0)), 5, 5);
		}
		if (c == null) {
			if (colorDirection) {
				g2d.setColor(Color.red);
				// red is used if we can go to this point but can't
				// start from there
			}
			double x = graph.nodes[i].lat;
			double y = graph.nodes[i].lon;
			g2d.fillOval(
					(int) (((x - s[0]) * w / (s[1] - s[0]) - this.x0) * w / (this.x1 - this.x0)),
					(int) (h - ((y - s[2]) * h / (s[3] - s[2]) - this.y0) * h
							/ (this.y1 - this.y0)), 5, 5);
		}
	}

	public void DrawExitEdges(int i, double speedMax) {
		Browse<Edge> l = graph.going[i].browse();
		boolean isNotSearched = searchedGraph.coming[i].isEmpty()
				&& searchedGraph.going[i].isEmpty();
		while (l.hasNext()) {
			Edge e = l.next();
			if (average) {
				double x1 = (e.start.lat - s[0]) * w / (s[1] - s[0]);
				double y1 = h - (e.start.lon - s[2]) * h / (s[3] - s[2]);
				double x2 = (e.end.lat - s[0]) * w / (s[1] - s[0]);
				double y2 = h - (e.end.lon - s[2]) * h / (s[3] - s[2]);
				if (isNotSearched
						|| (searchedGraph.coming[e.end.id].isEmpty() && searchedGraph.going[e.end.id]
								.isEmpty())) {
					double transparence = 0.7;
					g2d.setColor(new Color(
							(int) (255 * transparence + (1 - transparence)
									* (255 - 255 * e.average / speedMax)),
							(int) (255 * transparence + (1 - transparence)
									* (255 * e.average / speedMax)),
							(int) (255 * transparence)));
				} else {
					g2d.setColor(new Color(
							255 - (int) (255 * e.average / speedMax),
							(int) (255 * e.average / speedMax), 0));
				}
				draw(g2d, (x1 - this.x0) * w / (this.x1 - this.x0),
						(y1 - this.y0) * h / (this.y1 - this.y0),
						(x2 - this.x0) * w / (this.x1 - this.x0),
						(y2 - this.y0) * h / (this.y1 - this.y0));
			} else {
				double x1 = (e.start.lat - s[0]) * w / (s[1] - s[0]);
				double y1 = h - (e.start.lon - s[2]) * h / (s[3] - s[2]);
				double x2 = (e.end.lat - s[0]) * w / (s[1] - s[0]);
				double y2 = h - (e.end.lon - s[2]) * h / (s[3] - s[2]);
				if (colorDirection) {
					g2d.setColor(Color.green);
				}
				draw(g2d, (x1 - this.x0) * w / (this.x1 - this.x0),
						(y1 - this.y0) * h / (this.y1 - this.y0),
						(x2 - this.x0) * w / (this.x1 - this.x0),
						(y2 - this.y0) * h / (this.y1 - this.y0));
			}
		}
	}
}