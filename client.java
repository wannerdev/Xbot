package Xbot;

import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import lenz.htw.sawhian.Move;
import lenz.htw.sawhian.net.*;

public class client {

	// java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native
	// -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	// Laptop auflösung server
	// Desktop
	// java -Djava.library.path=D:\Wichtig\Programmieren\Java\Xbot\sawhian\lib\native -jar D:\Wichtig\Programmieren\Java\Xbot\sawhian\sawhian.jar 4 1600 900
	
	// Man bekommt nur die position des steines nicht das Ziel da der Stein ja nur
	// in eine Richtung kann
	public static void main(String[] args) {
		try {

			String logoh = "cybran";
			BufferedImage logo = ImageIO.read(new File("src/Xbot/" +logoh + ".png"));
			Integer num = (int) (Math.random() * 50);
			NetworkClient client = new NetworkClient(null, "XBot" + num.toString() + "000", logo);
			int myNumber = client.getMyPlayerNumber();
			System.out.println("Player:" + myNumber);

			Board b = new Board();
			GameTree tree = new GameTree();
			int x = 0, y = 0;
			while (true) {
				Move move = client.receiveMove(); // Man bekommt auch den eigenen Zug
				System.out.println("Allmoves:" + b.calcFreeMoves(myNumber, b).toString());
				Move lastmove = tree.randomMove(myNumber, b); 
				if(myNumber ==0)tree.MultiMax(myNumber, b);
				
				// ich bin dran
				if (move == null) {
					client.sendMove(lastmove);
					// x++;
				} else {
					// baue Zug in meine spielfeldrepräsentation ein
					b.makeMove(move);
					System.out.println(" Anzahl steine: " + b.getStateConfig().ptr);

					System.out.println(b.getStateConfig().toString());
					System.out.println("MOVE: X = " + move.x + " || Y = " + move.y);
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
			System.err.println("Exception: \n" + e.getLocalizedMessage());
		}
	}

}
