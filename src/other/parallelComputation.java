package other;

import SOTA.SOTA;
import main.main;
import structure.*;

public abstract class parallelComputation extends SOTA  implements mapReduce {

	public int nb_threads;
	public int current_nb_threads;
	public int[] done;
	public int nextToDo;
	public double[] budgets;

	public abstract void prepare2();

	public void computation() {

		prepare();
		prepare2();

		nextToDo = nb_threads;
		current_nb_threads = nb_threads;
		Thread[] threads = new Thread[nb_threads];

		System.out.println();
		for (int i = 0; i < nb_threads; i++) {
			final int num = i;
			Thread t = new Thread() {
				public void run() {
					map(num);
				}
			};
			t.start();
			threads[i] = t;
		}
		for (int i = 0; i < nb_threads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		String rep = "results/"+main.networkName+"/";					
		reduce(rep);
	}
}
