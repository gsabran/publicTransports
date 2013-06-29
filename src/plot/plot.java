package plot;

import java.awt.Dimension;

import javax.swing.JFrame;

import other.GetDonnees;

import com.sun.media.sound.*;
import main.*;
import structure.*;

public class plot {
	static int width = 650;
	static int height = 750;

	public static void plotGraph(Graph g, List<Edge> l, List<Node> n,
			List<Node> n2, float[] proba, List<Node> nCircle, boolean circle,
			int[] nbUpdate, String title, double X0, double Y0, double X1,
			double Y1, int nbClick, int grillLength, int grillHeight, int x,
			int y, int[] equivalent, double budget) {
		if (!main.noPlot) {
			JFrame frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			int w = (int) Math
					.min((height * (g.size[1] - g.size[0]) / (g.size[3] - g.size[2])),
							width);
			int h = height * width / w;
			frame.setSize(w, h);
			frame.setVisible(true);
			if (grillLength != 0) {
				frame.add(new plotGraph(g, l, n, n2, proba, nCircle, circle,
						nbUpdate, X0, Y0, X1, Y1, nbClick, grillLength,
						grillHeight, x, y, equivalent, budget));
			} else {
				frame.add(new plotGraph(g, l, n, n2, proba, nCircle, circle,
						nbUpdate, X0, Y0, X1, Y1, nbClick, 0, 0, 0, 0, equivalent, budget));
			}
			frame.setLocationRelativeTo(null);
		}
	}

	public static void plotGraph(Graph g, List<Edge> l, List<Node> n,
			List<Node> n2, List<Node> nCircle, boolean circle, String title) {
		plotGraph(g, l, n, n2,null, nCircle, circle, null, title, 0, 0, 0, 0, 0, 0,
				0, 0, 0, new int[0],0);
	}
	
	public static void plotGraph(Graph g) {
		plotGraph(g, null, null, null,null, null, true, null, "the graph", 0, 0, 0, 0, 0, 0,
				0, 0, 0, new int[0],0);
	}
	
	public static void plotGraph(Graph g, Node n) {
		plotGraph(g, null, null, null,null, new List<Node>(n), true, null, "the graph", 0, 0, 0, 0, 0, 0,
				0, 0, 0, new int[0],0);
	}
	
	public static void plotGraph(Graph g, List<Node> n) {
		plotGraph(g, null, null, null,null, n, true, null, "the graph", 0, 0, 0, 0, 0, 0,
				0, 0, 0, new int[0],0);
	}

	public static void plotGraph(Graph g, List<Edge> l, List<Node> n,
			int grillLength, int grillHeight, int x, int y, String title) {
		plotGraph(g, l, n, null,null, null, true, null, title, 0, 0, 0, 0, 0,
				grillLength, grillHeight, x, y, new int[0],0);
	}

	
}