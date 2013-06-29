package SOTA;

import structure.Graph;
import structure.Node;

public class zeroDelayOptOrderingSOTA extends oneSourceSOTA {

	public zeroDelayOptOrderingSOTA(double budget, int s, int d, Graph g) {
		fft = false;
		zeroDelay = true;
		loop = false;
		withOptOrder = true;
		updateForAllT=false;
		newSOTA(budget, s, d, g);

	}

	public boolean prune(Node n, int t) {
		return (t < tau[n.id] || minTTfromOr[n.id] == -1
				|| minTTtoDest[n.id] == -1 || n == destination);
	}
};
