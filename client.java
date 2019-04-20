package Xbot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import lenz.htw.sawhian.Move;
import lenz.htw.sawhian.net.*;

public class Client {

	//java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	//Laptop auflösung server
	//Man bekommt nur die position des steines nicht das Ziel da der Stein ja nur in eine Richtung kann
	public static void main (String[] args) {		
		try {
			
			String logoh ="cybran";			
			BufferedImage logo = ImageIO.read(new File("src/xbot/"+logoh+".png"));
			Integer num = (int) (Math.random()*10);
	        NetworkClient client = new NetworkClient(null, "XBot"+num.toString()+"000", logo);
	        int myNumber = client.getMyPlayerNumber();
        	int x=0, y=0;
        	Board b = new Board();
        	while(true) {
	            Move move = client.receiveMove(); //Man bekommt auch den eigenen Zug
	            if (move == null) {
	                //ich bin dran
	            	Move lastmove = new Move(client.getMyPlayerNumber(), x,y);
	            	if(myNumber ==0) {
	            		//if(Board.isValidMove(lastmove, b )) {
	            			client.sendMove(lastmove);
	            		//}
	            	}else {
		                client.sendMove(KoordHelper.rotate((byte)myNumber, lastmove));
	            	}
	                //baue Zug in meine spielfeldrepräsentation ein
	            	b.addMoveToBoard(lastmove);
	            	if(x == 5)x=0;
	            	x++;
	            }
        	}
    	}catch(Exception e) {
	    		System.out.println(e.getLocalizedMessage());
    	}
	}
	
}
