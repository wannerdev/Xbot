package Xbot;

import java.util.concurrent.TimeUnit;

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
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				throw new AssertionError();
			}// to make sure they get the winner
			exit = true;
		}
	}
	
	public int getWinner(){
		return winner;
	}
	
	public void stop(){
      
    }
}
