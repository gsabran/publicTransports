package main;

import structure.Edge;

public class withAverageTT extends function {
	public double metric(Edge e) {
		return e.average() * main.deltaT;
	}
}