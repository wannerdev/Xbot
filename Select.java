package Xbot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Select {

	static boolean lock = true;

	public static void main(String[] args) throws InterruptedException {
		SelectFillerAndIO fill = new SelectFillerAndIO();
		// //from filesystem
		Float[] weights = { 0.5f, 0.25f, 0.25f, 0.5f };
		float[] loaded = fill.load();
		if (loaded != null) {
			weights[0] = loaded[0];
			weights[1] = loaded[1];
			weights[2] = loaded[2];
			weights[3] = loaded[3];
		} else {
			// arbitrary default values
		}

		// Candidates
		Set<Float[]> candidates = new HashSet<Float[]>();
		Set<Float[]> adaptedCands = new HashSet<Float[]>();
		adaptedCands.add(weights);
		candidates.add(weights);
		// set base quality 0
		candidates = fill.firstfill(candidates);

		int counter = 0;

		while (true) {

			Iterator<Float[]> it = candidates.iterator();

			// Server
			Thread tsrv;

			// evaluation
			String again = "y";
			// System.out.println(it.hasNext()+" " +"ad"+ adaptedCands.size() );
			// try {
			while (it.hasNext() && adaptedCands.size() != 0 && again != "n") {
				Tserver srv = new Tserver();
				tsrv = new Thread(srv, "Server");
				tsrv.start();

				it = candidates.iterator();
				Float[] cand1 = it.next();
				if (it.hasNext() == false)
					break;
				Float[] cand2 = it.next();
				if (it.hasNext() == false)
					break;
				Float[] cand3 = it.next();
				if (it.hasNext() == false)
					break;
				Float[] cand4 = it.next();
				System.out.println("Candidated Playing:");
				//for (int i=0; i<4;) {
				System.out.println("# " + cand1[0] + " " + cand1[1] + " " + cand1[2] + " " + cand1[3]);
				System.out.println("# " + cand2[0] + " " + cand2[1] + " " + cand2[2] + " " + cand2[3]);
				System.out.println("# " + cand3[0] + " " + cand3[1] + " " + cand3[2] + " " + cand3[3]);
				System.out.println("# " + cand4[0] + " " + cand4[1] + " " + cand4[2] + " " + cand4[3]);
				//}
				client[] gc = new client[4];
				gc[0] = new client(cand1);
				gc[1] = new client(cand2);
				gc[2] = new client(cand3);
				gc[3] = new client(cand4);

				Thread[] clientThread = new Thread[4];

				for (int i = 0; i < 4; i++) {
					clientThread[i] = new Thread(gc[i]);
					clientThread[i].start();
					long past = System.currentTimeMillis();
					TimeUnit.SECONDS.sleep(1);// to make sure they connect in correct order
				}
				while (lock) {
					for (int i = 0; i < 4; i++) {
						if (clientThread[i] != null) {
							clientThread[i].isAlive(); // Magic
						}
					}
					TimeUnit.SECONDS.sleep(2);
					// System.out.println("locked");
				};
				//TimeUnit.SECONDS.sleep(2);// warte 2sek
				int win = srv.getWinner();
				int sel = -1;
				if (win >= 0) {
					sel = win - 1;// player starts at 1
				}
				System.out.println("Select Thread getting player:" + win);
				srv.stop();
				srv = null;
				if (sel >= 0) {// valid significant game
					Float[] winner =  new Float[] { gc[sel].weights[0], gc[sel].weights[1], gc[sel].weights[2],
							gc[sel].weights[3] + 1f };
					// increase quality, should there be a winner shose quality shouldn't be
					// increased?
					System.out.println("Winner misfit:");
					System.out.println("0:" + winner[0] + " 1:" + winner[1] + " 2:" + winner[2] + " 3:" + winner[3]);
					//Float[] comp = {winner[0],winner[1], winner[2], winner[3]};
					boolean contains =false;
					for (Float[] cand : adaptedCands) {
						if((
						cand[0].equals(winner[0])&&
						cand[1].equals(winner[1])&&
						cand[2].equals(winner[2])
						//cand[3].floatValue()==winner[3].floatValue()
						)){
							contains = true;
						}
					}
					if(!contains) {
						adaptedCands.add(winner); // add the real winner
					}/*System.out.println("add result:"+);
					 * 
					 * for (Float[] f : adaptedCands) { System.out.println("# " + f[0] + " " + f[1]
					 * + " " + f[2] + " " + f[3]);
					 * System.out.println("equals result:"+winner.equals(f)); }
					 */
				}
				counter++;
				lock = true;
				/* Stop threads */
				for (int i = 0; i < 4; i++) {
					if (gc[i] != null) {// Magic
						gc[i].stop();
						clientThread[i].stop();
					}
				}
				// all candidates played at least once
				System.out.println("COUNTER :: " + counter);
				if (counter == 3) {
					counter = 0;

					// recombination
					Iterator<Float[]> ada = adaptedCands.iterator();

					Set<Float[]> delete = new HashSet<Float[]>();
					for (Float loser[] : candidates) {
						if (loser[3] < 1f) {
							delete.add(loser);
						}
					}
					// }
					candidates.clear(); // almost all die
					// candidates.removeAll(delete);
					candidates.removeAll(delete);// loosers die
					System.out.println("candis: " + candidates.size());
					while (ada.hasNext()) { // mutate //should be 4 3 winners one loaded(default or parent)
						Float[] candi = ada.next();
						Float[] mutated =new Float[4];
						if(!candidates.contains(candi)) {
							candidates.add(candi); // add the real winners (adapted+)
						}
						// Range ist um den wert herum maximal einfach +- 0.2
						if (candi[3] >= 0.5f) {// If winner (all should be winners except default)
							for (int j = 0; j < 3; j++) { // mutate the weights
								//TODO change less depending on quality
								mutated[j] = (float) ((float) candi[j] + ((0.01) * Math.random() - 0.01 * Math.random()));
								// respect 1 as maximum and 0 as minimum
								if (mutated[j] < 0) {
									mutated[j] = 0f;
								}
								if (mutated[j] > 1) {
									mutated[j] = 1f;
								}
							}
							mutated[3] = 0.1f; // qualität 0 da nur mutated winner, 0.1 just for id purposes
							candidates.add(mutated); // add mutated winners to candidates
						}
					}
					System.out.println("Adapted cands"+adaptedCands.size());
					for (Float[] f : adaptedCands) {
						System.out.println("ADapted misfits:" + f[0] +" "+ f[1] +" "+ f[2]+" " + f[3]);
					}
					Float[] best = fill.getBestRef(adaptedCands);
					if(adaptedCands.remove(best)==false) {
						System.err.println("fuck");
					};
					Float[] best2 = fill.getBestRef(adaptedCands);
					adaptedCands.remove(best2);
					Float[] best3 = fill.getBestRef(adaptedCands);
					adaptedCands.remove(best3);
					adaptedCands.removeAll(adaptedCands); //clean winners for new ones.
					weights = best;
					adaptedCands.add(best); // Add Best rated Parent 
					adaptedCands.add(best2); 
					adaptedCands.add(best3); 
					// candidates.add(adaptedCands);
					candidates = fill.fillInUntilFull(candidates);
					fill.save(weights);
				}
			}
			// Response to not enough candidates in iterator
			System.out.println("No Cands?");
			candidates = fill.fillInUntilFull(candidates);
			//it = candidates.iterator();
		}
	}

}
