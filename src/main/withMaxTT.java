package main;

import structure.Edge;

public class withMaxTT  extends function{
	public double metric(Edge e){
		return e.maxTT();
	}
}
