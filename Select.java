package Xbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import lenz.htw.sawhian.Server;

public class Select {
	//static float weight_x = 0.5f, weight_y = 0.25f, weight_z = 0.25f; //arbitrary default values
	static Float[] weights= {0.5f,0.25f,0.25f, 1f};
	static boolean lock = true;
	//Not WORKING ATM
	
	public static void main(String[] args) {			
		load();		//from filesystem
		
		int quali[]= new int[12];
		//Candidates
		Set<Float[]> candidates = new HashSet<Float[]>();
		Set<Float[]> adaptedCands = new HashSet<Float[]>();
		adaptedCands.add(Select.weights);
		
		candidates = firstfill(candidates);
		Iterator<Float[]> it = candidates.iterator();
		
        while(true) {
        	//Server 
        	Thread tsrv;
        	//clients
        	Thread t1;
    		Thread t2;
    		Thread t3;
    		Thread t4;
    		//evaluation
    		int counter = 0;
    		String again = "y";
    		int winner=-1;
        	while( it.hasNext() && adaptedCands.size() != 0 && again!="n"){
        			Tserver srv= new Tserver();
	        		tsrv = new Thread (srv,"Server");
	        		tsrv.start();
	        		
	        		Float[] cand1 = it.next();
	        		if(it.hasNext() == false)break;
	        		Float[] cand2 = it.next();
	        		if(it.hasNext() == false)break;
	        		Float[] cand3 = it.next();
	        		if(it.hasNext() == false)break;
	        		Float[] cand4 = it.next();
	        		
	        		client[] gc = new client[4];
	        		gc[0] = new client(cand1[0],cand1[1],cand1[2]);
	        		gc[1] = new client(cand2[0],cand2[1],cand2[2]);
	        		gc[2] = new client(cand3[0],cand3[1],cand3[2]);
	        		gc[3] = new client(cand4[0],cand4[1],cand4[2]);
	
	        		t1 = new Thread(gc[0]);
	        		t2 = new Thread(gc[1]);
	        		t3 = new Thread(gc[2]);
	        		t4 = new Thread(gc[3]);
	        		
	        		t1.start();
	        		t2.start();
	        		t3.start();
	        		t4.start();

	        		long jetzt = System.currentTimeMillis();
	        		while(System.currentTimeMillis() > jetzt +2000);//warte 2sek
	        		while(lock);
					int sel = 0;
					System.out.println("getting winner");
	        		//TODO who won
					sel = srv.getWinner();
					srv.stop();
					System.out.println("getting winner:"+winner);
	        		adaptedCands.add(new Float[] {gc[sel].weight_x, gc[sel].weight_y, gc[sel].weight_z});
	        		//System.out.println(); return value of server?	        		
	        		counter++;
	        		
	        		//all candidates played at least once
	        		if(counter == 3) {
	        			//recombination
	        			
	        			//adaptedCands.forEach((Integer[] cand)-> X++);
	        			int j=0, l=0, k=0;
	        			
	        			candidates.removeAll(candidates);//loosers die
	        			it = adaptedCands.iterator();
	        			while( it.hasNext() ) { //mutate
	        				Float[] candi = it.next();
	        							//Range ist um den wert herum maximal einfach 
	        				candi[0] = (float) ((float) candi[0]+((0.2)*Math.random()-0.2*Math.random()));
	        				candi[1]= (float) ((float) candi[1]+((0.2)*Math.random()-0.2*Math.random()));
	        				candi[2]= (float) ((float) candi[2]+((0.2)*Math.random()-0.2*Math.random()));
	        				candi[3]++;
	        				
	        				candidates.add(candi); //refill candidates  
	        			}
	        			//and use one winner as test?
	        			
	        	        save();
	        			System.out.println("Anothertime? y/n");
	        			Scanner in = new Scanner(System.in);
	        			int i = in.nextInt();
	        			again = in.next();
	        			if (again.equals("n"))break;
	        		}
	        	}
        	}
        }
	
	private static Set<Float[]> firstfill(Set<Float[]> cands) {
		for(int i=0; i < 12; i++) {
			cands.add(new Float[] {(float) (Math.random()),(float) (Math.random()*100),(float) (Math.random()*100)});
		}
		return cands;
	}
	
	private static Set<Float[]> mutate(Set<Float[]> winners) {
		for(int i=0; i < 12; i++) {
			winners.add(new Float[] {(float) (Math.random()),(float) (Math.random()*100),(float) (Math.random()*100)});
		}
		return winners;
	}

	private static void load() {
		Scanner scanner=null;
		try {
			
			scanner = new Scanner(new File("weights.csv"));
	        scanner.useDelimiter(";");
	        while(scanner.hasNext()){
	        	weights[0] = Float.valueOf(scanner.next());
	        	weights[1] = Float.valueOf(scanner.next());
	        	weights[2] = Float.valueOf(scanner.next());
	        }
	        scanner.close();
		} catch (FileNotFoundException e1) {
			System.out.println("using default values");
			// do nothing use standards
		}		
	}

	public static void save() {
		try {
			PrintWriter pw = new PrintWriter(new File("weights.csv"));
	        StringBuilder sb = new StringBuilder();
	        sb.append(weights[0]);
	        sb.append(';');
	        sb.append(weights[1]);
	        sb.append(';');
	        sb.append(weights[2]);
	        sb.append(';');
	        sb.append('\n');
	        pw.write(sb.toString());
	        pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new AssertionError();
		}
	}
	
	/**
	 * //it = adaptedCands.iterator();
	        			/*
	        			int medianX=0, medianY=0, medianZ=0;
	        			for(Float[] cand : adaptedCands) {
	        				medianX += cand[0];
	        				medianY += cand[1];
	        				medianZ += cand[2];
	        			}
	        			Float amount = (float) adaptedCands.size();
	        			final Float X = (medianX/amount);
	        			final Float Y = medianY/amount;
	        			final Float Z = medianZ/amount;
	        			*/
}
