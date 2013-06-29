package main;

import java.io.IOException;

import javax.swing.JFrame;

import other.GetDonnees;
import other.Writer;
import other.p;
import other.utils;
import plot.*;
import structure.*;
import SOTA.*;

public class main {
	// different important values

	// different important values
	public static double[][] network;
	public static int nModes = 3;
	public static double deltaT = 1;
	public static double Tmax = 5000;
	public static double zero = 0.00001; // used to avoid taking into account
	public static int nbThreads = 5;
	public static int nbQuantiles = 0;
	public static String metricForLoops = "minTT";
	public static String metricForReach = "minTT";
	public static boolean displayUsefulNodes = true;

	public static Graph graph;
	public static String repertoire = "data";
	public static String networkName = "SF";
	public static boolean noPlot = false;
	public static boolean drawEdges = true;
	public static int maxPeriod = 3600;

	public static void main(String[] args) {
		//Graph g = new Graph();
		//g.toText(networkName);
		Graph g = Graph.fromText(networkName);
		
		int b = 4000;
		int s =  (int) (Math.random()*g.nodes.length);

		int d =  new withAverageTT().getNodeDistant(g.nodes[s], b*95/100,
						 g).id;
		
		new fewLoopsSOTA(b, s, d, g);
		new zeroDelayOptOrderingSOTA(b,s,d,g);
	}
}





































/**/
