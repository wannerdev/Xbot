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

	float weight_x = 0.5f, weight_y = 0.25f, weight_z = 0.25f; // arbitrary default values
	private volatile boolean exit = false;
	// Laptop Aufl�sung server
	// java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native
	// -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	// Desktop
	// java
	// -Djava.library.path=D:\Wichtig\Programmieren\Java\Xbot\sawhian\lib\native
	// -jar D:\Wichtig\Programmieren\Java\Xbot\sawhian\sawhian.jar 4 1600 900

	public client(Float weight_x, Float weight_y, Float weight_z) {
		this.weight_x = weight_x;
		this.weight_y = weight_y;
		this.weight_z = weight_z;
	}

	public client() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while (!exit) {
			Board b = new Board();
			int myNumber = -1;
			try {
				String logoh = "cybran";
				BufferedImage logo = ImageIO.read(new File("src/Xbot/" + logoh + ".png"));
				Integer num = (int) (Math.random() * 50);
				NetworkClient client = new NetworkClient(null, "XBot" + num.toString() + "000", logo);
				myNumber = client.getMyPlayerNumber();
				System.out.println("Player:" + myNumber);

				GameTree tree = new GameTree();
				int x = 0, y = 0;
				while (true) {
					Move move = client.receiveMove(); // Man bekommt auch den eigenen Zug

					// problem when my next move is only possible by the player before me enabling()
					// a move
					// ich bin dran
					if (move == null) {
						System.out.println("Allmoves:" + b.calcFreeMoves(myNumber, b).toString());
						Move lastmove = null;
						if (myNumber == 0) {
							lastmove = tree.MultiMax(myNumber, b);
						} else {
							lastmove = tree.randomMove(myNumber, b);
						}
						if (lastmove == null || lastmove.y == -1)
							throw new Exception("Game over:" + b.getScores());
						client.sendMove(lastmove);
					} else {
						if (move.y == -1)
							throw new Exception("Game over:" + b.getScores());
						// baue Zug in meine spielfeldrepr�sentation ein
						b.makeMove(move);
						System.out.println("Anzahl steine: " + b.getStateConfig().ptr);
						System.out.println(b.getStateConfig().toString());
						System.out.println("MOVE: X = " + move.x + " || Y = " + move.y);
					}
				}
				// TODO recognize valid game end.
			} catch (RuntimeException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
				System.err.println("Player" + myNumber + " Runtime Excep: GameOver Scores:" + b.getScores());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Player" + myNumber + " Exception: \n" + e.getLocalizedMessage());
			}
		}
	}
	
	public void stop(){
        exit = true;
    }

}
