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
	//static float weight_x = 0.5f, weight_y = 0.25f, weight_z = 0.25f; +quality //arbitrary default values
	static Float[] weights= {0.5f,0.25f,0.25f, 0f};
	static boolean lock = true;
	//Not WORKING ATM
	
	public static void main(String[] args) {			
    	load();		//from filesystem
		
		//set base quality
		int quali[]= {0,0,0,0,0,0,0,0,0,0,0,0};
		//Candidates
		Set<Float[]> candidates = new HashSet<Float[]>();
		Set<Float[]> adaptedCands = new HashSet<Float[]>();
		adaptedCands.add(Select.weights);
    	candidates = firstfill(candidates);
		

		
        while(true) {

    		Iterator<Float[]> it = candidates.iterator();
        	System.out.println(adaptedCands.size());
        	System.out.println(it.hasNext());
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
	                
	        		Thread[] clientThread = new Thread[4];
	        		clientThread[0] = new Thread(gc[0]);
	        		clientThread[1] = new Thread(gc[1]);
	        		clientThread[2] = new Thread(gc[2]);
	        		clientThread[3] = new Thread(gc[3]);
	        		
	        		clientThread[0].start();
	        		clientThread[1].start();
	        		clientThread[2].start();
	        		clientThread[3].start();

	        		
	        		while (lock) {
						
	                    for(int i = 0; i < 4 ; i++) {
	                    	clientThread[i].isAlive();            	
	                    }
					};
					int sel = 0;
					System.out.println("getting winner");
	        		//TODO who won
					sel = (srv.getWinner()<0)?  srv.getWinner(): srv.getWinner()-1;
					srv.stop();
					if(sel >0) {//valid significant game
						System.out.println("getting winner:"+sel);
						long past = System.currentTimeMillis();
		        		while(System.currentTimeMillis() > past +2000);//warte 2sek
						Float[] winner = new Float[] {gc[sel].weight_x, gc[sel].weight_y, gc[sel].weight_z,0f}; 
		        		if(!adaptedCands.add(winner)) {
		        			//if already in set increase quality
		        			winner[3]++;
		        			//quali[(sel+1)*(counter+1)]=quali[sel*(counter+1)]+1;
		        		}
		        		//System.out.println(); return value of server?	        		
		        		
					}
					counter++;
	        		lock = true;
	        		/*Stop threads
	        		gc[0].stop();
	        		gc[1].stop();
	        		gc[2].stop();
	        		gc[3].stop();*/
	        		
	        		//all candidates played at least once
	        		if(counter == 3) {
	        			counter=0;
	        		
	        			//recombination
	        			candidates.removeAll(candidates);//loosers die
	        			it = adaptedCands.iterator();
	        			while( it.hasNext() ) { //mutate //should be 4 3 winners one loaded(default or parent)
	        				Float[] candi = it.next();
	        				//Range ist um den wert herum maximal einfach +- 0.2
	        				candi[0] = (float) ((float) candi[0]+((0.2)*Math.random()-0.2*Math.random()));
	        				candi[1]= (float) ((float) candi[1]+((0.2)*Math.random()-0.2*Math.random()));
	        				candi[2]= (float) ((float) candi[2]+((0.2)*Math.random()-0.2*Math.random()));
	        				candi[3]= 1f; //qualität 1 da mutated winner
	        				
	        				candidates.add(candi); //refill candidates with mutated winners  
	        			}
	        			getBest(adaptedCands);
	        			candidates.addAll(adaptedCands); //fill with real winners
	        			candidates=refill(candidates);
	        			
	        	        save();
	        			/*System.out.println("Anothertime? y/n");
	        			Scanner in = new Scanner(System.in);
	        			
	        			again = in.next();
	        			if (again.equals("n"))break;
	        			/*byte b[]= new byte[30] ;
	        			try {
							System.in.read(b);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
	        		}
	        	}
        	}
        }
	
	private static void getBest(Set<Float[]> adaptedCands) {
		Float[] cache= {0f,0f,0f,0f};
		for(Float[] win : adaptedCands) {
			if(win[3]> cache[3]) {
				cache= win;
			}
		}
		Select.weights= cache;
	}

	private static Set<Float[]> firstfill(Set<Float[]> cands) {
		for(int i=0; i < 12; i++) {
			cands.add(new Float[] {(float) (0.01f+Math.random()),(float) ((float) 0.01f+(Math.random())),(float) ((float) 0.01f+Math.random()),0f});
		}
		return cands;
	}
	
	private static Set<Float[]> refill(Set<Float[]> winners) {
		//winners should be max 4
		for(int i=0; i < 12-winners.size(); i++) {
			winners.add(new Float[] {(float) (0.01f+Math.random()),(float) (0.01f+Math.random()),(float) (0.01f+Math.random()),0f});
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
	        	weights[3] = Float.valueOf(scanner.next());
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
	        sb.append(weights[3]);
	        sb.append(';');
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
