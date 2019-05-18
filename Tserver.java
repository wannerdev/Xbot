package Xbot;

import lenz.htw.sawhian.Server;

public class Tserver implements Runnable{
	
	@Override
	public void run(){
		System.out.println("Tserver:");
		int win = Server.runOnceAndReturnTheWinner(2);
		TestRun.winner = win;
		System.out.println("Winner:"+win);
	}
}
