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

	float weight_x , weight_y , weight_z; // arbitrary default values
	float[] weights;
	private volatile boolean exit = false;
	public boolean dead = false;
	// Laptop Auflï¿½sung server
	// java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native
	// -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	// Desktop
	// java
	// -Djava.library.path=D:\Wichtig\Programmieren\Java\Xbot\sawhian\lib\native
	// -jar D:\Wichtig\Programmieren\Java\Xbot\sawhian\sawhian.jar 4 1600 900

	public client(Float weight_x, Float weight_y, Float weight_z) {
		weights = new float[3];
		weights[0] = weight_x;
		weights[1] = weight_y;
		weights[2] = weight_z;
	}

	//TODO Check loading weights

	public client() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
        
		if (dead) {
			return;
		}
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
					//System.out.println("Allmoves:" + b.calcFreeMoves(myNumber, b).toString());
					Move lastmove = tree.MultiMax(myNumber, b,weights);
					if (myNumber == 0) {
						lastmove = tree.MultiMax(myNumber, b,weights);

					}
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
