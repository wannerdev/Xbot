package Xbot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import lenz.htw.sawhian.Move;
import lenz.htw.sawhian.net.*;

public class client {

	//java -Djava.library.path=C:\Users\Johannes\Dropbox\Java\sawhian\lib\native -jar C:\Users\Johannes\Dropbox\Java\sawhian\sawhian.jar 4 1600 900
	//Laptop auflösung server
	
	
	//Man bekommt nur die position des steines nicht das Ziel da der Stein ja nur in eine Richtung kann
	public static void main (String[] args) {		
		try {
			
			String logoh ="cybran";			
			BufferedImage logo = ImageIO.read(new File("xbot/"+logoh+".png"));
			Integer num = (int) (Math.random()*50);
	        NetworkClient client = new NetworkClient(null, "XBot"+num.toString()+"000", logo);
	        int myNumber = client.getMyPlayerNumber();
	        System.out.println(myNumber);
        
        	Board b = new Board();
        	GameTree tree = new GameTree();
        	while(true) {
	            Move move = client.receiveMove(); //Man bekommt auch den eigenen Zug
	        	Move lastmove =  tree.bestMove(myNumber, b);
                //ich bin dran
	            if (move == null) {
	            	if(myNumber == 0) {
	            		//if(Board.isValidMove(lastmove, b )) {
	            		    //lastmove =  new Move(0,5,myNumber);
	            			client.sendMove(lastmove);
	            			
	            		//}
	            	}else {
	            		// ansonsten rotiere das spielbrett f�r den entsprechenden Spieler       		
	            		lastmove = KoordHelper.rotate((byte)myNumber, lastmove);
		                client.sendMove(lastmove);
	            	}

	            }else {
	                //baue Zug in meine spielfeldrepräsentation ein
	            }
	            //lastmove.
            	b.makeMove(lastmove);

        	}
    	}catch(Exception e) {
	    		System.out.println("Something killed it \n"+e.getLocalizedMessage());
    	}
	}
	
}
