package Xbot;

import lenz.htw.sawhian.Server;

public class Tserver implements Runnable{
	int winner=-100;
	private volatile boolean exit = false;
	
	@Override
	public void run() { 
		while(!exit){
			System.out.println("Server is running....."); 
			System.out.println("Tserver started:");
			winner= Server.runOnceAndReturnTheWinner(2);
			//TestRun.winner = winner;
			Select.lock =false;
			System.out.println("\nTserver Winner:"+winner);
			exit = true;
		}
	}
	
	public int getWinner(){
		return winner;
	}
	
	public void stop(){
      
    }
}
