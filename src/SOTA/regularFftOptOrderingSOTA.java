package SOTA;

import other.p;
import structure.*;

public class regularFftOptOrderingSOTA extends oneSourceSOTA {

	public regularFftOptOrderingSOTA(double budget, int s, int d, Graph g) {
		fft = true;
		zeroDelay = false;
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
