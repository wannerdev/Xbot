package Xbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class SelectFillerAndIO {

	/**
	 * create 11 cands 0,1 -0,9 and some completely random with q=0.3
	 * 
	 * @param cands
	 * @return
	 */
	Set<Float[]> firstfill(Set<Float[]> cands) {
		for (int i = 1, j = 10; i < 10; i++, j--) {
			float n = (j % 2 == 0) ? j : i;
			float m = (i % 2 == 0) ? j : i;
			float o = (j % 3 == 0) ? j : i; // try to get a little bit of variety in ratios
			cands.add(new Float[] { n / 10f, m / 10f, o / 10f, 0f });
		}
		for (int i = 0; i < 12 - cands.size(); i++) {
			cands.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
					(float) ((float) Math.random()), 0.3f });
		}
		return cands;
	}

	public Float[] getBest(Set<Float[]> adaptedCands){
		System.out.println(adaptedCands.size());
		Float[] cache = { 0f, 0f, 0f, -1f };
		Float[] cache2 = { 0f, 0f, 0f, -1f };
		for (Float[] win : adaptedCands) {
			if (win[3] >= cache2[3]) {
				cache2 = win;
			}
		}

		cache[0] = cache2[0];
		cache[1] = cache2[1];
		cache[2] = cache2[2];
		cache[3] = cache2[3];
		if (cache[3] == 0)
			System.out.println("Best Bs "+adaptedCands.size());// throw new Exception("BEst is bullshit");
		for (Float[] f : adaptedCands) {
			System.out.println("ADapted misfits:" + f[0] +" "+ f[1] +" "+ f[2]+" " + f[3]);
		}
		return cache;
	}

	/**
	 * 
	 * @param winners
	 * @return
	 */
	Set<Float[]> fillInUntilFull(Set<Float[]> winners) {
		// winners should be max 4
		for (int i = 0; i < 12 - winners.size(); i++) {
			winners.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
					(float) ((float) Math.random()), 0f });
		}
		return winners;
	}

	public float[] load() {
		Scanner scanner = null;
		try {
			float[] weights = new float[4];
			scanner = new Scanner(new File("weights.csv"));
			scanner.useDelimiter(";");
			while (scanner.hasNext()) {
				weights[0] = Float.valueOf(scanner.next());
				weights[1] = Float.valueOf(scanner.next());
				weights[2] = Float.valueOf(scanner.next());
				weights[3] = Float.valueOf(scanner.next());
			}
			scanner.close();
			return weights;
		} catch (FileNotFoundException e1) {
			System.out.println("using default values");
			// do nothing use standards
			return null;
		}
	}

	public void save(Float[] weights) {
		try {
			System.out.println("=================Saving=================");
			System.out.println("0:" + weights[0] + " 1:" + weights[1] + " 2:" + weights[2] + " 3:" + weights[3]);
			PrintWriter pw = new PrintWriter(new File("weights.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append(weights[0]);
			sb.append(';');
			sb.append(weights[1]);
			sb.append(';');
			sb.append(weights[2]);
			sb.append(';');
			sb.append(weights[3]);
			sb.append(';');
			pw.write(sb.toString());
			pw.close();

		} catch (FileNotFoundException e) {
			// kill process
			throw new AssertionError();
		}
	}
}
