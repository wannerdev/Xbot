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
	    	
	        //client.getMyPlayerNumber();        
	        //client.getTimeLimitInSeconds();        
	        //client.getExpectedNetworkLatencyInMilliseconds();
	        
        	while(true) {
	            Move move = client.receiveMove(); //Man bekommt auch den eigenen Zug
	            if (move == null) {
	                //ich bin dran
	            	int x=0, y=0;
	            	Move lastmove = new Move(client.getMyPlayerNumber(), x,y);
	            	switch( client.getMyPlayerNumber()){
	            		case 1:
			                client.sendMove(KoordHelper.rotate(1,lastmove));
			                break;
		            	case 2:
			                client.sendMove(KoordHelper.rotate(2,lastmove));
			                break;
		            	case 3:
			                client.sendMove(KoordHelper.rotate(3,lastmove));
			                break;
		            	case 0: 
		            		client.sendMove(lastmove);
		            		break;
	            	}
	                //baue Zug in meine spielfeldrepräsentation ein
	            	}
	            
        	}
    	}catch(Exception e) {
	    		System.out.println(e.getLocalizedMessage());
    	}
	}
	
}
