package Xbot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import lenz.htw.sawhian.Move;
import lenz.htw.sawhian.net.*;

public class client implements Runnable {
	
	float[] weights={0.5f,0.25f,0.25f, Float.MIN_VALUE};;
	private volatile boolean exit = false;
	public boolean dead = false;
	String howtoplay="MaxN"; 
	// Laptop Auflï¿½sung server
	// java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native
	// -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	// Desktop
	// java
	// -Djava.library.path=D:\Wichtig\Programmieren\Java\Xbot\sawhian\lib\native
	// -jar D:\Wichtig\Programmieren\Java\Xbot\sawhian\sawhian.jar 4 1600 900

	public client(Float[] weights) {
		this.weights[0] = weights[0];
		this.weights[1] = weights[1];
		this.weights[2] = weights[2];
		this.weights[3] = weights[3];
		howtoplay= "MaxN";
	}

	/**
	 * 
	 * @param howtoplay takes either MaxN, random or firstMove, atm
	 */
	public client(String howtoplay) {
		this.howtoplay = this.howtoplay;
	}
	
	/**
	 * plays with MaxN
	 */
	public client() {
		this.howtoplay = "MaxN";
		SelectFillerAndIO fill = new SelectFillerAndIO();
		weights=fill.load();
		System.out.println("Playing with:");
		System.out.println("0:"+weights[0]+" 1:"+weights[1]+" 2:"+weights[2]+" 3"+weights[3]);
	}

	
	@Override
	public void run() {
        
		if (dead) {
			return;
		}
		long timeElapsed=0;
		Board b = new Board();

		try {
			String logoh = "cybran";
			BufferedImage logo = ImageIO.read(new File("src/Xbot/" + logoh + ".png"));
			Integer num = (int) (Math.random() * 50);
			NetworkClient client = new NetworkClient(null, "XBot" + num.toString() + "000", logo);
			int myNumber = client.getMyPlayerNumber();
			System.out.println("Player:" + myNumber);

			GameTree tree = new GameTree();
			int x = 0, y = 0;
			while (true) {
				Move move = client.receiveMove(); // Man bekommt auch den eigenen Zug

				// problem when my next move is only possible by the player before me
				// enabling(unblocking) a move
				// ich bin dran
				if (move == null) {
					long startTime = System.currentTimeMillis();
					//System.out.println("Allmoves:" + b.calcFreeMoves(myNumber, b).toString());
					Move lastmove = null;
					if(howtoplay.equals("MaxN")) {
						lastmove = tree.MultiMax(myNumber, b,weights);
						if (myNumber == 0) {
							lastmove = tree.MultiMax(myNumber, b,weights);
						}
					}else if(howtoplay.equals("random")){
						lastmove = tree.randomMove(myNumber, b);
					}else if(howtoplay.equals("firstMove")){
						lastmove = tree.calculateOneMove(myNumber, b);						
					}
					long endTime = System.currentTimeMillis();
					timeElapsed = endTime - startTime;
					client.sendMove(lastmove);
					// x++;
				} else {
					// baue Zug in meine spielfeldrepr�sentation ein
					b.makeMove(move);
					//System.out.println(" Anzahl steine: " + b.getStateConfig().ptr);

					//System.out.println(b.getStateConfig().toString());
					///System.out.println("MOVE: X = " + move.x + " || Y = " + move.y);
				}
			}
			// TODO recognize valid game end.
			// save();
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.err.println("Zulange gebraucht?:"+timeElapsed);
			System.err.println("Maybe GameOver Scores:" + b.getScores());
		} catch (Exception e) {

			e.printStackTrace();
			System.err.println("Exception: \n" + e.getLocalizedMessage());
		}
	}

	public void stop() {
		exit = true;
		dead = true;
	}

}
