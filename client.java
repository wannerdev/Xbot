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

public class client {

	static float weight_x = 0.5f, weight_y = 0.25f, weight_z = 0.25f; //arbitrary default values

	// java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native
	// -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	// Laptop aufl√∂sung server
	// Desktop
	// java -Djava.library.path=D:\Wichtig\Programmieren\Java\Xbot\sawhian\lib\native -jar D:\Wichtig\Programmieren\Java\Xbot\sawhian\sawhian.jar 4 1600 900
	
	// Man bekommt nur die position des steines nicht das Ziel da der Stein ja nur
	// in eine Richtung kann
	public static void main(String[] args) {
			load();
			Board b = new Board();
		try {
			String logoh = "cybran";
			BufferedImage logo = ImageIO.read(new File("src/Xbot/" +logoh + ".png"));
			Integer num = (int) (Math.random() * 50);
			NetworkClient client = new NetworkClient(null, "XBot" + num.toString() + "000", logo);
			int myNumber = client.getMyPlayerNumber();
			System.out.println("Player:" + myNumber);

			GameTree tree = new GameTree();
			int x = 0, y = 0;
			while (true) {
				Move move = client.receiveMove(); // Man bekommt auch den eigenen Zug
				System.out.println("Allmoves:" + b.calcFreeMoves(myNumber, b).toString());
				
				//problem when  my next move is only possible by the player before me enabling(unblocking) a move
				// ich bin dran
				if (move == null) {
					Move lastmove = tree.randomMove(myNumber, b);
					if(myNumber == 0) {
						lastmove  = tree.MultiMax(myNumber, b);
					}
					client.sendMove(lastmove);
					// x++;
				} else {
					// baue Zug in meine spielfeldrepr‰sentation ein
					b.makeMove(move);
					System.out.println(" Anzahl steine: " + b.getStateConfig().ptr);

					System.out.println(b.getStateConfig().toString());
					System.out.println("MOVE: X = " + move.x + " || Y = " + move.y);
				}
			}
			//TODO recognize valid game end.
			//save();
		}catch (RuntimeException e) {
			e.printStackTrace();			
			System.err.println("Maybe GameOver Scores:"+b.getScores());
		} catch (Exception e) {
			save();
			e.printStackTrace();
			System.err.println("Exception: \n" + e.getLocalizedMessage());
		}
	}

	

	private static void load() {
		Scanner scanner=null;
		try {
			
			scanner = new Scanner(new File("weights.csv"));
	        scanner.useDelimiter(";");
	        while(scanner.hasNext()){
	            weight_x = Integer.valueOf(scanner.next());
	            weight_y = Integer.valueOf(scanner.next());
	            weight_z = Integer.valueOf(scanner.next());
	        }
	        scanner.close();
		} catch (FileNotFoundException e1) {
			// do nothing use standards
		}		
	}

	public static void save() {
		try {
			PrintWriter pw = new PrintWriter(new File("weights.csv"));
	        StringBuilder sb = new StringBuilder();
	        sb.append(weight_x);
	        sb.append(';');
	        sb.append(weight_y);
	        sb.append(';');
	        sb.append(weight_z);
	        sb.append(';');
	        sb.append('\n');
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new AssertionError();
		}
	}
}
