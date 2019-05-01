package Xbot;

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
	// java -Djava.library.path=F:\downloads\Dropbox\Java\sawhian\lib\native -jar
	// F:\downloads\Dropbox\Java\sawhian\sawhian.jar 4 1600 900

	// Man bekommt nur die position des steines nicht das Ziel da der Stein ja nur
	// in eine Richtung kann
	public static void main(String[] args) {
		try {

			String logoh = "cybran";
			BufferedImage logo = ImageIO.read(new File("src/xbot/" + logoh + ".png"));
			Integer num = (int) (Math.random() * 50);
			NetworkClient client = new NetworkClient(null, "XBot" + num.toString() + "000", logo);
			int myNumber = client.getMyPlayerNumber();
			System.out.println("Player:" + myNumber);

			Board b = new Board();
			GameTree tree = new GameTree();
			int x = 0, y = 0;
			while (true) {
				Move move = client.receiveMove(); // Man bekommt auch den eigenen Zug
				Move lastmove = new Move(myNumber, x, y);// tree.bestMove(myNumber, b); //calculateOneMove(myNumber, b);
				// ich bin dran
				if (move == null) {
					if (myNumber != 0) {

						lastmove = KoordHelper.rotate(myNumber, lastmove);
					}
					if (Board.isValidMove(lastmove, b)) {
						
						client.sendMove(lastmove);
					}else {
						System.out.println("MY MOVE: X = " + lastmove.x + " || Y = " + lastmove.y);
						System.out.println("MY MOVE IS INVALID");
					}
					
					 x++;
				} else {
					// baue Zug in meine spielfeldrepräsentation ein
					b.makeMove(move);
					System.out.println(" Anzahl steine: " + b.stateConfig.ptr);
	            	System.out.println("Allmoves:"+b.calcFreeMoves(myNumber, b.stateConfig).toString());
					System.out.println(b.stateConfig.toString());
					System.out.println("MOVE: X = " + move.x + " || Y = " + move.y);

				}
			}
		} catch (Exception e) {
			System.err.println("Exception: \n" + e.getLocalizedMessage());
		}

	}

}
