package Xbot;

import lenz.htw.sawhian.Server;

public class TestRun {

	static int winner = -1;

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
				/*clientThreads[0] = new Thread(new client(), "client0");// 0
				clientThreads[1]  = new Thread(new client(), "client1");// 1
				clientThreads[2]  = new Thread(new client(), "client2");// 2
				clientThreads[3]  = new Thread(new client(), "client3");// 3
				clientThreads[0].start();
				clientThreads[1].start();
				clientThreads[2].start();
				clientThreads[3].start();*/
				System.out.println("Winner?:" + winner);
				int aliveCount = 4;
				while (aliveCount>0 && winner == -1) {
					aliveCount = 4;
                    for(int i = 0; i < 4 ; i++) {
                    	
                    	if (!clientThreads[i].isAlive()) {
                    		
                    		aliveCount--;
                    	}
                    }
					winner = srv.getWinner();

				};
				System.out.println(tsrv.getName());
				System.out.println("Winner:" + winner);
				boolean flag = true;
				for(int i = 0; i < 4 ; i++) {
				
					clients[i].stop();
				}
				srv.stop();
				while (flag);
					
				// q[winner]++;

				// start 4threads mit den ersten vier subjects

				// increase q of winner and decrease q of loosers

			}
			/*
			 * client[] gc = new client[3]; gc[0] = new client(cand1[0],cand1[1],cand1[2]);
			 * gc[1] = new client(cand2[0],cand2[1],cand2[2]); gc[2] = new
			 * client(cand3[0],cand3[1],cand3[2]);
			 * 
			 * t1 = new Thread(gc[0]); t2 = new Thread(gc[1]); t3 = new Thread(gc[2]);
			 * 
			 * t1.start(); t2.start(); t3.start();
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
