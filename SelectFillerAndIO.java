package Xbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;

public class SelectFillerAndIO {

	/**
	 * create 11 cands 0,1 -0,9 and some completely random with q=0
	 * @param cands
	 * @return
	 */
	Set<Float[]> firstfill(Set<Float[]> cands) {
		for (int i = 1; i < 10; i++) {
			cands.add(new Float[] { i/10f,i/10f,i/10f, 0f });
		}
		cands.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
				(float) ((float)  Math.random()), 0f });
		cands.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
				(float) ((float)  Math.random()), 0f });
		return cands;
	}
	
	public Float[] getBest(Set<Float[]> adaptedCands) throws Exception {
		System.out.println(adaptedCands.size());
		Float[] cache = { 0f, 0f, 0f, -1f };
		Float[] cache2 = { 0f, 0f, 0f, -1f };
		for (Float[] win : adaptedCands) {
			if (win[3] >= cache[3]) {
				cache = win;
			}
		}
		if(cache.equals(cache2))throw new Exception("BEst is bullshit");
		return cache;
	}
	/**
	 * 
	 * @param winners
	 * @return
	 */
	Set<Float[]> refill(Set<Float[]> winners) {
		// winners should be max 4
		for (int i = 0; i < 12 - winners.size(); i++) {
			winners.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
					(float) ((float)  Math.random()), 0f });
		}
		return winners;
	}


	public float[] load() {
		Scanner scanner = null;
		try {
			float[] weights=new float[4];
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
			System.out.println("saving to disk");
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
			//kill process
			throw new AssertionError();
		}
	}
}
