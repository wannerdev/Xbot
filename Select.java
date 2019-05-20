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
	// static float weight_x = 0.5f, weight_y = 0.25f, weight_z = 0.25f; +quality
	// //arbitrary default values
	static Float[] weights = { 0.5f, 0.25f, 0.25f, 0f };
	static boolean lock = true;
	// Not WORKING ATM

	public static void main(String[] args) {			
    	load();		//from filesystem
		
		//set base quality
		int quali[]= {0,0,0,0,0,0,0,0,0,0,0,0};
		//Candidates
		Set<Float[]> candidates = new HashSet<Float[]>();
		Set<Float[]> adaptedCands = new HashSet<Float[]>();
		adaptedCands.add(Select.weights);
    	candidates = firstfill(candidates);
    	int counter = 0;

		try {						
        while(true) {

    		Iterator<Float[]> it = candidates.iterator();
    
        	//Server 
        	Thread tsrv;
        	//clients
        	Thread t1;
    		Thread t2;
    		Thread t3;
    		Thread t4;
    		//evaluation
    	
    		String again = "y";
        	while( it.hasNext() && adaptedCands.size() != 0 && again!="n"){
        			Tserver srv= new Tserver();
	        		tsrv = new Thread (srv,"Server");
	        		tsrv.start();
	        		
	        		Float[] cand1 = it.next();
	        		if(it.hasNext() == false)throw new Exception("no more cands");					
	        		Float[] cand2 = it.next();
	        		if(it.hasNext() == false)throw new Exception("no more cands");
	        		Float[] cand3 = it.next();
	        		if(it.hasNext() == false)throw new Exception("no more cands");
	        		Float[] cand4 = it.next();
	        		
	        		client[] gc = new client[4];
	        		gc[0] = new client(cand1);
	        		gc[1] = new client(cand2);
	        		gc[2] = new client(cand3);
	        		gc[3] = new client(cand4);
	                
	        		Thread[] clientThread = new Thread[12];
	        		
	        		for(int i=0; i< 4*counter+1;i++) {
	        			clientThread[i] = new Thread(gc[i]);
	        			clientThread[i].start();
						long past = System.currentTimeMillis();
		        		while(System.currentTimeMillis() > past +400);//to make sure they connect in correct order
	        		}
	        		while (lock) {						
	                    for(int i = 0; i < 4 ; i++) {
	                    	clientThread[i].isAlive(); //Magic           	
	                    }
					};
					int win = srv.getWinner();
					int sel =-1;
					if(win >= 0) {
						sel= srv.getWinner()-1;//player starts at 1
					}
					System.out.println("Getting winner player:"+win);
					srv.stop();
					srv=null;
					if(sel >=0) {//valid significant game
						System.out.println("winner is :"+sel);
						Float[] winner = new Float[] {gc[sel].weights[0], gc[sel].weights[1], gc[sel].weights[2], weights[3]}; 
		        		if(!adaptedCands.add(winner)) {
		        			//if already in set increase quality
		        			winner[3]++;
		        			//quali[(sel+1)*(counter+1)]=quali[sel*(counter+1)]+1;
		        		}
						long past = System.currentTimeMillis();
		        		while(System.currentTimeMillis() > past +2000);//warte 2sek TODO test if needed
					}
					counter++;
	        		lock = true;
	        		/*Stop threads*/
	        		
	        		//all candidates played at least once
	        		System.out.println("COUNTER :: "+counter);
	        		if(counter == 3) {
	        			counter=0;
	        		
	        			//recombination
	        			candidates.removeAll(candidates);//loosers die
	        			it = adaptedCands.iterator();
	        			while( it.hasNext() ) { //mutate //should be 4 3 winners one loaded(default or parent)
	        				Float[] candi = it.next();
	        				//Range ist um den wert herum maximal einfach +- 0.2
	        				candi[0] = (float) ((float) candi[0]+((0.02)*Math.random()-0.01*Math.random()));
	        				if (candi[0]<0) {
	        					candi[0] = 0f;
	        				}
	        				if (candi[0]>1) {
	        					candi[0] = 1f;
	        				}
	        				candi[1]= (float) ((float) candi[1]+((0.02)*Math.random()-0.01*Math.random()));
	        				if (candi[1]<0) {
	        					candi[1] = 0f;
	        				}
	        				if (candi[1]>1) {
	        					candi[1] = 1f;
	        				}
	        				candi[2]= (float) ((float) candi[2]+((0.02)*Math.random()-0.01*Math.random()));
	        				if (candi[2]<0) {
	        					candi[2] = 0f;
	        				}
	        				if (candi[2]>1) {
	        					candi[2] = 1f;
	        				}
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
        } catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AssertionError();
		}
        }

	private static void getBest(Set<Float[]> adaptedCands) {
		Float[] cache = { 0f, 0f, 0f, 0f };
		for (Float[] win : adaptedCands) {
			if (win[3] > cache[3]) {
				cache = win;
			}
		}
		Select.weights = cache;
	}

	/**
	 * create 12 cands 0,1 -0,9 and some completely random
	 * @param cands
	 * @return
	 */
	private static Set<Float[]> firstfill(Set<Float[]> cands) {
		for (int i = 1; i < 10; i++) {
			cands.add(new Float[] { i/10f,i/10f,i/10f, 0f });
		}
		cands.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
				(float) ((float)  Math.random()), 0f });
		cands.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
				(float) ((float)  Math.random()), 0f });
		cands.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
				(float) ((float)  Math.random()), 0f });
		return cands;
	}

	private static Set<Float[]> refill(Set<Float[]> winners) {
		// winners should be max 4
		for (int i = 0; i < 12 - winners.size(); i++) {
			winners.add(new Float[] { (float) (Math.random()), (float) ((float) (Math.random())),
					(float) ((float)  Math.random()), 0f });
		}
		return winners;
	}

	public static Float[] load() {
		Scanner scanner = null;
		try {

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

	public static void save() {
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
			// TODO Auto-generated catch block
			throw new AssertionError();
		}
	}
}
