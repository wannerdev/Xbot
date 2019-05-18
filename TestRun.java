package Xbot;


import lenz.htw.sawhian.Server;

public class TestRun {
	
	static int winner=-1;

	public static void main(String[] args) {
		int q[] = new int[10]; //the quality values of the subjects
        try {
        	while(true) {
				// Run server in a separate system process
				System.out.println("running");
				Tserver srv = new Tserver();
				Thread thread = new Thread(srv, "server");
				thread.start();
				Thread client0 = new Thread(new client(), "client0");//0
				Thread client1 = new Thread(new client(), "client1");//1
				Thread client2 = new Thread(new client(), "client2");//2
				Thread client3 = new Thread(new client(), "client3");//3
				client0.start();
				client1.start();
				client2.start();
				client3.start();
				System.out.println("Winner?:"+winner);
				while(winner == -1);
				System.out.println(thread.getName());
				System.out.println("Winner:"+winner);
				q[winner]++;
				System.out.println("server up");
				
				//start 4threads mit den ersten vier subjects
				
				//increase q of winner and decrease q of loosers
				
        	}
			/*
			client[] gc = new client[3];
			gc[0] = new client(cand1[0],cand1[1],cand1[2]);
			gc[1] = new client(cand2[0],cand2[1],cand2[2]);
			gc[2] = new client(cand3[0],cand3[1],cand3[2]);
	
			t1 = new Thread(gc[0]);
			t2 = new Thread(gc[1]);
			t3 = new Thread(gc[2]);
			
			t1.start();
			t2.start();
    		t3.start();*/
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
