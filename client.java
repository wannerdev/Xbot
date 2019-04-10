package Xbot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import lenz.htw.sawhian.Move;
import lenz.htw.sawhian.net.*;

public class client {

	//Man bekommt nur die position des steines nicht das Ziel da der Stein ja nur in eine Richtung kann
	public static void main (String[] args) {		
		try {
			
			String logoh ="cybran";			
			BufferedImage logo = ImageIO.read(new File("res/"+logoh+".png"));
			Integer num = (int) (Math.random()*10);
	        NetworkClient client = new NetworkClient(null, "XBot"+num.toString()+"000", logo);
	    	
	        client.getMyPlayerNumber();        
	        client.getTimeLimitInSeconds();        
	        client.getExpectedNetworkLatencyInMilliseconds();
	        
        	while(true) {
	            Move move = client.receiveMove(); //Man bekommt auch den eigenen Zug
	            if (move == null) {
	                //ich bin dran
	            	if(client.getMyPlayerNumber() == 1) {

		                client.sendMove(new Move(client.getMyPlayerNumber(), 1, 6));
	            	}
	            	if(client.getMyPlayerNumber() == 2) {

		                client.sendMove(new Move(client.getMyPlayerNumber(), 6, 6));
	            	}
	            	if(client.getMyPlayerNumber() == 3) {

		                client.sendMove(new Move(client.getMyPlayerNumber(), 6, 1));
	            	}
	                client.sendMove(new Move(client.getMyPlayerNumber(),1, 1));
	            } else {
	                //baue Zug in meine spielfeldrepräsentation ein
	            }
        	}
    	}catch(Exception e) {
	    		System.out.println(e.getLocalizedMessage());
    	}
	}
	
}
