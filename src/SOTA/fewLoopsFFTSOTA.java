package SOTA;

import structure.Graph;
import structure.Node;

public class fewLoopsFFTSOTA extends oneSourceSOTA{
	public fewLoopsFFTSOTA(double budget, int s, int d, Graph g){
		fft = true;
		zeroDelay = false;
		loop = true;
		withOptOrder = true;
		updateForAllT=false;
		newSOTA(budget, s, d, g);
	}
	
	public boolean prune(Node n, int t){
		return(t < tau[n.id]
				|| minTTfromOr[n.id]==-1
				|| minTTtoDest[n.id]==-1
				|| n == destination);
	}
};
