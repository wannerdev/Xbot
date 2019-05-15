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

public class Select {
	static float weight_x = 0.5f, weight_y = 0.25f, weight_z = 0.25f; //arbitrary default values
	//Not WORKING ATM
	
	public static void main(String[] args) {			
		load();		
		
		//Candidates
		//Set<Integer[]> mutatedCand = new HashSet<Integer[]>();
		Set<Float[]> candidates = new HashSet<Float[]>();
		Set<Float[]> adaptedCands = new HashSet<Float[]>();
		adaptedCands.add(new Float[] {(float) ((int)Math.random()*100),(float) ((int)Math.random()*100),(float) ((int)Math.random()*100)});
		adaptedCands.add(new Float[] {(float) ((int)Math.random()*100),(float) ((int)Math.random()*100),(float) ((int)Math.random()*100)});
		//just for the while loop
		
		for(int i=0; i < 9; i++) {
			candidates.add(new Float[] {(float) ((int)Math.random()*100),(float) ((int)Math.random()*100),(float) ((int)Math.random()*100)});
		}
		Iterator<Float[]> it = candidates.iterator();
		//server
		Process proc = null;
        while(true) {
        	//clients
        	Thread t1;
    		Thread t2;
    		Thread t3;
    		Thread t4;
    		//evaluation
    		int counter = 0;
    		String again = "y";
        	while( it.hasNext() && adaptedCands.size() != 1 && again!="n"){		
	        		try {
	        			// Run server in a separate system process
						proc = Runtime.getRuntime().exec(""
								+ "java -Djava.library.path=D:\\Wichtig\\Programmieren\\Java\\Xbot\\sawhian\\lib\\native -jar D:\\Wichtig\\Programmieren\\Java\\Xbot\\sawhian\\sawhian.jar 4 1600 900\r\n" + 
								"	 8 noanim");
						System.out.println("running");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						throw new AssertionError();
					}
	        		Float[] cand1 = it.next();
	        		if(it.hasNext() == false)break;
	        		Float[] cand2 = it.next();
	        		if(it.hasNext() == false)break;
	        		Float[] cand3 = it.next();
	        		
	        		client[] gc = new client[4];
	        		gc[0] = new client(cand1[0],cand1[1],cand1[2]);
	        		gc[1] = new client(cand2[0],cand2[1],cand2[2]);
	        		gc[2] = new client(cand3[0],cand3[1],cand3[2]);
	        		gc[3] = new client(cand3[0],cand3[1],cand3[2]);
	
	        		t1 = new Thread(gc[0]);
	        		t2 = new Thread(gc[1]);
	        		t3 = new Thread(gc[2]);
	        		t4 = new Thread(gc[3]);
	        		
	        		t1.start();
	        		t2.start();
	        		t3.start();
	        		t4.start();

	        		long jetzt = System.currentTimeMillis();
	        		while(System.currentTimeMillis() > jetzt +2000);
					
	        		while(t1.isAlive() || t2.isAlive() || t3.isAlive()|| t4.isAlive()); //wait till game over //
	        		//System.out.println("P:");
	        		//proc.
					int sel = 0;					
	        		//TODO who won
	        		//give client a board object? 
	        		adaptedCands.add(new Float[] {gc[sel].weight_x, gc[sel].weight_y, gc[sel].weight_z});
	        		//System.out.println(); return value of server?
	        		//while(proc.isAlive());
	        		proc.destroyForcibly();
	        		
	        		counter++;
	        		
	        		//all candidates played at least once
	        		if(counter == 6) {
	        			//recombination
	        			//it = adaptedCands.iterator();
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
	        			//adaptedCands.forEach((Integer[] cand)-> X++);
	        			int j=0, l=0, k=0;
	        			
	        			candidates.removeAll(candidates);//loosers die
	        			while( it.hasNext() ) { //mutate
	        				Float[] candi = it.next();
	        				candi[0]= X + j;
	        				candi[1]= Y + k;
	        				candi[2]= Z + l;
	        				j++;
	        				if(j%2 ==0) {
	        					j=j*-1;
	        				}
	        				k++;
	        				if(k%2 !=0) {
	        					k=k*-1;
	        				}
	        				l++;
	        				if(l%2 ==0) {
	        					l=l*-1;
	        				}
	        				candidates.add(candi); 
	        			}
	        			//refill candidates and use one winner as test?
	        			System.out.println("Anothertime? y/n");
	        			Scanner in = new Scanner(System.in);

	        			int i = in.nextInt();
	        			again = in.next();
	        			if (again.equals("n"))break;
	        		}
	        	}
        	}
        }
	
	private static void load() {
		Scanner scanner=null;
		try {
			
			scanner = new Scanner(new File("weights.csv"));
	        scanner.useDelimiter(";");
	        while(scanner.hasNext()){
	            weight_x = Integer.valueOf(scanner.next());
	            weight_y = Integer.valueOf(scanner.next());
	            weight_z = Integer.valueOf(scanner.next());
	        }
	        scanner.close();
		} catch (FileNotFoundException e1) {
			// do nothing use standards
		}		
	}

	public static void save() {
		try {
			PrintWriter pw = new PrintWriter(new File("weights.csv"));
	        StringBuilder sb = new StringBuilder();
	        sb.append(weight_x);
	        sb.append(';');
	        sb.append(weight_y);
	        sb.append(';');
	        sb.append(weight_z);
	        sb.append(';');
	        sb.append('\n');
	        pw.write(sb.toString());
	        pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new AssertionError();
		}
	}
}
