package Xbot;

import lenz.htw.sawhian.Server;

public class TestRun {

	static int winner = -100;
    static int count =0 ;
	public static void main(String[] args) {
		int q[] = new int[10]; // the quality values of the subjects
		try {
			while (true) {
				System.out.println("running");
				Thread tsrv;
				Tserver srv = new Tserver();
				tsrv = new Thread(srv, "Server");
				tsrv.start();
				Thread clientThreads[] = new Thread[4];
				client clients[] = new client[4];
				for(int i = 0; i < 4 ; i++) {
					clients[i] = new client();
					clientThreads[i] = new Thread(clients[i], "client"+i);// 0
					clientThreads[i].start();
				}
				
				System.out.println("Winner?:" + winner);
				int aliveCount = 4;
				while (winner == -100) {
					
                    for(int i = 0; i < 4 ; i++) {
                    	clientThreads[i].isAlive();            	
                    }
				};
				System.out.println(tsrv.getName());
				System.out.println("Winner:" + winner);
			
				System.out.println("Count:" + count);
				
				boolean flag = true;
				srv.stop();
				long jetzt = System.currentTimeMillis();
        		while(System.currentTimeMillis() > jetzt +2000);//warte 2sek
				count++;
				winner = -100;
				while(count>2);
					
			

			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
