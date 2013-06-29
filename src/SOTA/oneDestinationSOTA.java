package SOTA;

import other.p;
import main.*;
import structure.*;

public class oneDestinationSOTA extends SOTA {
	public void result() {
	}
	
	public void pruneIniFromOrigin() {}
	
	public oneDestinationSOTA( double budgetMax, int d, Graph g){
		fft = false;
		zeroDelay = false;
		loop = true;
		withOptOrder = true;
		List<Node> origins = new List<Node>();
		Node dest = g.nodes[d];
		double[] minTT = new withMinTT().minTTtoDestination(g, dest);
		for(int i=0; i<g.nodes.length; i++){
			if(minTT[i]<=budgetMax && minTT[i]>-1){
				origins.add(g.nodes[i]);
			}
		}
		this.origins = origins;
		newSOTA(budgetMax,origins, dest, g);
	}
	
	public boolean prune(Node n, int t){
		return(t < minTTtoDest[n.id]
				|| n == destination
				|| minTTtoDest[n.id]==-1);
	}
	public oneDestinationSOTA(){};
}
