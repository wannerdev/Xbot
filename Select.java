package Xbot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class Select {

	// arbitrary default values
	static Float[] weights = { 0.5f, 0.25f, 0.25f, 0f };
	static boolean lock = true;

	public static void main(String[] args) {
		SelectFillerAndIO fill =  new SelectFillerAndIO();
		float[]loaded = fill.load();
		if(loaded!=null) {
		weights[0]=loaded[0];
    	weights[1]=loaded[1];
		weights[2]=loaded[2];
		weights[3]=loaded[3];
		}
    	//		//from filesystem
		
		//set base quality 0
		//Candidates
		Set<Float[]> candidates = new HashSet<Float[]>();
		Set<Float[]> adaptedCands = new HashSet<Float[]>();
		adaptedCands.add(Select.weights);
		candidates.add(Select.weights);
    	candidates = fill.firstfill(candidates);
    	
    	int counter = 0;
								
        while(true) {

    		Iterator<Float[]> it = candidates.iterator();
    
        	//Server  
        	Thread tsrv;
    		
        	//evaluation
    		String again = "y";
    		//System.out.println(it.hasNext()+" " +"ad"+ adaptedCands.size() );
    		try {
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
	                
	        		Thread[] clientThread = new Thread[4];
	        		
	        		for(int i=0; i< 4;i++) {
	        			clientThread[i] = new Thread(gc[i]);
	        			clientThread[i].start();
						long past = System.currentTimeMillis();
						TimeUnit.SECONDS.sleep(2);//to make sure they connect in correct order
	        		}
	        		while (lock) {
	                    for(int i = 0; i < 4 ; i++) {
	                    	if(clientThread[i]!=null) {
	                    	clientThread[i].isAlive(); //Magic
	                    	}
	                    }
	                    TimeUnit.SECONDS.sleep(2);
	        			//System.out.println("locked");
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
						System.out.println("getting winner:"+sel);
						long past = System.currentTimeMillis();
		        		while(System.currentTimeMillis() > past +2000);//warte 2sek
						System.out.println("winner is :"+sel);
						Float[] winner = new Float[] {gc[sel].weights[0], gc[sel].weights[1], gc[sel].weights[2], weights[3]}; 
		        		//increase quality, should there be a winner shose quality shouldn't be increased?
		        		winner[3]++;	
	        			System.out.println("0:"+gc[sel].weights[0]+" 1:"+gc[sel].weights[1]+" 2:"+gc[sel].weights[2]+" 3:"+gc[sel].weights[3]);
						adaptedCands.add(winner);
					}
					counter++;
	        		lock = true;
	        		/*Stop threads*/
	        		for(int i = 0; i < 4 ; i++) {
                    	if(clientThread[i]!=null) {
                    	clientThread[i].stop(); //Magic
                    	}
                    }
	        		//all candidates played at least once
	        		System.out.println("COUNTER :: "+counter);
	        		if(counter == 10) {
	        			counter=0;
	        		
	        			//recombination
	        			candidates.removeAll(candidates);//loosers die
	        			it = adaptedCands.iterator();
	        			while( it.hasNext() ) { //mutate //should be 4 3 winners one loaded(default or parent)
	        				Float[] candi = it.next();
	        				//Range ist um den wert herum maximal einfach +- 0.2
	        				if(candi[3]>=1f) {//If winner (all should be winners)
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
		        				candi[3]= 0f; //qualität 0 da mutated winner
		        				candidates.add(candi); //add mutated winners to candidates  
	        				} 
	        			}
	        			candidates.add(fill.getBest(adaptedCands)); //Add Best rated Parent without changing	        			
	        			
	        			candidates.addAll(adaptedCands); //fill with real winners
	        			candidates = fill.refill(candidates);
	        			weights =fill.getBest(adaptedCands);
	        	        fill.save(weights);
	        	        System.out.println("=================Saving=================");
						System.out.println("0:"+weights[0]+" 1:"+weights[1]+" 2:"+weights[2]+" 3:"+weights[3]);
	        			/*System.out.println("Anothertime? y/n");
	        			Scanner in = new Scanner(System.in);
	        			
	        			again = in.next();
	        			if (again.equals("n"))break;
	        			/*byte b[]= new byte[30] ;
	        			try {
							System.in.read(b);
						} catch (IOException e) {
							// 
							e.printStackTrace();
						}*/
	        		}
	        	}
		        } catch (Exception e) {
					// kill
		        	e.printStackTrace();
		        	System.err.println(e.getLocalizedMessage());
		        	System.out.println("Select.main()");
					//throw new AssertionError();
		        	fill.refill(adaptedCands);
				}
        
        	}
        
        }
		
}
